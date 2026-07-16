package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.item.ItemRegister;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class DataGen_ItemModelProvider extends ItemModelProvider {
    public DataGen_ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RealityValue.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemRegister.ITEMS.getEntries().forEach(itemRegistryObject -> {
            itemRegistryObject.ifPresent(item -> {
                    simpleItem(itemRegistryObject);

            });
        });
    }
    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),new ResourceLocation("item/generated")).texture("layer0",new ResourceLocation(RealityValue.MOD_ID,"item/"+item.getId().getPath()));
    }
}
