package se.itssimple.obsidianweave;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import se.itssimple.obsidianweave.neoforge.platform.NeoForgePlatformHelper;
import se.itssimple.obsidianweave.util.Reference;

/**
 * The main Forge mod class for Obsidian Weave.
 * Handles mod initialization and server events for the Forge platform.
 */
@Mod(Reference.MOD_ID)
public class ModNeoForge {

    /**
     * Constructs the Forge mod and registers event listeners.
     */
    public ModNeoForge(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(NeoForgePlatformHelper::onModConfigEvent);
    }

    /**
     * Called when Forge has completed loading mods.
     * @param event The load complete event.
     */
    private void loadComplete(final FMLLoadCompleteEvent event) {
        ModCommon.init();
    }

    /**
     * Called when the server is stopping. Saves all mod configs.
     * @param event The server stopping event.
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ModCommon.saveAllModsConfigs();
    }
}