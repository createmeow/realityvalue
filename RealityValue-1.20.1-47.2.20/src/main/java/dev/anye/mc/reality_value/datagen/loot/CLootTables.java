package dev.anye.mc.reality_value.datagen.loot;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.datagen.DatagenBlockTag;
import dev.anye.mc.reality_value.item.ItemRegister;
import dev.anye.mc.reality_value.loot.AddItemModifier;
import dev.anye.mc.reality_value.loot.MatchBlockTagCondition;
import dev.anye.mc.reality_value.loot.ReplaceItemModifier;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlockTagPredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class CLootTables extends GlobalLootModifierProvider {
    public static final LootItemCondition isGrassTag = LootTableIdCondition.builder(DatagenBlockTag.Key).build();
    public CLootTables(PackOutput output) {
        super(output, RealityValue.MOD_ID);
    }

    @Override
    protected void start() {
        add("epinephrine_from_zombie", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(new ResourceLocation("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.001f).build()
                },
                ItemRegister.Epinephrine.get()
        ));
        add("tattered_cloth_from_zombie", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(new ResourceLocation("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.01f).build()
                },
                ItemRegister.TatteredCloth.get()
        ));
        add("stimulant_from_zombie", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(new ResourceLocation("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.005f).build()
                },
                ItemRegister.Stimulant.get()
        ));


        add("moss_block_drop_replace", new ReplaceItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(new ResourceLocation("blocks/moss_block")).build(),
                        LootItemRandomChanceCondition.randomChance(0.01f).build()
                },
                ItemRegister.Antibiotics.get()
        ));
        add("moss_carpet_drop_replace", new ReplaceItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(new ResourceLocation("blocks/moss_carpet")).build(),
                        LootItemRandomChanceCondition.randomChance(0.01f).build()
                },
                ItemRegister.Antibiotics.get()
        ));


        add("herb_drop_from_grass", new ReplaceItemModifier(
                new LootItemCondition[] {
                        /*
                        LocationCheck.checkLocation(
                                LocationPredicate.Builder.location()
                                        .setBlock(BlockPredicate.Builder.block()
                                                .of(DatagenBlockTag.Grass)
                                                .build())
                        ).build(),

                         */
                        MatchBlockTagCondition.builder(DatagenBlockTag.Grass).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build(),
                        MatchTool.toolMatches(ItemPredicate.Builder.item()
                                        .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))
                                .invert().build()
                },
                ItemRegister.Herb.get()
        ));
        add("vanilla_drop_from_grass", new ReplaceItemModifier(
                new LootItemCondition[] {
                        MatchBlockTagCondition.builder(DatagenBlockTag.Grass).build(),
                        LootItemRandomChanceCondition.randomChance(0.005f).build(),
                        MatchTool.toolMatches(ItemPredicate.Builder.item()
                                        .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))))
                                .invert().build()
                },
                ItemRegister.Vanilla.get()
        ));
    }
}
