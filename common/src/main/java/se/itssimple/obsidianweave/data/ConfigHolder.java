package se.itssimple.obsidianweave.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Holds configuration entries for a specific mod in Obsidian Weave.
 * Provides access to config values and entry lookup by key.
 */
public class ConfigHolder {
    /** The mod ID for this config holder. */
    private final String modId;
    /** Map of config keys to config entries. */
    private final Map<String, ConfigEntry<?>> configEntryMap;
    /** List of all config entries. */
    private final List<ConfigEntry<?>> configEntryList;

    /**
     * Constructs a config holder for the given mod ID and entries.
     * @param modId The mod ID.
     * @param configEntryList The list of config entries.
     */
    public ConfigHolder(String modId, List<ConfigEntry<?>> configEntryList) {
        this.modId = modId;
        this.configEntryList = List.copyOf(configEntryList);
        this.configEntryMap = configEntryList.stream().collect(Collectors.toMap(ConfigEntry::getKey, Function.identity()));
    }

    /**
     * Gets the mod ID for this config holder.
     * @return The mod ID.
     */
    public String getModId() {
        return modId;
    }

    /**
     * Gets all config entries for this mod.
     * @return List of config entries.
     */
    public List<ConfigEntry<?>> getAllEntries() {
        return configEntryList;
    }

    /**
     * Gets a config entry by key, if present.
     * @param key The config key.
     * @param <T> The entry type.
     * @return Optional config entry.
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<ConfigEntry<T>> getEntry(String key) {
        ConfigEntry<?> entry = configEntryMap.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of((ConfigEntry<T>) entry);
    }

    /**
     * Gets a boolean config value by key.
     * @param key The config key.
     * @return The boolean value, or false if not present.
     */
    public boolean getBoolean(String key) {
        return getEntry(key).map(e -> (Boolean)e.getValue()).orElse(false);
    }

    /**
     * Gets an integer config value by key.
     * @param key The config key.
     * @return The integer value, or 0 if not present.
     */
    public Integer getInteger(String key) {
        return getEntry(key).map(e -> (Integer)e.getValue()).orElse(0);
    }

    /**
     * Gets a string config value by key.
     * @param key The config key.
     * @return The string value, or empty string if not present.
     */
    public String getString(String key) {
        return getEntry(key).map(e -> (String)e.getValue()).orElse("");
    }

    /**
     * Gets a double config value by key.
     * @param key The config key.
     * @return The double value, or 0.0 if not present.
     */
    public Double getDouble(String key) {
        return getEntry(key).map(e -> (Double)e.getValue()).orElse(0.0);
    }
}
