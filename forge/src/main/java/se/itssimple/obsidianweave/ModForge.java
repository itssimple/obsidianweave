package se.itssimple.obsidianweave;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.itssimple.obsidianweave.forge.platform.ForgeItemHelper;
import se.itssimple.obsidianweave.forge.platform.ForgePlatformHelper;
import se.itssimple.obsidianweave.services.Services;
import se.itssimple.obsidianweave.util.Reference;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class ModForge {

	public ModForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(ForgePlatformHelper::onModConfigEvent);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ModCommon.init();
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		ModCommon.saveAllModsConfigs();
	}
}