package se.itssimple.obsidianweave.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import se.itssimple.obsidianweave.network.PayloadHandler;

import java.util.function.Supplier;

/**
 * Platform abstraction for custom payload networking in Obsidian Weave mods.
 * Implemented by platform-specific helpers for Fabric, Forge and NeoForge.
 * <p>
 * Payloads may be registered at any point during mod initialization, including from
 * {@code FMLLoadCompleteEvent} on Forge/NeoForge; each platform applies the registrations
 * at the moment its own network registry expects them.
 */
public interface IPlatformNetworkHelper {
    /**
     * Registers a payload sent from the server to clients.
     * The handler supplier is only invoked on the client, so it may safely reference client-only classes.
     * @param type The payload type.
     * @param codec The stream codec.
     * @param clientHandler Supplier of the client-side handler; never invoked on a dedicated server.
     * @param <T> The payload type.
     */
    <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type,
                                                             StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                             Supplier<PayloadHandler<T>> clientHandler);

    /**
     * Registers a payload sent from clients to the server.
     * @param type The payload type.
     * @param codec The stream codec.
     * @param serverHandler The server-side handler.
     * @param <T> The payload type.
     */
    <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type,
                                                             StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                             PayloadHandler<T> serverHandler);

    /**
     * Sends a payload to a specific player. Callers should check {@link #canSend} first when the
     * client may not have the mod installed.
     * @param player The target player.
     * @param payload The payload.
     */
    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    /**
     * Sends a payload from the client to the server. Client side only.
     * @param payload The payload.
     */
    void sendToServer(CustomPacketPayload payload);

    /**
     * Whether the given player's client has registered the given payload type, i.e. has the mod installed.
     * @param player The player.
     * @param type The payload type.
     * @return True if the payload can be sent to that player.
     */
    boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type);
}
