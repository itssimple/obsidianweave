package se.itssimple.obsidianweave.fabric.platform;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.language.I18n;
import se.itssimple.obsidianweave.ModCommon;
import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;
import se.itssimple.obsidianweave.data.Constants;
import se.itssimple.obsidianweave.platform.IPlatformConfigHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformConfigHelper {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private Path configRootPath;

    @Override
    public void initialize(String modId, String configSubDirectory) {
        if(ModCommon.CONFIG_DEBUG_LOGGING) {
            Constants.LOG.info("Initializing Fabric config for mod {}", modId);
        }
        this.configRootPath = FabricLoader.getInstance().getConfigDir().resolve(configSubDirectory);
        try {
            Files.createDirectories(configRootPath);
            Constants.LOG.info("Config directory: {}", configRootPath);
        } catch (IOException e) {
            Constants.LOG.error("Failed to create config directory: {}", configRootPath, e);
        }
    }

    private Path getConfigFilePath(ConfigHolder holder) {
        return configRootPath.resolve(holder.getModId() + ".json");
    }

    private String findModIdForEntry(ConfigEntry<?> entry) {
        for (ConfigHolder holder : ModCommon.getAllConfigs().values()) {
            if (holder.getEntry(entry.getKey()).isPresent()) {
                return holder.getModId();
            }
        }

        return null;
    }

    @Override
    public void register(ConfigHolder holder) { /* No-op in Fabric */ }

    @Override
    public void load(ConfigHolder holder) {
        if(ModCommon.CONFIG_DEBUG_LOGGING) {
            Constants.LOG.info("Loading config for mod {}", holder.getModId());
        }
        Path configPath = getConfigFilePath(holder);

        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json == null) return;

                for (ConfigEntry<?> entry : holder.getAllEntries()) {
                    if (json.has(entry.getKey())) {
                        JsonElement element = json.get(entry.getKey());
                        Object value = null;
                        try {
                            if (entry.getType() == Boolean.class && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
                                value = element.getAsBoolean();
                            } else if (entry.getType() == Integer.class && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                                value = element.getAsInt();
                            } else if (entry.getType() == Double.class && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                                value = element.getAsDouble();
                            } else if (entry.getType() == String.class && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                                value = element.getAsString();
                            }
                            if (value != null) {
                                setValueUnsafe(entry, value);
                            } else {
                                Constants.LOG.warn("Type mismatch or invalid type for config key '{}' in mod '{}'. Using default.", entry.getKey(), holder.getModId());
                                resetToDefaultUnsafe(entry);
                            }
                        } catch (Exception e) {
                            Constants.LOG.warn("Error parsing value for config key '{}' in mod '{}'. Using default. Error: {}", entry.getKey(), holder.getModId(), e.getMessage());
                            resetToDefaultUnsafe(entry);
                        }
                    } else {
                        resetToDefaultUnsafe(entry);
                    }
                }
            } catch (IOException e) {
                Constants.LOG.error("Failed to read config file: {}", configPath, e);
            }
        } else {
            holder.getAllEntries().forEach(this::resetToDefaultUnsafe);
            save(holder);
        }

        if(ModCommon.CONFIG_DEBUG_LOGGING) {
            Constants.LOG.info("Config loaded for mod {}", holder.getModId());
        }
    }

    @Override
    public void save(ConfigHolder holder) {
        Path configPath = getConfigFilePath(holder);
        JsonObject json = new JsonObject();

        for (ConfigEntry<?> entry : holder.getAllEntries()) {
            json.add("_comment_" + entry.getKey(), new JsonPrimitive("# " + I18n.get(entry.getCommentLangKey())));
            json.add("_type_" + entry.getKey(), new JsonPrimitive("# " + entry.getType()));

            Object value = entry.getInternalCachedValue();
            if (value instanceof Boolean) {
                json.addProperty(entry.getKey(), (Boolean) value);
            } else if (value instanceof Integer) {
                json.addProperty(entry.getKey(), (Integer) value);
            } else if (value instanceof Double) {
                json.addProperty(entry.getKey(), (Double) value);
            }else if (value instanceof String) {
                json.addProperty(entry.getKey(), (String) value);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config for {}: {}", holder.getModId(), configPath, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void setValueUnsafe(ConfigEntry<?> rawEntry, T value) {
        ConfigEntry<T> entry = (ConfigEntry<T>) rawEntry;
        entry.setInternalCachedValue(value);
    }

    private void resetToDefaultUnsafe(ConfigEntry<?> rawEntry) {
        setValueUnsafe(rawEntry, rawEntry.getDefaultValue());
    }

    @Override
    public <T> T getValue(ConfigEntry<T> entry) {
        return entry.getValue();
    }

    @Override
    public <T> void setValue(ConfigEntry<T> entry, T value) {
        entry.setInternalCachedValue(value);
        ModCommon.getConfig(findModIdForEntry(entry)).ifPresent(this::save);
    }
}
