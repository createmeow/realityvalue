package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.content.thirst.PlayerThirst;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({PotionItem.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinPotionItem.class */
public class MixinPotionItem {
    @Redirect(method = {"finishUsingItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean finishUsingItem(Inventory instance, ItemStack stack) {
        if (!instance.m_36054_(stack)) {
            instance.f_35978_.m_36176_(stack, false);
            return true;
        }
        return true;
    }

    @Inject(method = {"finishUsingItem"}, at = {@At("HEAD")}, locals = LocalCapture.CAPTURE_FAILHARD)
    public void onFinishUsingItem(ItemStack item, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        Player player = livingEntity instanceof Player ? (Player) livingEntity : null;
        if (player != null && WaterPurity.givePurityEffects((Player) livingEntity, item)) {
            PlayerThirst.drink(item, player);
        }
    }
}
