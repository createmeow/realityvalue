package dev.anye.mc.reality_value.mixin;

import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract float getHealth();

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void rv$setHealth$ban(float pHealth, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer serverPlayer) {
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            if (cap.getHealth() < 6) {
                if (pHealth > this.getHealth())
                    ci.cancel();
            }
        }
    }
}