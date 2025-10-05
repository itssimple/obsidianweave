package se.itssimple.obsidianweave.fabric.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import se.itssimple.obsidianweave.platform.IPlatformItemHelper;

import java.util.function.Supplier;


public class FabricItemHelper implements IPlatformItemHelper {
    private String modId;

    @Override
    public void initialize(String modId) {
        this.modId = modId;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier)
    {
        T item = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(modId, name), itemSupplier.get());
        return () -> item;
    }
}
