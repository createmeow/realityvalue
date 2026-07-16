package dev.anye.mc.reality_value.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class MatchBlockTagCondition implements LootItemCondition {
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

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MatchBlockTagCondition> {
        @Override
        public void serialize(JsonObject json, MatchBlockTagCondition condition, JsonSerializationContext context) {
            json.addProperty("tag", condition.tag.location().toString());
        }

        @Override
        public MatchBlockTagCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            return new MatchBlockTagCondition(BlockTags.create(resourcelocation));
        }
    }
}
