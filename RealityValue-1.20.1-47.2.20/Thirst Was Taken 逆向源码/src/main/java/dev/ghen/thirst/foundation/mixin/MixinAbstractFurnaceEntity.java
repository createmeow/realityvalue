package dev.ghen.thirst.foundation.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({AbstractFurnaceBlockEntity.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinAbstractFurnaceEntity.class */
public class MixinAbstractFurnaceEntity {
    @Redirect(method = {"canBurn"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean canBurn(ItemStack remainItem, ItemStack recipeResult) {
        return ItemStack.m_150942_(remainItem, recipeResult);
    }
}
