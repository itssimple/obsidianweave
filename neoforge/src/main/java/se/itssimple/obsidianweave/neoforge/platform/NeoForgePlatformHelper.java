package se.itssimple.obsidianweave.neoforge.platform;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
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
 * NeoForge-specific implementation of platform config helper for Obsidian Weave.
 * Handles registration and management of mod configuration using NeoForge's config system.
 */
public class NeoForgePlatformHelper implements IPlatformConfigHelper {
    /**
     * Maps mod IDs to their config holders.
     */
    private static final Map<String, ConfigHolder> modIdToHolderMap = new HashMap<>();
    /**
     * Maps mod IDs to their NeoForge config specs.
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

        // The config must belong to the mod that owns it: NeoForge posts ModConfigEvent on the owning mod's bus,
        // so the value sync has to listen there, not on Obsidian Weave's. Resolve the container by id rather than
        // trusting the active container, so this also works when called outside the mod's own constructor.
        ModContainer container = ModList.get().getModContainerById(holder.getModId())
                .map(c -> (ModContainer) c)
                .orElseGet(() -> ModLoadingContext.get().getActiveContainer());
        container.registerConfig(ModConfig.Type.LOCAL, spec, holder.getModId() + "-common.toml");
        IEventBus bus = container.getEventBus();
        if (bus != null) {
            bus.addListener(NeoForgePlatformHelper::onModConfigEvent);
        } else {
            Constants.LOG.warn("Mod {} has no event bus; its config values will stay at their defaults", holder.getModId());
        }
        if(ModCommon.CONFIG_DEBUG_LOGGING) {
            Constants.LOG.info("Config registered for mod {}", holder.getModId());
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
        ModConfigSpec spec = modIdToSpecMap.get(holder.getModId());
        if(!spec.isLoaded())
            return;

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
        if (spec != null && !spec.isLoaded()) {
            // Common configs are read once, right after registry initialisation. Anything registered later is never
            // loaded from disk, never written, and silently keeps its defaults.
            Constants.LOG.warn("Config for mod {} was never loaded from disk; on NeoForge, registerConfig must run from the mod constructor", holder.getModId());
        }
        // Otherwise NeoForge writes the file itself.
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
                Constants.LOG.debug("NeoForge config event for {}: {}, syncing values.", modId, event.getClass().getSimpleName());
                ConfigHolder holder = modIdToHolderMap.get(modId);
                if (holder != null) {
                    syncValuesFromForge(holder);
                }
            }
        }
    }
}
