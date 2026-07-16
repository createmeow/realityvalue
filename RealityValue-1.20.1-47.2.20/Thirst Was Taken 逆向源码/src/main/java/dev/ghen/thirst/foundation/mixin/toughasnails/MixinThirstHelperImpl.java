package dev.ghen.thirst.foundation.mixin.toughasnails;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.thirst.ThirstHelperImpl;

@Mixin(value = {ThirstHelperImpl.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/toughasnails/MixinThirstHelperImpl.class */
public class MixinThirstHelperImpl {
    @Inject(method = {"canDrink"}, at = {@At("RETURN")}, cancellable = true)
    private void canDrink(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
