package se.itssimple.obsidianweave.platform;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Platform abstraction for item registration in Obsidian Weave mods.
 * Implemented by platform-specific helpers for Fabric and Forge.
 */
public interface IPlatformItemHelper {
    /**
     * Initializes the item helper for the given mod ID.
     * @param modId The mod ID to register items for.
     */
    void initialize(String modId);

    /**
     * Registers an item with the given name and supplier.
     * @param name The item name.
     * @param itemSupplier The item supplier.
     * @param <T> The item type.
     * @return A supplier for the registered item.
     */
    <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier);
}
