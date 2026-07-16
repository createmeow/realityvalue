package dev.anye.mc.reality_value.item;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.item.food.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ItemRegister {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RealityValue.MOD_ID);

    public static final Supplier<AntibioticsItem> Antibiotics = ITEMS.register("antibiotics", AntibioticsItem::new);
    public static final Supplier<HerbItem> Herb = ITEMS.register("herb", HerbItem::new);
    public static final Supplier<BandageItem> Bandage = ITEMS.register("bandage", BandageItem::new);
    public static final Supplier<TatteredClothItem> TatteredCloth = ITEMS.register("tattered_cloth",
            TatteredClothItem::new);

    public static final Supplier<RumItem> Rum = ITEMS.register("rum", RumItem::new);
    public static final Supplier<FirstAidInjectionItem> FirstAidInjection = ITEMS.register("first_aid_injection",
            FirstAidInjectionItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}