package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.item.ItemRegister;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DataGen_Recipe extends RecipeProvider implements IConditionBuilder {
    public DataGen_Recipe(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegister.Bandage.get())
                .requires(ItemRegister.Herb.get())
                .requires(ItemRegister.TatteredCloth.get())
                .unlockedBy(getHasName(ItemRegister.TatteredCloth.get()),has(ItemRegister.TatteredCloth.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegister.HighlyConcentratedVanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .requires(ItemRegister.Vanilla.get())
                .unlockedBy(getHasName(ItemRegister.Vanilla.get()),has(ItemRegister.Vanilla.get()))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegister.HighSugarChocolate.get())
                .pattern("AB")
                .pattern("BC")
                .define('A', Items.MILK_BUCKET)
                .define('B', Items.SUGAR)
                .define('C', Items.COCOA_BEANS)
                .unlockedBy(getHasName(Items.SUGAR),has(Items.SUGAR))
                .save(consumer);
    }
}
