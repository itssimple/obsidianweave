package se.itssimple.obsidianweave.data;

import java.util.ArrayList;
import java.util.List;

public class ConfigBuilder {
    private final List<ConfigEntry<?>> entries = new ArrayList<>();
    private final String modId;

    public ConfigBuilder(String modId) {
        this.modId = modId;
    }

    public <T> ConfigEntry<T> add(ConfigEntry<T> entry)
    {
        entries.add(entry);
        return entry;
    }

    public List<ConfigEntry<?>> getEntries()
    {
        return entries;
    }

    private String getNameLangKey(String key)
    {
        return "config." + modId + ".entry." + key;
    }

    private String getCommentLangKey(String key)
    {
        return getNameLangKey(key) + ".tooltip";
    }

    public ConfigEntry<Boolean> define(String key, boolean defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<Boolean> entry = new ConfigEntry<>(key, () -> defaultValue, Boolean.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    public ConfigEntry<Boolean> define(String key, boolean defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }

    public ConfigEntry<String> define(String key, String defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<String> entry = new ConfigEntry<>(key, () -> defaultValue, String.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    public ConfigEntry<String> define(String key, String defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }

    public ConfigEntry<Integer> define(String key, Integer defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<Integer> entry = new ConfigEntry<>(key, () -> defaultValue, Integer.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    public ConfigEntry<Integer> define(String key, Integer defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }

    public ConfigEntry<Double> define(String key, Double defaultValue, String nameLangKey, String commentLangKey)
    {
        ConfigEntry<Double> entry = new ConfigEntry<>(key, () -> defaultValue, Double.class)
                .setNameLangKey(nameLangKey)
                .setCommentLangKey(commentLangKey);

        return add(entry);
    }

    public ConfigEntry<Double> define(String key, Double defaultValue) {
        return define(
                key,
                defaultValue,
                getNameLangKey(key),
                getCommentLangKey(key)
        );
    }
}
