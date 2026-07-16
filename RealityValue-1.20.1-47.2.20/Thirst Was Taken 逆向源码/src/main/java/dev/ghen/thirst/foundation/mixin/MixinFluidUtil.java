package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = {FluidUtil.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinFluidUtil.class */
public class MixinFluidUtil {
    @Overwrite
    @NotNull
    public static ItemStack getFilledBucket(@NotNull FluidStack fluidStack) {
        FlowingFluid fluid = fluidStack.getFluid();
        if (!fluidStack.hasTag() || fluidStack.getTag().m_128456_()) {
            if (fluid == Fluids.f_76193_) {
                return new ItemStack(Items.f_42447_);
            }
            if (fluid == Fluids.f_76195_) {
                return new ItemStack(Items.f_42448_);
            }
        } else if (WaterPurity.hasPurity(fluidStack)) {
            return WaterPurity.addPurity(new ItemStack(fluidStack.getFluid().m_6859_()), WaterPurity.getPurity(fluidStack));
        }
        return new ItemStack(fluidStack.getFluid().m_6859_());
    }
}
