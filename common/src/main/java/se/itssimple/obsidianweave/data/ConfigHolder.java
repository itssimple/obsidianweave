package se.itssimple.obsidianweave.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigHolder {
    private final String modId;
    private final Map<String, ConfigEntry<?>> configEntryMap;
    private final List<ConfigEntry<?>> configEntryList;

    public ConfigHolder(String modId, List<ConfigEntry<?>> configEntryList) {
        this.modId = modId;
        this.configEntryList = List.copyOf(configEntryList);
        this.configEntryMap = configEntryList.stream().collect(Collectors.toMap(ConfigEntry::getKey, Function.identity()));
    }

    public String getModId() {
        return modId;
    }

    public List<ConfigEntry<?>> getAllEntries() {
        return configEntryList;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<ConfigEntry<T>> getEntry(String key) {
        ConfigEntry<?> entry = configEntryMap.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of((ConfigEntry<T>) entry);
    }

    public boolean getBoolean(String key) {
        return getEntry(key).map(e -> (Boolean)e.getValue()).orElse(false);
    }

    public Integer getInteger(String key) {
        return getEntry(key).map(e -> (Integer)e.getValue()).orElse(0);
    }

    public String getString(String key) {
        return getEntry(key).map(e -> (String)e.getValue()).orElse("");
    }

    public Double getDouble(String key) {
        return getEntry(key).map(e -> (Double)e.getValue()).orElse(0.0);
    }
}
