package se.itssimple.obsidianweave.services;

import se.itssimple.obsidianweave.platform.IPlatformConfigHelper;
import se.itssimple.obsidianweave.platform.IPlatformItemHelper;

import java.util.ServiceLoader;

/**
 * Service loader for platform helpers in Obsidian Weave.
 * Provides access to platform-specific config and item helpers.
 */
public class Services {
    /**
     * The platform config helper loaded for the current environment.
     */
    public final static IPlatformConfigHelper PLATFORMHELPER = load(IPlatformConfigHelper.class);
    /**
     * The platform item helper loaded for the current environment.
     */
    public final static IPlatformItemHelper ITEMHELPER = load(IPlatformItemHelper.class);

    /**
     * Loads a service implementation for the given class.
     * @param clazz The class to load the service for.
     * @param <T> The type of the service.
     * @return The loaded service implementation.
     * @throws NullPointerException if no implementation is found.
     */
    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("[Obsidian Weave] Failed to load service for " + clazz.getName() + "."));
    }
}
