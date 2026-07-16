package dev.anye.mc.reality_value.item;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.item.food.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RealityValue.MOD_ID);
    public static final RegistryObject<EpinephrineItem> Epinephrine = ITEMS.register("epinephrine",EpinephrineItem::new);
    public static final RegistryObject<AntibioticsItem> Antibiotics = ITEMS.register("antibiotics",AntibioticsItem::new);
    public static final RegistryObject<HerbItem> Herb = ITEMS.register("herb", HerbItem::new);
    public static final RegistryObject<BandageItem> Bandage = ITEMS.register("bandage", BandageItem::new);
    public static final RegistryObject<Item> TatteredCloth = ITEMS.register("tattered_cloth",()->new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<StimulantItem> Stimulant = ITEMS.register("stimulant", StimulantItem::new);
    public static final RegistryObject<HighSugarChocolate> HighSugarChocolate = ITEMS.register("high_sugar_chocolate", HighSugarChocolate::new);

    public static final RegistryObject<VanillaItem> Vanilla = ITEMS.register("vanilla",VanillaItem::new);
    public static final RegistryObject<HighlyConcentratedVanillaItem> HighlyConcentratedVanilla = ITEMS.register("highly_concentrated_vanilla",HighlyConcentratedVanillaItem::new);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
