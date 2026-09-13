package se.itssimple.obsidianweave.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Loader-agnostic lifecycle events. Mods register listeners from common code; each platform module
 * forwards the corresponding Fabric/Forge/NeoForge event into these hooks.
 */
public final class ModEvents {
    private ModEvents() {}

    /** Fired once the server has fully started (worlds loaded, ready for players). */
    public static final EventHook<Consumer<MinecraftServer>> SERVER_STARTED = new EventHook<>();

    /** Fired when the server begins shutting down. */
    public static final EventHook<Consumer<MinecraftServer>> SERVER_STOPPING = new EventHook<>();

    /**
     * Fired when datapack contents are synced to clients: once with a null player after a {@code /reload}
     * (all datapack-driven registries, including advancements, have been rebuilt), and once per player with
     * that player set when they join.
     */
    public static final EventHook<DatapackSync> DATAPACK_SYNC = new EventHook<>();

    /** Fired after a player has joined the server and is fully placed in the world. */
    public static final EventHook<Consumer<ServerPlayer>> PLAYER_JOIN = new EventHook<>();

    /** Fired when a player disconnects. */
    public static final EventHook<Consumer<ServerPlayer>> PLAYER_LEAVE = new EventHook<>();

    /** Fired when the command dispatcher is built; register commands here. */
    public static final EventHook<CommandRegistration> REGISTER_COMMANDS = new EventHook<>();

    /** Listener for {@link #DATAPACK_SYNC}. */
    @FunctionalInterface
    public interface DatapackSync {
        /**
         * Called on datapack sync.
         * @param server The server.
         * @param player The joining player, or null when the sync follows a {@code /reload}.
         */
        void onDatapackSync(MinecraftServer server, @Nullable ServerPlayer player);
    }

    /** Listener for {@link #REGISTER_COMMANDS}. */
    @FunctionalInterface
    public interface CommandRegistration {
        /**
         * Called when commands are being registered.
         * @param dispatcher The command dispatcher.
         * @param context The command build context (registry access).
         * @param selection Which side(s) the commands are being built for.
         */
        void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection);
    }
}
