package dev.anye.mc.reality_value.mixin;

import dev.anye.mc.reality_value.cap.PlayerExProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract <T> LazyOptional<T> getCapability(Capability<T> capability, @org.jetbrains.annotations.Nullable Direction facing);
    @Shadow
    public abstract float getHealth();
    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void rv$setHealth$ban(float pHealth, CallbackInfo ci){
        this.getCapability(PlayerExProvider.PlayerExCap,null).ifPresent(playerExCap -> {
            if (playerExCap.getHealth() < 6) {
                if (pHealth > this.getHealth()) ci.cancel();
            }
        });

    }
}
