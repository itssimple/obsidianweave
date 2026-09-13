package se.itssimple.obsidianweave;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.itssimple.obsidianweave.event.ModEvents;
import se.itssimple.obsidianweave.forge.platform.ForgeNetworkHelper;
import se.itssimple.obsidianweave.util.Reference;
import net.minecraftforge.fml.common.Mod;

/**
 * The main Forge mod class for Obsidian Weave.
 * Handles mod initialization, network channel setup and forwards server events into {@link ModEvents}.
 */
@Mod(Reference.MOD_ID)
public class ModForge {

    /** The mod instance. */
    public static ModForge instance;
    /** The Forge mod loading context. */
    public FMLJavaModLoadingContext loadingContext;

    /**
     * Constructs the Forge mod and registers event listeners.
     * @param modLoadingContext The mod loading context from Forge
     */
    public ModForge(FMLJavaModLoadingContext modLoadingContext) {
        instance = this;
        loadingContext = modLoadingContext;
        // Config registration listens on each owning mod's bus group from ForgePlatformHelper.register.
        ForgeNetworkHelper.init();
        // From the constructor, not load-complete: common configs are read in CONFIG_LOAD, right after
        // construction, so anything registered later keeps its defaults. Mods using Obsidian Weave should do the same.
        ModCommon.init();

        ServerStartedEvent.BUS.addListener(event -> ModEvents.SERVER_STARTED.fire(l -> l.accept(event.getServer())));
        ServerStoppingEvent.BUS.addListener(event -> {
            ModCommon.saveAllModsConfigs();
            ModEvents.SERVER_STOPPING.fire(l -> l.accept(event.getServer()));
        });
        OnDatapackSyncEvent.BUS.addListener(event -> {
            ServerPlayer player = event.getPlayer();
            ModEvents.DATAPACK_SYNC.fire(l -> l.onDatapackSync(event.getPlayerList().getServer(), player));
        });
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                ModEvents.PLAYER_JOIN.fire(l -> l.accept(player));
            }
        });
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                ModEvents.PLAYER_LEAVE.fire(l -> l.accept(player));
            }
        });
        RegisterCommandsEvent.BUS.addListener(event ->
                ModEvents.REGISTER_COMMANDS.fire(l -> l.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection())));
    }
}
