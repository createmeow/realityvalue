package dev.ghen.thirst.content.registry;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.item.DrinkableItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/registry/ItemInit.class */
public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Thirst.f0ID);
    public static final RegistryObject<Item> CLAY_BOWL = ITEMS.register("clay_bowl", () -> {
        return new Item(new Item.Properties().m_41487_(64));
    });
    public static final RegistryObject<Item> TERRACOTTA_BOWL = ITEMS.register("terracotta_bowl", () -> {
        return new Item(new Item.Properties().m_41487_(64));
    });
    public static final RegistryObject<Item> TERRACOTTA_WATER_BOWL = ITEMS.register("terracotta_water_bowl", () -> {
        return new DrinkableItem().setContainer((Item) TERRACOTTA_BOWL.get());
    });
}
