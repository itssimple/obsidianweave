package se.itssimple.obsidianweave.neoforge.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import se.itssimple.obsidianweave.data.Constants;
import se.itssimple.obsidianweave.network.PayloadContext;
import se.itssimple.obsidianweave.network.PayloadHandler;
import se.itssimple.obsidianweave.platform.IPlatformNetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * NeoForge implementation of the network helper.
 * Registrations are collected until NeoForge fires {@link RegisterPayloadHandlersEvent}
 * (which happens after {@code FMLLoadCompleteEvent}), then applied in one go.
 */
public class NeoForgeNetworkHelper implements IPlatformNetworkHelper {
    /** Pending registrations, applied when the payload registrar becomes available. */
    private static final List<Consumer<PayloadRegistrar>> PENDING = new ArrayList<>();
    /** Set once the registrar event has fired; later registrations are an error. */
    private static boolean applied = false;

    /** Default constructor */
    public NeoForgeNetworkHelper() {}

    /**
     * Applies all collected registrations. Called from the mod bus listener in {@code ModNeoForge}.
     * @param event The NeoForge payload registration event.
     */
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        for (Consumer<PayloadRegistrar> pending : PENDING) {
            pending.accept(registrar);
        }
        Constants.LOG.info("Registered {} network payloads", PENDING.size());
        PENDING.clear();
        applied = true;
    }

    private static void enqueue(Consumer<PayloadRegistrar> registration) {
        if (applied) {
            throw new IllegalStateException("[Obsidian Weave] Payloads must be registered before RegisterPayloadHandlersEvent has fired");
        }
        PENDING.add(registration);
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type,
                                                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                                    Supplier<PayloadHandler<T>> clientHandler) {
        enqueue(registrar -> {
            if (FMLEnvironment.getDist().isClient()) {
                PayloadHandler<T> handler = clientHandler.get();
                registrar.playToClient(type, codec, (payload, ctx) -> handler.handle(payload, wrap(ctx, true)));
            } else {
                registrar.playToClient(type, codec);
            }
        });
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type,
                                                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                                    PayloadHandler<T> serverHandler) {
        enqueue(registrar -> registrar.playToServer(type, codec, (payload, ctx) -> serverHandler.handle(payload, wrap(ctx, false))));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        NeoForgeClientNetwork.sendToServer(payload);
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return player.connection.hasChannel(type);
    }

    private static PayloadContext wrap(IPayloadContext ctx, boolean clientSide) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return ctx.player();
            }

            @Override
            public boolean isClientSide() {
                return clientSide;
            }

            @Override
            public void execute(Runnable task) {
                ctx.enqueueWork(task);
            }
        };
    }
}
