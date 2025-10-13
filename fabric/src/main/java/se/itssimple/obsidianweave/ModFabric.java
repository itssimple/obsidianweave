package se.itssimple.obsidianweave;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * The main Fabric mod class for Obsidian Weave.
 * Handles mod initialization and server events for the Fabric platform.
 */
public class ModFabric implements ModInitializer {

    /**
     * Called when the Fabric mod is initialized.
     * Initializes common mod logic and registers server stop event.
     */
    @Override
    public void onInitialize() {
        ModCommon.init();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ModCommon.saveAllModsConfigs());
    }
}