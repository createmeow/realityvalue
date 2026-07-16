package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.item.ItemRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DataGen_Recipe extends RecipeProvider {
    public DataGen_Recipe(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(pOutput, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput consumer) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegister.Bandage.get())
                .requires(ItemRegister.Herb.get())
                .requires(ItemRegister.TatteredCloth.get())
                .unlockedBy(getHasName(ItemRegister.TatteredCloth.get()), has(ItemRegister.TatteredCloth.get()))
                .save(consumer);
    }
}