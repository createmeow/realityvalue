package dev.anye.mc.reality_value.item;

import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            RealityValue.MOD_ID);

    public static final Supplier<CreativeModeTab> TAB = TABS.register("nu_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ItemRegister.Bandage.get()))
            .title(Component.translatable("create_tab.reality_value.all"))
            .displayItems((pParameters, pOutput) -> {
                ItemRegister.ITEMS.getEntries().forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
            })
            .build());

    public static void reg(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}