package se.itssimple.obsidianweave.neoforge.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Client-only networking calls, kept in a separate class so the dedicated server never loads client classes.
 */
final class NeoForgeClientNetwork {
    private NeoForgeClientNetwork() {}

    static void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
