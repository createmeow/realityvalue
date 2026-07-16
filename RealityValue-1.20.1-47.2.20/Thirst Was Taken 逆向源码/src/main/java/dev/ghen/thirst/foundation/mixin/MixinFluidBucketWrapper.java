package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.compat.create.SandFilterTileEntity;
import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {FluidBucketWrapper.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinFluidBucketWrapper.class */
public class MixinFluidBucketWrapper {

    @Shadow
    @NotNull
    protected ItemStack container;

    @Overwrite
    @NotNull
    public FluidStack getFluid() {
        BucketItem bucketItemM_41720_ = this.container.m_41720_();
        if (!(bucketItemM_41720_ instanceof BucketItem)) {
            return ((bucketItemM_41720_ instanceof MilkBucketItem) && ForgeMod.MILK.isPresent()) ? new FluidStack((Fluid) ForgeMod.MILK.get(), SandFilterTileEntity.TANK_SIZE) : FluidStack.EMPTY;
        }
        FluidStack stack = new FluidStack(bucketItemM_41720_.getFluid(), SandFilterTileEntity.TANK_SIZE);
        if (WaterPurity.hasPurity(this.container)) {
            WaterPurity.addPurity(stack, WaterPurity.getPurity(this.container));
        }
        return stack;
    }
}
