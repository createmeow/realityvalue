package dev.anye.mc.reality_value.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class MatchBlockTagCondition implements LootItemCondition {
    public static final MapCodec<MatchBlockTagCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("tag").forGetter(c -> c.tag.location()))
                    .apply(instance, rl -> new MatchBlockTagCondition(BlockTags.create(rl))));

    private final TagKey<Block> tag;

    public MatchBlockTagCondition(TagKey<Block> tag) {
        this.tag = tag;
    }

    @Override
    public LootItemConditionType getType() {
        return LootRegister.MATCH_BLOCK_TAG.get();
    }

    @Override
    public boolean test(LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        return state != null && state.is(tag);
    }

    public static LootItemCondition.Builder builder(TagKey<Block> tag) {
        return () -> new MatchBlockTagCondition(tag);
    }
}