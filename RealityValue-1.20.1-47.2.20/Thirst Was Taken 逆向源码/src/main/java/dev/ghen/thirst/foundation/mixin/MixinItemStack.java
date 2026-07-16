package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.foundation.config.CommonConfig;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinItemStack.class */
public abstract class MixinItemStack {
    @Shadow
    public abstract Item m_41720_();

    @Shadow
    @Nullable
    public abstract CompoundTag m_41783_();

    @Inject(method = {"getMaxStackSize"}, at = {@At("HEAD")}, cancellable = true)
    public void changeWaterBottleStackSize(CallbackInfoReturnable<Integer> cir) {
        if (m_41720_() == Items.f_42589_ && PotionUtils.m_43577_(m_41783_()) == Potions.f_43599_) {
            cir.setReturnValue((Integer) CommonConfig.WATER_BOTTLE_STACKSIZE.get());
        }
    }
}
