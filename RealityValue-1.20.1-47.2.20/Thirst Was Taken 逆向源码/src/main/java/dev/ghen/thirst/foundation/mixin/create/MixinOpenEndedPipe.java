package dev.ghen.thirst.foundation.mixin.create;

import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.FluidHelper;
import dev.ghen.thirst.compat.create.SandFilterTileEntity;
import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {OpenEndedPipe.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/create/MixinOpenEndedPipe.class */
public class MixinOpenEndedPipe {
    @Inject(method = {"removeFluidFromSpace"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    private void removeFluidFromSpace(boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        OpenEndedPipe pipe = (OpenEndedPipe) this;
        if (pipe.getWorld() != null && pipe.getWorld().m_46749_(pipe.getOutputPos())) {
            BlockState state = pipe.getWorld().m_8055_(pipe.getOutputPos());
            FluidState fluidState = state.m_60819_();
            boolean waterlog = state.m_61138_(BlockStateProperties.f_61362_);
            if (fluidState.m_76178_() || !fluidState.m_76170_()) {
                return;
            }
            if (waterlog || state.m_247087_()) {
                FluidStack stack = new FluidStack(fluidState.m_76152_(), SandFilterTileEntity.TANK_SIZE);
                if (FluidHelper.isWater(stack.getFluid())) {
                    WaterPurity.addPurity(stack, WaterPurity.getBlockPurity(pipe.getWorld(), pipe.getOutputPos()));
                    if (simulate) {
                        cir.setReturnValue(stack);
                        return;
                    }
                    AdvancementBehaviour.tryAward(pipe.getWorld(), pipe.getPos(), AllAdvancements.WATER_SUPPLY);
                    if (waterlog) {
                        pipe.getWorld().m_7731_(pipe.getOutputPos(), (BlockState) state.m_61124_(BlockStateProperties.f_61362_, false), 3);
                        pipe.getWorld().m_186469_(pipe.getOutputPos(), Fluids.f_76193_, 1);
                        cir.setReturnValue(stack);
                    } else {
                        pipe.getWorld().m_7731_(pipe.getOutputPos(), (BlockState) fluidState.m_76188_().m_61124_(LiquidBlock.f_54688_, 14), 3);
                        cir.setReturnValue(stack);
                    }
                }
            }
        }
    }
}
