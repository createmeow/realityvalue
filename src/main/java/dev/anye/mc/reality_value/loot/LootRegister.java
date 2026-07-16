package dev.anye.mc.reality_value.loot;

import com.mojang.serialization.MapCodec;
import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LootRegister {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT = DeferredRegister
            .create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RealityValue.MOD_ID);

    public static final Supplier<MapCodec<AddItemModifier>> ADD = LOOT.register("add_item", AddItemModifier.CODEC);
    public static final Supplier<MapCodec<ReplaceItemModifier>> Replace = LOOT.register("replace_item",
            ReplaceItemModifier.CODEC);

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister
            .create(Registries.LOOT_CONDITION_TYPE, RealityValue.MOD_ID);

    public static final Supplier<LootItemConditionType> MATCH_BLOCK_TAG = LOOT_CONDITIONS.register("match_block_tag",
            () -> new LootItemConditionType(MatchBlockTagCondition.CODEC));

    public static void reg(IEventBus eventBus) {
        LOOT_CONDITIONS.register(eventBus);
        LOOT.register(eventBus);
    }
}