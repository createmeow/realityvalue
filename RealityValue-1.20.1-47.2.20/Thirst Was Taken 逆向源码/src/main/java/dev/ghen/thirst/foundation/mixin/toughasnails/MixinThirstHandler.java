package dev.ghen.thirst.foundation.mixin.toughasnails;

import glitchcore.event.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstHandler;

@Mixin(value = {ThirstHandler.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/toughasnails/MixinThirstHandler.class */
public class MixinThirstHandler {
    @Inject(method = {"onPlayerUseItem"}, at = {@At("HEAD")}, cancellable = true)
    private static void onPlayerInteractItem(PlayerInteractEvent.UseItem event, CallbackInfo ci) {
        ci.cancel();
    }
}
