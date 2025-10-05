package se.itssimple.obsidianweave.platform;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface IPlatformItemHelper {
    void initialize(String modId);
    <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier);
}
