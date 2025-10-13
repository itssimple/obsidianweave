package se.itssimple.obsidianweave.platform;

import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;

/**
 * Platform abstraction for config management in Obsidian Weave mods.
 * Implemented by platform-specific helpers for Fabric and Forge.
 */
public interface IPlatformConfigHelper {
    /**
     * Registers a config holder with the platform.
     * @param holder The config holder.
     */
    void register(ConfigHolder holder);

    /**
     * Loads the config for the given holder from disk.
     * @param holder The config holder.
     */
    void load(ConfigHolder holder);

    /**
     * Saves the config for the given holder to disk.
     * @param holder The config holder.
     */
    void save(ConfigHolder holder);

    /**
     * Gets the value of a config entry.
     * @param entry The config entry.
     * @param <T> The value type.
     * @return The value of the config entry.
     */
    <T> T getValue(ConfigEntry<T> entry);

    /**
     * Sets the value of a config entry.
     * @param entry The config entry.
     * @param value The value to set.
     * @param <T> The value type.
     */
    <T> void setValue(ConfigEntry<T> entry, T value);
}
