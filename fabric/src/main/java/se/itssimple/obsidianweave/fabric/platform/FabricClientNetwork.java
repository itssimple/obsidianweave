package se.itssimple.obsidianweave.fabric.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import se.itssimple.obsidianweave.network.PayloadContext;
import se.itssimple.obsidianweave.network.PayloadHandler;

/**
 * Client-only networking calls, kept in a separate class so the dedicated server never loads client classes.
 */
final class FabricClientNetwork {
    private FabricClientNetwork() {}

    static <T extends CustomPacketPayload> void registerReceiver(CustomPacketPayload.Type<T> type, PayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> handler.handle(payload, new PayloadContext() {
            @Override
            public Player player() {
                return ctx.player();
            }

            @Override
            public boolean isClientSide() {
                return true;
            }

            @Override
            public void execute(Runnable task) {
                ctx.client().execute(task);
            }
        }));
    }

    static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
