package se.itssimple.obsidianweave.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for mod configuration entries in Obsidian Weave.
 * Used to define and collect config entries for a specific mod.
 */
public class ConfigBuilder {
    /** List of config entries for the mod. */
    private final List<ConfigEntry<?>> entries = new ArrayList<>();
    /** The mod ID for which configs are being built. */
    private final String modId;

    /**
     * Constructs a config builder for the given mod ID.
     * @param modId The mod ID.
     */
    public ConfigBuilder(String modId) {
        this.modId = modId;
    }

    /**
     * Adds a config entry to the builder.
     * @param entry The config entry to add.
     * @param <T> The entry type.
     * @return The added config entry.
     */
    public <T> ConfigEntry<T> add(ConfigEntry<T> entry)
    {
        entries.add(entry);
        return entry;
    }

    /**
     * Gets all config entries added to the builder.
     * @return List of config entries.
     */
    public List<ConfigEntry<?>> getEntries()
    {
        return entries;
    }

    /**
     * Gets the language key for the config entry name.
     * @param key The config key.
     * @return The language key for the entry name.
     */
    private String getNameLangKey(String key)
    {
        return "config." + modId + ".entry." + key;
    }

    /**
     * Gets the language key for the config entry comment/tooltip.
     * @param key The config key.
     * @return The language key for the entry comment.
     */
    private String getCommentLangKey(String key)
    {
        return getNameLangKey(key) + ".tooltip";
    }

    /**
     * Defines a boolean config entry with custom language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @param nameLangKey The language key for the name.
     * @param commentLangKey The language key for the comment.
     * @return The defined config entry.
     */
    public ConfigEntry<Boolean> define(String key, boolean defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<Boolean> entry = new ConfigEntry<>(key, () -> defaultValue, Boolean.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    /**
     * Defines a boolean config entry with auto-generated language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @return The defined config entry.
     */
    public ConfigEntry<Boolean> define(String key, boolean defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }

    /**
     * Defines a string config entry with custom language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @param nameLangKey The language key for the name.
     * @param commentLangKey The language key for the comment.
     * @return The defined config entry.
     */
    public ConfigEntry<String> define(String key, String defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<String> entry = new ConfigEntry<>(key, () -> defaultValue, String.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    /**
     * Defines a string config entry with auto-generated language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @return The defined config entry.
     */
    public ConfigEntry<String> define(String key, String defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }

    /**
     * Defines an integer config entry with custom language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @param nameLangKey The language key for the name.
     * @param commentLangKey The language key for the comment.
     * @return The defined config entry.
     */
    public ConfigEntry<Integer> define(String key, Integer defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<Integer> entry = new ConfigEntry<>(key, () -> defaultValue, Integer.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    /**
     * Defines an integer config entry with auto-generated language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @return The defined config entry.
     */
    public ConfigEntry<Integer> define(String key, Integer defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }

    /**
     * Defines a double config entry with custom language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @param nameLangKey The language key for the name.
     * @param commentLangKey The language key for the comment.
     * @return The defined config entry.
     */
    public ConfigEntry<Double> define(String key, Double defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<Double> entry = new ConfigEntry<>(key, () -> defaultValue, Double.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    /**
     * Defines a double config entry with auto-generated language keys.
     * @param key The config key.
     * @param defaultValue The default value.
     * @return The defined config entry.
     */
    public ConfigEntry<Double> define(String key, Double defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }
}
