package se.itssimple.obsidianweave.services;

import se.itssimple.obsidianweave.platform.IPlatformConfigHelper;

import java.util.ServiceLoader;

public class Services {
    public final static IPlatformConfigHelper PLATFORMHELPER = load(IPlatformConfigHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("[Obsidian Weave] Failed to load service for " + clazz.getName() + "."));
    }
}
