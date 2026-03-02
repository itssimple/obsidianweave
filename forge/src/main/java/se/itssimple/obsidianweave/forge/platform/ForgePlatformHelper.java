package se.itssimple.obsidianweave.forge.platform;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import se.itssimple.obsidianweave.ModCommon;
import se.itssimple.obsidianweave.ModForge;
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
public class ForgePlatformHelper implements IPlatformConfigHelper {
    /**
     * Maps mod IDs to their config holders.
     */
    private static final Map<String, ConfigHolder> modIdToHolderMap = new HashMap<>();
    /**
     * Maps mod IDs to their Forge config specs.
     */
    private static final Map<String, ForgeConfigSpec> modIdToSpecMap = new HashMap<>();

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

        ForgeConfigSpec.Builder specBuilder = new ForgeConfigSpec.Builder();

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

        ForgeConfigSpec spec = specBuilder.build();
        modIdToSpecMap.put(holder.getModId(), spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, holder.getModId() + "-common.toml");
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
        ForgeConfigSpec spec = modIdToSpecMap.get(holder.getModId());
        if(!spec.isLoaded())
            return;

        for(ConfigEntry<?> entry : holder.getAllEntries()) {
           if(entry.getPlatformBinding() instanceof ForgeConfigSpec.ConfigValue) {
               var value = ((ForgeConfigSpec.ConfigValue<T>) entry.getPlatformBinding()).get();
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
        ForgeConfigSpec spec = modIdToSpecMap.get(holder.getModId());
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
        return entry.getInternalCachedValue();
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
        if(entry.getPlatformBinding() instanceof ForgeConfigSpec.ConfigValue) {
            ForgeConfigSpec.ConfigValue<T> configValue = (ForgeConfigSpec.ConfigValue<T>) entry.getPlatformBinding();
            configValue.set(value);
        }
    }

    /**
     * Handles mod config events, syncing values for the affected mod.
     * @param event The config event to handle.
     */
    public static void onModConfigEvent(final ModConfigEvent event) {
        ModConfig config = event.getConfig();
        for (Map.Entry<String, ForgeConfigSpec> entry : modIdToSpecMap.entrySet()) {
            String modId = entry.getKey();
            ForgeConfigSpec spec = entry.getValue();
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
