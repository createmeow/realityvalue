package dev.ghen.thirst.foundation.mixin.accessors.brewinandchewin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;

@Mixin(value = {KegBlockEntity.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/accessors/brewinandchewin/KegBlockEntityAccessor.class */
public interface KegBlockEntityAccessor {
    @Invoker
    void invokeEjectIngredientRemainder(ItemStack itemStack);
}
