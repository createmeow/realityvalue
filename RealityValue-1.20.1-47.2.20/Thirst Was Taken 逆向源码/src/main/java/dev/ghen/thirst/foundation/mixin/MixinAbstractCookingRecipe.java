package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({AbstractCookingRecipe.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinAbstractCookingRecipe.class */
public class MixinAbstractCookingRecipe {
    @ModifyArg(method = {"matches"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z"))
    public ItemStack matches(ItemStack itemStack) {
        if (WaterPurity.isWaterFilledContainer(itemStack) && !itemStack.m_41783_().m_128441_("Purity")) {
            ItemStack matched = itemStack.m_41777_();
            CompoundTag tag = matched.m_41784_();
            tag.m_128405_("Purity", ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue());
            return matched;
        }
        return itemStack;
    }
}
