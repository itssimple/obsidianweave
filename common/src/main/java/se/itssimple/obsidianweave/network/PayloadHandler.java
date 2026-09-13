package se.itssimple.obsidianweave.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Receives a payload of type {@code T} together with its {@link PayloadContext}.
 * @param <T> The payload type.
 */
@FunctionalInterface
public interface PayloadHandler<T extends CustomPacketPayload> {
    /**
     * Handles a received payload.
     * @param payload The payload.
     * @param context The receive context.
     */
    void handle(T payload, PayloadContext context);
}
