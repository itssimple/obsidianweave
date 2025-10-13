package se.itssimple.obsidianweave.forge.platform;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import se.itssimple.obsidianweave.platform.IPlatformItemHelper;

import java.util.function.Supplier;

public class ForgeItemHelper implements IPlatformItemHelper {
    private DeferredRegister<Item> deferredRegister;

    @Override
    public void initialize(String modId) {
        this.deferredRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        return this.deferredRegister.register(name, itemSupplier);
    }

    public void register(IEventBus eventBus)
    {
        this.deferredRegister.register(eventBus);
    }
}
