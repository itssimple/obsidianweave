package se.itssimple.obsidianweave.forge.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Client-only accessors, kept in a separate class so the dedicated server never loads client classes.
 */
final class ForgeClientNetwork {
    private ForgeClientNetwork() {}

    static Player localPlayer() {
        return Minecraft.getInstance().player;
    }
}
