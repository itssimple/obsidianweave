package se.itssimple.obsidianweave;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.minecraft.server.level.ServerPlayer;
import se.itssimple.obsidianweave.event.ModEvents;
import se.itssimple.obsidianweave.neoforge.platform.NeoForgeNetworkHelper;
import se.itssimple.obsidianweave.neoforge.platform.NeoForgePlatformHelper;
import se.itssimple.obsidianweave.util.Reference;

/**
 * The main NeoForge mod class for Obsidian Weave.
 * Handles mod initialization, network registration and forwards server events into {@link ModEvents}.
 */
@Mod(Reference.MOD_ID)
public class ModNeoForge {

    /**
     * Constructs the NeoForge mod and registers event listeners.
     * @param eventBus The mod event bus.
     * @param modContainer The mod container.
     */
    public ModNeoForge(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(NeoForgePlatformHelper::onModConfigEvent);
        eventBus.addListener(NeoForgeNetworkHelper::onRegisterPayloadHandlers);
        NeoForge.EVENT_BUS.register(this);
    }

    /**
     * Called when NeoForge has completed loading mods.
     * @param event The load complete event.
     */
    private void loadComplete(final FMLLoadCompleteEvent event) {
        ModCommon.init();
    }

    /**
     * Forwards server started.
     * @param event The event.
     */
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        ModEvents.SERVER_STARTED.fire(l -> l.accept(event.getServer()));
    }

    /**
     * Called when the server is stopping. Saves all mod configs and forwards the event.
     * @param event The server stopping event.
     */
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ModCommon.saveAllModsConfigs();
        ModEvents.SERVER_STOPPING.fire(l -> l.accept(event.getServer()));
    }

    /**
     * Forwards datapack sync (player is null after /reload).
     * @param event The event.
     */
    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        ModEvents.DATAPACK_SYNC.fire(l -> l.onDatapackSync(event.getPlayerList().getServer(), player));
    }

    /**
     * Forwards player join.
     * @param event The event.
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModEvents.PLAYER_JOIN.fire(l -> l.accept(player));
        }
    }

    /**
     * Forwards player leave.
     * @param event The event.
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModEvents.PLAYER_LEAVE.fire(l -> l.accept(player));
        }
    }

    /**
     * Forwards command registration.
     * @param event The event.
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModEvents.REGISTER_COMMANDS.fire(l -> l.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
    }
}
