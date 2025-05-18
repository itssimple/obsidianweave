package se.itssimple.obsidianweave;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ModFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ModCommon.init();

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> ModCommon.saveAllModsConfigs());
	}
}