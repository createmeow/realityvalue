package dev.ghen.thirst.foundation.mixin.toughasnails;

import glitchcore.event.client.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.client.handler.TooltipHandler;

@Mixin(value = {TooltipHandler.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/toughasnails/MixinTooltipHandler.class */
public class MixinTooltipHandler {
    @Inject(method = {"onRenderTooltip"}, at = {@At("HEAD")}, remap = false, cancellable = true)
    private static void onRenderTooltip(RenderTooltipEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
