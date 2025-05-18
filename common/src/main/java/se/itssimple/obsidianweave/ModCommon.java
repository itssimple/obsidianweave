package se.itssimple.obsidianweave;

import se.itssimple.obsidianweave.data.ConfigBuilder;
import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;
import se.itssimple.obsidianweave.data.Constants;
import se.itssimple.obsidianweave.platform.IPlatformConfigHelper;
import se.itssimple.obsidianweave.util.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class ModCommon {
	private static final Map<String, ConfigHolder> REGISTERED_CONFIGS = new HashMap<>();
	private static IPlatformConfigHelper platformConfigHelper;

	public static Boolean CONFIG_DEBUG_LOGGING = true;

	public static void init() {
		Constants.LOG.info("Loading {} (ID: {}), version {}", Reference.NAME, Reference.MOD_ID, Reference.VERSION);
		load();
	}

	private static void load() {
		platformConfigHelper = ServiceLoader.load(IPlatformConfigHelper.class).findFirst().orElseThrow(() -> {
			Constants.LOG.error("No platform config helper found!");
			return new RuntimeException("No platform config helper found!");
		});

		platformConfigHelper.initialize(Reference.MOD_ID, Reference.MOD_ID);
	}

	public static IPlatformConfigHelper getPlatformConfigHelper()
	{
		if(platformConfigHelper == null) {
			Constants.LOG.error("Platform config helper is not initialized!");
			throw new IllegalStateException("Platform config helper is not initialized!");
		} else {
			return platformConfigHelper;
		}
	}

	public static ConfigHolder registerConfig(String modId, Consumer<ConfigBuilder> configurator) {
		if (REGISTERED_CONFIGS.containsKey(modId)) {
			Constants.LOG.error("Config for mod {} is already registered!", modId);
			throw new IllegalStateException("Config for mod " + modId + " is already registered!");
		}

		ConfigBuilder builder = new ConfigBuilder(modId);

		configurator.accept(builder);

		ConfigHolder holder = new ConfigHolder(modId, builder.getEntries());

		platformConfigHelper.register(holder);
		platformConfigHelper.load(holder);

		REGISTERED_CONFIGS.put(modId, holder);

		Constants.LOG.info("Registered config for mod {} with {} entries", modId, holder.getAllEntries().size());
		return holder;
	}

	public static Map<String, ConfigHolder> getAllConfigs() {
		return REGISTERED_CONFIGS;
	}

	public static Optional<ConfigHolder> getConfig(String modId) {
		return Optional.ofNullable(REGISTERED_CONFIGS.get(modId));
	}

	public static void saveAllModsConfigs() {
		REGISTERED_CONFIGS.values().forEach(platformConfigHelper::save);
		Constants.LOG.info("Saved all mods configs");
	}
}