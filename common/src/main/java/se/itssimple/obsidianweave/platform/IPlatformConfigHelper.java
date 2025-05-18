package se.itssimple.obsidianweave.platform;

import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;

public interface IPlatformConfigHelper {
    void initialize(String modId, String configSubDirectory);
    void register(ConfigHolder holder);
    void load(ConfigHolder holder);
    void save(ConfigHolder holder);

    <T> T getValue(ConfigEntry<T> entry);
    <T> void setValue(ConfigEntry<T> entry, T value);
}
