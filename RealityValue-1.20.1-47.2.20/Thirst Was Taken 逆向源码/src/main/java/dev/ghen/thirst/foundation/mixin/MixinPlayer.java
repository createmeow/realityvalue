package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.thirst.PlayerThirst;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinPlayer.class */
public abstract class MixinPlayer {
    @Inject(method = {"eat"}, at = {@At("HEAD")})
    public void onEatDrink(Level level, ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        Player player = (Player) this;
        PlayerThirst.drink(item, player);
    }
}
