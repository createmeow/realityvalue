package dev.ghen.thirst.foundation.tab;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.compat.create.CreateRegistry;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.content.registry.ItemInit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/tab/ThirstTab.class */
public class ThirstTab {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.f_279569_, Thirst.f0ID);
    public static final RegistryObject<CreativeModeTab> THIRST_TAB = TAB_REGISTER.register(Thirst.f0ID, () -> {
        CreativeModeTab.Builder builderM_257941_ = CreativeModeTab.builder().m_257941_(Component.m_237115_("itemGroup.thirst"));
        Item item = (Item) ItemInit.TERRACOTTA_WATER_BOWL.get();
        Objects.requireNonNull(item);
        return builderM_257941_.m_257737_(item::m_7968_).m_257501_((displayParameters, output) -> {
            output.m_246601_(DisplayItems());
        }).m_257652_();
    });

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }

    public static Collection<ItemStack> DisplayItems() {
        Collection<ItemStack> list = new ArrayList<>();
        list.add(WaterPurity.addPurity(new ItemStack(Items.f_42447_), 0));
        list.add(WaterPurity.addPurity(new ItemStack(Items.f_42447_), 1));
        list.add(WaterPurity.addPurity(new ItemStack(Items.f_42447_), 2));
        list.add(WaterPurity.addPurity(new ItemStack(Items.f_42447_), 3));
        list.add(WaterPurity.addPurity(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_), 0));
        list.add(WaterPurity.addPurity(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_), 1));
        list.add(WaterPurity.addPurity(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_), 2));
        list.add(WaterPurity.addPurity(PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_), 3));
        list.add(((Item) ItemInit.CLAY_BOWL.get()).m_7968_());
        list.add(((Item) ItemInit.TERRACOTTA_BOWL.get()).m_7968_());
        list.add(WaterPurity.addPurity(new ItemStack((ItemLike) ItemInit.TERRACOTTA_WATER_BOWL.get()), 0));
        list.add(WaterPurity.addPurity(new ItemStack((ItemLike) ItemInit.TERRACOTTA_WATER_BOWL.get()), 1));
        list.add(WaterPurity.addPurity(new ItemStack((ItemLike) ItemInit.TERRACOTTA_WATER_BOWL.get()), 2));
        list.add(((Item) ItemInit.TERRACOTTA_WATER_BOWL.get()).m_7968_());
        if (ModList.get().isLoaded("create")) {
            list.add(CreateRegistry.SAND_FILTER_BLOCK.asStack());
        }
        return list;
    }
}
