package se.itssimple.obsidianweave.data;

import se.itssimple.obsidianweave.ModCommon;

import java.util.function.Supplier;

public class ConfigEntry<T> {
    private final String key;
    private final Supplier<T> defaultValueSupplier;
    private final Class<T> type;

    private String nameLangKey;
    private String commentLangKey;

    private Object platformBinding;
    private T internalCachedValue;

    public ConfigEntry(String key, Supplier<T> defaultValueSupplier, Class<T> type)
    {
        this.key = key;
        this.defaultValueSupplier = defaultValueSupplier;
        this.type = type;

        this.internalCachedValue = defaultValueSupplier.get();
    }

    public String getKey()
    {
        return key;
    }

    public Supplier<T> getDefaultValueSupplier()
    {
        return defaultValueSupplier;
    }

    public Class<T> getType()
    {
        return type;
    }

    public T getDefaultValue()
    {
        return defaultValueSupplier.get();
    }

    public String getNameLangKey() { return this.nameLangKey == null ? "config.entry." + key : this.nameLangKey; }
    public ConfigEntry<T> setNameLangKey(String nameLangKey) {
        this.nameLangKey = nameLangKey;
        return this;
    }

    public String getCommentLangKey() { return this.commentLangKey == null ? getNameLangKey() + ".tooltip" : this.commentLangKey; }
    public ConfigEntry<T> setCommentLangKey(String commentLangKey) {
        this.commentLangKey = commentLangKey;
        return this;
    }

    public T getValue() {
        return ModCommon.getPlatformConfigHelper().getValue(this);
    }

    public void setValue(T value) {
        ModCommon.getPlatformConfigHelper().setValue(this, value);
    }

    public T getInternalCachedValue() {
        return internalCachedValue;
    }

    public void setInternalCachedValue(T internalCachedValue) {
        this.internalCachedValue = internalCachedValue;
    }

    public Object getPlatformBinding() {
        return platformBinding;
    }

    public void setPlatformBinding(Object platformBinding) {
        this.platformBinding = platformBinding;
    }
}
