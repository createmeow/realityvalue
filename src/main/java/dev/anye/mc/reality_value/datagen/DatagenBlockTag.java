package dev.anye.mc.reality_value.datagen;

import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DatagenBlockTag extends BlockTagsProvider {
    public static final ResourceLocation Key = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "grass");
    public static final TagKey<Block> Grass = BlockTags.create(Key);

    public DatagenBlockTag(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RealityValue.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(Grass).add(Blocks.SHORT_GRASS, Blocks.FERN, Blocks.TALL_GRASS, Blocks.LARGE_FERN, Blocks.SEAGRASS);
    }
}