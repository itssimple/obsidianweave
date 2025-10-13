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

/**
 * The main Forge mod class for Obsidian Weave.
 * Handles mod initialization and server events for the Forge platform.
 */
@Mod(Reference.MOD_ID)
public class ModForge {

    /**
     * Constructs the Forge mod and registers event listeners.
     */
    public ModForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(ForgePlatformHelper::onModConfigEvent);
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