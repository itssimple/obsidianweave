package se.itssimple.obsidianweave.neoforge.platform;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import se.itssimple.obsidianweave.ModCommon;
import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;
import se.itssimple.obsidianweave.data.Constants;
import se.itssimple.obsidianweave.platform.IPlatformConfigHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Forge-specific implementation of platform config helper for Obsidian Weave.
 * Handles registration and management of mod configuration using Forge's config system.
 */
public class NeoForgePlatformHelper implements IPlatformConfigHelper {
    /**
     * Maps mod IDs to their config holders.
     */
    private static final Map<String, ConfigHolder> modIdToHolderMap = new HashMap<>();
    /**
     * Maps mod IDs to their Forge config specs.
     */
    private static final Map<String, ModConfigSpec> modIdToSpecMap = new HashMap<>();
    /**
     * Registers a config holder and builds its Forge config spec.
     * @param holder The config holder to register.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void register(ConfigHolder holder) {
        if(ModCommon.CONFIG_DEBUG_LOGGING) {
            Constants.LOG.info("Registering config for mod {}", holder.getModId());
        }
        modIdToHolderMap.put(holder.getModId(), holder);

        ModConfigSpec.Builder specBuilder = new ModConfigSpec.Builder();

        for (ConfigEntry<?> entry : holder.getAllEntries()) {
            specBuilder.comment(entry.getCommentLangKey()) // Forge uses comment for tooltip
                    .translation(entry.getNameLangKey());

            Object forgeConfValue;
            if (entry.getType() == Boolean.class) {
                forgeConfValue = specBuilder.define(entry.getKey(), entry.getDefaultValueSupplier().get());
            } else if (entry.getType() == Integer.class) {
                ConfigEntry<Integer> intEntry = (ConfigEntry<Integer>) entry;
                forgeConfValue = specBuilder.define(entry.getKey(), intEntry.getDefaultValueSupplier().get());
            } else if (entry.getType() == Double.class) {
                ConfigEntry<Double> doubleEntry = (ConfigEntry<Double>) entry;
                forgeConfValue = specBuilder.define(entry.getKey(), doubleEntry.getDefaultValueSupplier().get());
            } else if (entry.getType() == String.class) {
                forgeConfValue = specBuilder.define(entry.getKey(), entry.getDefaultValueSupplier().get());
            } else {
                Constants.LOG.warn("Unsupported config entry type {} for key {} in mod {}", entry.getType(), entry.getKey(), holder.getModId());
                continue;
            }
            entry.setPlatformBinding(forgeConfValue);
        }

        ModConfigSpec spec = specBuilder.build();
        modIdToSpecMap.put(holder.getModId(), spec);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, spec, holder.getModId() + "-common.toml");
        if(ModCommon.CONFIG_DEBUG_LOGGING) {
            Constants.LOG.info("Config loaded for mod {}", holder.getModId());
        }
    }

    /**
     * Loads the configuration values for the given holder from Forge.
     * @param holder The config holder to load values for.
     */
    @Override
    public void load(ConfigHolder holder) {
        syncValuesFromForge(holder);
    }

    @SuppressWarnings("unchecked")
    private static <T> void syncValuesFromForge(ConfigHolder holder)
    {
        for(ConfigEntry<?> entry : holder.getAllEntries()) {
            if(entry.getPlatformBinding() instanceof ModConfigSpec.ConfigValue) {
                var value = ((ModConfigSpec.ConfigValue<T>) entry.getPlatformBinding()).get();
                setValueUnsafe(entry, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void setValueUnsafe(ConfigEntry<?> rawEntry, T value) {
        ConfigEntry<T> entry = (ConfigEntry<T>) rawEntry;
        entry.setInternalCachedValue(value);
    }

    /**
     * Saves the configuration for the given holder.
     * @param holder The config holder to save.
     */
    @Override
    public void save(ConfigHolder holder) {
        ModConfigSpec spec = modIdToSpecMap.get(holder.getModId());
        if(spec != null && spec.isLoaded()) {
            // Forge handles this automatically
        }
    }

    /**
     * Gets the value of a config entry, using the platform binding if available.
     * @param entry The config entry to get the value from.
     * @param <T> The type of the config entry value.
     * @return The value of the config entry.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(ConfigEntry<T> entry) {
        if(entry.getPlatformBinding() instanceof ModConfigSpec.ConfigValue) {
            return ((ModConfigSpec.ConfigValue<T>) entry.getPlatformBinding()).get();
        }

        return entry.getDefaultValue();
    }

    /**
     * Sets the value of a config entry, updating the platform binding if necessary.
     * @param entry The config entry to set the value for.
     * @param value The value to set.
     * @param <T> The type of the config entry value.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> void setValue(ConfigEntry<T> entry, T value) {
        if(entry.getPlatformBinding() instanceof ModConfigSpec.ConfigValue) {
            ModConfigSpec.ConfigValue<T> configValue = (ModConfigSpec.ConfigValue<T>) entry.getPlatformBinding();
            configValue.set(value);
        }
    }

    /**
     * Handles mod config events, syncing values for the affected mod.
     * @param event The config event to handle.
     */
    public static void onModConfigEvent(final ModConfigEvent event) {
        ModConfig config = event.getConfig();
        for (Map.Entry<String, ModConfigSpec> entry : modIdToSpecMap.entrySet()) {
            String modId = entry.getKey();
            ModConfigSpec spec = entry.getValue();
            if (spec == config.getSpec()) {
                Constants.LOG.debug("Forge config event for {}: {}, syncing values.", modId, event.getClass().getSimpleName());
                ConfigHolder holder = modIdToHolderMap.get(modId);
                if (holder != null) {
                    syncValuesFromForge(holder);
                }
            }
        }
    }
}
