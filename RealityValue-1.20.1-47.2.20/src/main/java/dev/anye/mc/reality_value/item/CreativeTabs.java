package dev.anye.mc.reality_value.item;

import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RealityValue.MOD_ID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("nu_tab",()-> CreativeModeTab.builder().icon(()->new ItemStack(ItemRegister.Epinephrine.get()))
            .title(Component.translatable("create_tab.reality_value.all"))
            .displayItems((pParameters, pOutput) -> {
                ItemRegister.ITEMS.getEntries().forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
            })
            .build());

    public static void reg(IEventBus eventBus){
        TABS.register(eventBus);
    }
}
