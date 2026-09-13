package se.itssimple.obsidianweave.network;

import net.minecraft.world.entity.player.Player;

/**
 * Loader-agnostic context handed to a {@link PayloadHandler} when a payload is received.
 * On the server the player is a {@code ServerPlayer}; on the client it is the local player.
 */
public interface PayloadContext {
    /**
     * Gets the player associated with the connection the payload arrived on.
     * @return The player (a {@code ServerPlayer} on the server, a {@code LocalPlayer} on the client).
     */
    Player player();

    /**
     * Whether this payload was received on the client side.
     * @return True on the client, false on the server.
     */
    boolean isClientSide();

    /**
     * Schedules work on the main game thread of the receiving side.
     * Handlers run on the network thread on some loaders, so any world/player mutation must go through here.
     * @param task The task to run on the main thread.
     */
    void execute(Runnable task);
}
