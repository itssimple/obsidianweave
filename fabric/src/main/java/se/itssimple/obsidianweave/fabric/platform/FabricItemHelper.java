package se.itssimple.obsidianweave.fabric.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import se.itssimple.obsidianweave.platform.IPlatformItemHelper;

import java.util.function.Supplier;

/**
 * Fabric-specific item helper for Obsidian Weave.
 * Handles item registration using Fabric's registry system.
 */
public class FabricItemHelper implements IPlatformItemHelper {
    /**
     * The mod ID for item registration.
     */
    private String modId;

    /**
     * Initializes the item helper for the given mod ID.
     * @param modId The mod ID to register items for.
     */
    @Override
    public void initialize(String modId) {
        this.modId = modId;
    }

    /**
     * Registers an item with the given name and supplier.
     * @param name The item name.
     * @param itemSupplier The item supplier.
     * @return A supplier for the registered item.
     */
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier)
    {
        T item = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(modId, name), itemSupplier.get());
        return () -> item;
    }
}
