package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.item.ItemRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class DataGen_ItemModelProvider extends ItemModelProvider {
    public DataGen_ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RealityValue.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemRegister.ITEMS.getEntries().forEach(itemRegistryObject -> {
            simpleItem(itemRegistryObject);
        });
    }

    private ItemModelBuilder simpleItem(Supplier<? extends Item> item) {
        var obj = item.get();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(obj);
        return withExistingParent(id.getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "item/" + id.getPath()));
    }
}