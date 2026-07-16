package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FoodData.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinFoodData.class */
public abstract class MixinFoodData {

    @Shadow
    private float f_38698_;

    @Unique
    private int dehydratedHealTimer = 0;

    @Shadow
    public abstract void m_38703_(float f);

    @Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V", ordinal = WaterPurity.MIN_PURITY))
    private void healWithSaturation(Player player, float amount) {
        if (!player.getCapability(ModCapabilities.PLAYER_THIRST).isPresent()) {
            return;
        }
        FoodData foodData = player.m_36324_();
        IThirst thirstData = (IThirst) player.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
        float f = Math.min(foodData.m_38722_(), 6.0f);
        boolean shouldHeal = !((Boolean) CommonConfig.DEHYDRATION_HALTS_HEALTH_REGEN.get()).booleanValue() || thirstData.getThirst() >= 20;
        if (shouldHeal) {
            player.m_5634_(f / 6.0f);
            thirstData.setJustHealed();
            return;
        }
        this.dehydratedHealTimer++;
        if (this.dehydratedHealTimer >= 8 && thirstData.getThirst() > 18) {
            player.m_5634_(f / 6.0f);
            thirstData.setJustHealed();
            this.dehydratedHealTimer = 0;
            return;
        }
        m_38703_(-f);
    }

    @Redirect(method = {"tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V", ordinal = 1))
    private void healWithHunger(Player player, float amount) {
        if (!player.getCapability(ModCapabilities.PLAYER_THIRST).isPresent()) {
            return;
        }
        IThirst thirstData = (IThirst) player.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
        boolean shouldHeal = !((Boolean) CommonConfig.DEHYDRATION_HALTS_HEALTH_REGEN.get()).booleanValue() || thirstData.getThirst() > 18;
        if (shouldHeal) {
            player.m_5634_(1.0f);
            thirstData.setJustHealed();
        } else {
            m_38703_(-6.0f);
        }
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void DealWithExhaustionBySaturation(Player player, CallbackInfo ci) {
        if (this.f_38698_ > 4.0f) {
            player.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent((v0) -> {
                v0.ExhaustionRecalculate();
            });
        }
    }
}
