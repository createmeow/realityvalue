package dev.ghen.thirst.foundation.mixin.toughasnails;

import dev.ghen.thirst.content.purity.WaterPurity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import toughasnails.config.ThirstConfig;

@Mixin(value = {ThirstConfig.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/toughasnails/MixinServerConfig.class */
public class MixinServerConfig {
    @ModifyArg(method = {"load"}, at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;valueOf(Z)Ljava/lang/Boolean;", ordinal = WaterPurity.MIN_PURITY), index = WaterPurity.MIN_PURITY)
    private boolean modifyBoolean(boolean defaultValue) {
        return false;
    }
}
