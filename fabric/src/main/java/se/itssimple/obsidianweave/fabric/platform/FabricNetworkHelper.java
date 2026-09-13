package se.itssimple.obsidianweave.fabric.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import se.itssimple.obsidianweave.network.PayloadContext;
import se.itssimple.obsidianweave.network.PayloadHandler;
import se.itssimple.obsidianweave.platform.IPlatformNetworkHelper;

import java.util.function.Supplier;

/**
 * Fabric implementation of the network helper. Registrations are applied immediately.
 */
public class FabricNetworkHelper implements IPlatformNetworkHelper {
    /** Default constructor */
    public FabricNetworkHelper() {}

    private static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type,
                                                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                                    Supplier<PayloadHandler<T>> clientHandler) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
        if (isClient()) {
            FabricClientNetwork.registerReceiver(type, clientHandler.get());
        }
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type,
                                                                    StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                                    PayloadHandler<T> serverHandler) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> serverHandler.handle(payload, new PayloadContext() {
            @Override
            public Player player() {
                return ctx.player();
            }

            @Override
            public boolean isClientSide() {
                return false;
            }

            @Override
            public void execute(Runnable task) {
                ctx.server().execute(task);
            }
        }));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        FabricClientNetwork.sendToServer(payload);
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return ServerPlayNetworking.canSend(player, type);
    }
}
