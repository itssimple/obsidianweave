package se.itssimple.obsidianweave.data;

import se.itssimple.obsidianweave.ModCommon;
import se.itssimple.obsidianweave.services.Services;

import java.util.function.Supplier;

/**
 * Represents a single configuration entry for a mod in Obsidian Weave.
 * Stores key, type, default value, language keys, and platform-specific binding.
 * @param <T> The type of the config value.
 */
public class ConfigEntry<T> {
    /** The config key. */
    private final String key;
    /** Supplier for the default value. */
    private final Supplier<T> defaultValueSupplier;
    /** The type of the config value. */
    private final Class<T> type;

    /** Language key for the entry name. */
    private String nameLangKey;
    /** Language key for the entry comment/tooltip. */
    private String commentLangKey;

    /** Platform-specific binding for this entry. */
    private Object platformBinding;
    /** Internal cached value for this entry. */
    private T internalCachedValue;

    /**
     * Constructs a config entry with the given key, default value supplier, and type.
     * @param key The config key.
     * @param defaultValueSupplier The supplier for the default value.
     * @param type The type of the config value.
     */
    public ConfigEntry(String key, Supplier<T> defaultValueSupplier, Class<T> type)
    {
        this.key = key;
        this.defaultValueSupplier = defaultValueSupplier;
        this.type = type;

        this.internalCachedValue = defaultValueSupplier.get();
    }

    /**
     * Gets the config key.
     * @return The config key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Gets the supplier for the default value.
     * @return The default value supplier.
     */
    public Supplier<T> getDefaultValueSupplier()
    {
        return defaultValueSupplier;
    }

    /**
     * Gets the type of the config value.
     * @return The value type.
     */
    public Class<T> getType()
    {
        return type;
    }

    /**
     * Gets the default value for this entry.
     * @return The default value.
     */
    public T getDefaultValue()
    {
        return defaultValueSupplier.get();
    }

    /**
     * Gets the language key for the entry name.
     * @return The name language key.
     */
    public String getNameLangKey() { return this.nameLangKey == null ? "config.entry." + key : this.nameLangKey; }
    /**
     * Sets the language key for the entry name.
     * @param nameLangKey The name language key.
     * @return This config entry.
     */
    public ConfigEntry<T> setNameLangKey(String nameLangKey) {
        this.nameLangKey = nameLangKey;
        return this;
    }

    /**
     * Gets the language key for the entry comment/tooltip.
     * @return The comment language key.
     */
    public String getCommentLangKey() { return this.commentLangKey == null ? getNameLangKey() + ".tooltip" : this.commentLangKey; }
    /**
     * Sets the language key for the entry comment/tooltip.
     * @param commentLangKey The comment language key.
     * @return This config entry.
     */
    public ConfigEntry<T> setCommentLangKey(String commentLangKey) {
        this.commentLangKey = commentLangKey;
        return this;
    }

    /**
     * Gets the current value of this config entry from the platform helper.
     * @return The config value.
     */
    public T getValue() {
        return Services.PLATFORMHELPER.getValue(this);
    }

    /**
     * Sets the value of this config entry using the platform helper.
     * @param value The value to set.
     */
    public void setValue(T value) {
        Services.PLATFORMHELPER.setValue(this, value);
    }

    /**
     * Gets the internal cached value for this entry.
     * @return The cached value.
     */
    public T getInternalCachedValue() {
        return internalCachedValue;
    }

    /**
     * Sets the internal cached value for this entry.
     * @param internalCachedValue The value to cache.
     */
    public void setInternalCachedValue(T internalCachedValue) {
        this.internalCachedValue = internalCachedValue;
    }

    /**
     * Gets the platform-specific binding for this entry.
     * @return The platform binding object.
     */
    public Object getPlatformBinding() {
        return platformBinding;
    }

    /**
     * Sets the platform-specific binding for this entry.
     * @param platformBinding The platform binding object.
     */
    public void setPlatformBinding(Object platformBinding) {
        this.platformBinding = platformBinding;
    }
}
