package se.itssimple.obsidianweave.neoforge.platform;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import se.itssimple.obsidianweave.platform.IPlatformItemHelper;

import java.util.function.Supplier;

/**
 * Forge-specific item helper for Obsidian Weave.
 * Handles item registration using Forge's DeferredRegister system.
 */
public class NeoForgeItemHelper implements IPlatformItemHelper {
    /**
     * Deferred register for items for the current mod.
     */
    private DeferredRegister<Item> deferredRegister;

    /**
     * Initializes the item helper for the given mod ID.
     * @param modId The mod ID to register items for.
     */
    @Override
    public void initialize(String modId) {
        this.deferredRegister = DeferredRegister.createItems(modId);
    }

    /**
     * Registers an item with the given name and supplier.
     * @param name The item name.
     * @param itemSupplier The item supplier.
     * @return A supplier for the registered item.
     */
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        return this.deferredRegister.register(name, itemSupplier);
    }

    /**
     * Registers all items with the given event bus.
     * @param eventBus The Forge event bus.
     */
    public void register(IEventBus eventBus)
    {
        this.deferredRegister.register(eventBus);
    }
}
