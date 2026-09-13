package se.itssimple.obsidianweave.forge.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.ConnectionStartEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.payload.PayloadProtocol;
import se.itssimple.obsidianweave.data.Constants;
import se.itssimple.obsidianweave.network.PayloadContext;
import se.itssimple.obsidianweave.network.PayloadHandler;
import se.itssimple.obsidianweave.platform.IPlatformNetworkHelper;
import se.itssimple.obsidianweave.util.Reference;

import java.util.function.Supplier;

/**
 * Forge implementation of the network helper.
 * All payloads from all Obsidian Weave mods share one optional payload channel. Payload types are
 * registered with Forge immediately; the channel itself is built lazily on the first connection,
 * which is after every mod has finished loading.
 */
public class ForgeNetworkHelper implements IPlatformNetworkHelper {
    /** The shared channel name. */
    private static final Identifier CHANNEL_NAME = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "main");
    /** Play-protocol builder that every registration is added to. */
    private static final PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> PLAY =
            ChannelBuilder.named(CHANNEL_NAME).optional().payloadChannel().play();
    /** Number of payloads registered so far; used for logging and to know whether a build is needed. */
    private static int registered = 0;
    /** The built channel, or null until the first connection starts. */
    private static Channel<CustomPacketPayload> channel;

    /** Default constructor */
    public ForgeNetworkHelper() {}

    /**
     * Hooks the lazy channel build to connection start. Called from {@code ModForge}.
     */
    public static void init() {
        ConnectionStartEvent.BUS.addListener(event -> ensureBuilt());
    }

    private static synchronized Channel<CustomPacketPayload> ensureBuilt() {
        if (channel == null) {
            channel = PLAY.bidirectional().build();
            Constants.LOG.info("Built network channel {} with {} payloads", CHANNEL_NAME, registered);
        }
        return channel;
    }

    private static void checkNotBuilt() {
        if (channel != null) {
            throw new IllegalStateException("[Obsidian Weave] Payloads must be registered before the first network connection");
        }
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type,
                                                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                                    Supplier<PayloadHandler<T>> clientHandler) {
        checkNotBuilt();
        if (FMLEnvironment.dist.isClient()) {
            PayloadHandler<T> handler = clientHandler.get();
            PLAY.clientbound().addMain(type, narrow(codec), (payload, ctx) -> handler.handle(payload, wrap(ctx)));
        } else {
            PLAY.clientbound().add(type, narrow(codec), (payload, ctx) -> ctx.setPacketHandled(true));
        }
        registered++;
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type,
                                                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                                    PayloadHandler<T> serverHandler) {
        checkNotBuilt();
        PLAY.serverbound().addMain(type, narrow(codec), (payload, ctx) -> serverHandler.handle(payload, wrap(ctx)));
        registered++;
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ensureBuilt().send(payload, PacketDistributor.PLAYER.with(player));
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ensureBuilt().send(payload, PacketDistributor.SERVER.noArg());
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return ensureBuilt().isRemotePresent(player.connection.getConnection());
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> StreamCodec<RegistryFriendlyByteBuf, T> narrow(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        // A codec that accepts any supertype of RegistryFriendlyByteBuf trivially accepts RegistryFriendlyByteBuf.
        return (StreamCodec<RegistryFriendlyByteBuf, T>) codec;
    }

    private static PayloadContext wrap(CustomPayloadEvent.Context ctx) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return ctx.isClientSide() ? ForgeClientNetwork.localPlayer() : ctx.getSender();
            }

            @Override
            public boolean isClientSide() {
                return ctx.isClientSide();
            }

            @Override
            public void execute(Runnable task) {
                ctx.enqueueWork(task);
            }
        };
    }
}
