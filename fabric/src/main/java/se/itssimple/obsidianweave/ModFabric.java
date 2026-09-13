package se.itssimple.obsidianweave;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import se.itssimple.obsidianweave.event.ModEvents;

/**
 * The main Fabric mod class for Obsidian Weave.
 * Handles mod initialization and forwards Fabric server events into {@link ModEvents}.
 */
public class ModFabric implements ModInitializer {

    /** Default constructor */
    public ModFabric() {}

    /**
     * Called when the Fabric mod is initialized.
     * Initializes common mod logic and registers server event forwarding.
     */
    @Override
    public void onInitialize() {
        ModCommon.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> ModEvents.SERVER_STARTED.fire(l -> l.accept(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ModCommon.saveAllModsConfigs();
            ModEvents.SERVER_STOPPING.fire(l -> l.accept(server));
        });
        // Fabric fires SYNC_DATA_PACK_CONTENTS per player on /reload; END_DATA_PACK_RELOAD gives the single
        // "everything rebuilt" moment that Forge/NeoForge express with a null player.
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                ModEvents.DATAPACK_SYNC.fire(l -> l.onDatapackSync(server, null));
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                ModEvents.DATAPACK_SYNC.fire(l -> l.onDatapackSync(player.level().getServer(), player));
            }
        });
        ServerPlayerEvents.JOIN.register(player -> ModEvents.PLAYER_JOIN.fire(l -> l.accept(player)));
        ServerPlayerEvents.LEAVE.register(player -> ModEvents.PLAYER_LEAVE.fire(l -> l.accept(player)));
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                ModEvents.REGISTER_COMMANDS.fire(l -> l.register(dispatcher, context, selection)));
    }
}
