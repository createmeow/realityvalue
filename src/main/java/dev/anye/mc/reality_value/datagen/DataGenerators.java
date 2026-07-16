package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.datagen.loot.CLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = RealityValue.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        dataGenerator.addProvider(event.includeClient(), new DataGen_Recipe(packOutput, lookup));

        dataGenerator.addProvider(event.includeClient(),
                new DataGen_ItemModelProvider(packOutput, existingFileHelper));

        DatagenBlockTag blockTag = dataGenerator.addProvider(true,
                new DatagenBlockTag(packOutput, lookup, existingFileHelper));
        dataGenerator.addProvider(true,
                new DataGen_ItemTag(packOutput, lookup, blockTag.contentsGetter(), existingFileHelper));

        dataGenerator.addProvider(event.includeServer(), new CLootTables(packOutput, lookup));
    }
}