package dev.anye.mc.reality_value.datagen.loot;

import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.datagen.DatagenBlockTag;
import dev.anye.mc.reality_value.item.ItemRegister;
import dev.anye.mc.reality_value.loot.AddItemModifier;
import dev.anye.mc.reality_value.loot.MatchBlockTagCondition;
import dev.anye.mc.reality_value.loot.ReplaceItemModifier;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CLootTables extends GlobalLootModifierProvider {
    public CLootTables(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, RealityValue.MOD_ID);
    }

    @Override
    protected void start() {
        add("first_aid_injection_from_zombie", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.001f).build()
                },
                ItemRegister.FirstAidInjection.get()));

        add("tattered_cloth_from_zombie", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.01f).build()
                },
                ItemRegister.TatteredCloth.get()));

        add("rum_from_zombie", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.005f).build()
                },
                ItemRegister.Rum.get()));

        add("moss_block_drop_replace", new ReplaceItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/moss_block")).build(),
                        LootItemRandomChanceCondition.randomChance(0.01f).build()
                },
                ItemRegister.Antibiotics.get()));

        add("moss_carpet_drop_replace", new ReplaceItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("blocks/moss_carpet"))
                                .build(),
                        LootItemRandomChanceCondition.randomChance(0.01f).build()
                },
                ItemRegister.Antibiotics.get()));

        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        add("herb_drop_from_grass", new ReplaceItemModifier(
                new LootItemCondition[] {
                        MatchBlockTagCondition.builder(DatagenBlockTag.Grass).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build(),
                        MatchTool.toolMatches(ItemPredicate.Builder.item()
                                        .withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
                                                ItemEnchantmentsPredicate.enchantments(List.of(
                                                        new EnchantmentPredicate(
                                                                enchantments.getOrThrow(Enchantments.SILK_TOUCH),
                                                                MinMaxBounds.Ints.atLeast(1))))))
                                .invert().build()
                },
                ItemRegister.Herb.get()));

        }
}