package se.itssimple.obsidianweave;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.itssimple.obsidianweave.forge.platform.ForgePlatformHelper;
import se.itssimple.obsidianweave.util.Reference;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class ModForge {

	public ModForge() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgePlatformHelper::onModConfigEvent);
	}

	private void init(final FMLClientSetupEvent event) {
		ModCommon.init();
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		ModCommon.saveAllModsConfigs();
	}
}