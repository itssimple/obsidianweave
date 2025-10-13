package se.itssimple.obsidianweave;

import se.itssimple.obsidianweave.data.ConfigBuilder;
import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;
import se.itssimple.obsidianweave.data.Constants;
import se.itssimple.obsidianweave.platform.IPlatformConfigHelper;
import se.itssimple.obsidianweave.services.Services;
import se.itssimple.obsidianweave.util.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * Common logic and configuration management for Obsidian Weave mods.
 * Handles config registration, initialization, and saving for all supported platforms.
 */
public class ModCommon {
    /**
     * Map of registered configs by mod ID.
     */
    private static final Map<String, ConfigHolder> REGISTERED_CONFIGS = new HashMap<>();

    /**
     * Enables debug logging for config operations.
     */
    public static Boolean CONFIG_DEBUG_LOGGING = true;

    /**
     * Initializes the mod and logs startup information.
     */
    public static void init() {
        Constants.LOG.info("Loading {} (ID: {}), version {}", Reference.NAME, Reference.MOD_ID, Reference.VERSION);
    }

    /**
     * Registers a config for the given mod ID using the provided configurator.
     * @param modId The mod ID.
     * @param configurator The config builder consumer.
     * @return The registered config holder.
     */
    public static ConfigHolder registerConfig(String modId, Consumer<ConfigBuilder> configurator) {
        if (REGISTERED_CONFIGS.containsKey(modId)) {
            Constants.LOG.error("Config for mod {} is already registered!", modId);
            throw new IllegalStateException("Config for mod " + modId + " is already registered!");
        }

        ConfigBuilder builder = new ConfigBuilder(modId);

        configurator.accept(builder);

        ConfigHolder holder = new ConfigHolder(modId, builder.getEntries());

        Services.PLATFORMHELPER.register(holder);
        Services.PLATFORMHELPER.load(holder);

        REGISTERED_CONFIGS.put(modId, holder);

        Constants.LOG.info("Registered config for mod {} with {} entries", modId, holder.getAllEntries().size());
        return holder;
    }

    /**
     * Gets all registered configs.
     * @return Map of mod IDs to config holders.
     */
    public static Map<String, ConfigHolder> getAllConfigs() {
        return REGISTERED_CONFIGS;
    }

    /**
     * Gets the config holder for the given mod ID, if present.
     * @param modId The mod ID.
     * @return Optional config holder.
     */
    public static Optional<ConfigHolder> getConfig(String modId) {
        return Optional.ofNullable(REGISTERED_CONFIGS.get(modId));
    }

    /**
     * Saves all registered mod configs.
     */
    public static void saveAllModsConfigs() {
        REGISTERED_CONFIGS.values().forEach(Services.PLATFORMHELPER::save);
        Constants.LOG.info("Saved all mods configs");
    }
}