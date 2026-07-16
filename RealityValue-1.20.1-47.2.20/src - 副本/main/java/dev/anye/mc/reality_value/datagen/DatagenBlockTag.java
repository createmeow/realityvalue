package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DatagenBlockTag extends BlockTagsProvider {
    public static final ResourceLocation Key = new ResourceLocation(RealityValue.MOD_ID,"grass");
    public static final TagKey<Block> Grass = BlockTags.create(Key);
    public DatagenBlockTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RealityValue.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(Grass).add(Blocks.GRASS,Blocks.FERN,Blocks.TALL_GRASS,Blocks.LARGE_FERN,Blocks.SEAGRASS);
    }
}
