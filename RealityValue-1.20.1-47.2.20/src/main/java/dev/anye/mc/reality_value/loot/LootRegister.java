package dev.anye.mc.reality_value.loot;

import com.mojang.serialization.Codec;
import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LootRegister {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RealityValue.MOD_ID);

    public static final RegistryObject<Codec<AddItemModifier>> ADD =
            LOOT.register("add_item", AddItemModifier.CODEC);
    public static final RegistryObject<Codec<ReplaceItemModifier>> Replace =
            LOOT.register("replace_item", ReplaceItemModifier.CODEC);


    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, RealityValue.MOD_ID);

    public static final RegistryObject<LootItemConditionType> MATCH_BLOCK_TAG =
            LOOT_CONDITIONS.register("match_block_tag",
                    () -> new LootItemConditionType(new MatchBlockTagCondition.Serializer()));

    public static void reg(IEventBus eventBus){
        LOOT_CONDITIONS.register(eventBus);
        LOOT.register(eventBus);

    }
}
