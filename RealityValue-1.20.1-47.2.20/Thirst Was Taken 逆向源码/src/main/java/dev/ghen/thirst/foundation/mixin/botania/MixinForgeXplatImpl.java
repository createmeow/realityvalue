package dev.ghen.thirst.foundation.mixin.botania;

import dev.ghen.thirst.compat.create.SandFilterTileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.forge.xplat.ForgeXplatImpl;
import vazkii.botania.xplat.XplatAbstractions;

@Mixin(value = {ForgeXplatImpl.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/botania/MixinForgeXplatImpl.class */
public abstract class MixinForgeXplatImpl implements XplatAbstractions {
    @Inject(method = {"extractFluidFromPlayerItem"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    public void extractFluidFromPlayerItem(Player player, InteractionHand hand, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        ItemStack a = player.m_21120_(hand);
        ItemStack abc = new ItemStack(a.m_41720_());
        cir.setReturnValue((Boolean) abc.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(h -> {
            FluidStack ex = h.drain(new FluidStack(fluid, SandFilterTileEntity.TANK_SIZE), IFluidHandler.FluidAction.SIMULATE);
            boolean su = ex.getFluid() == fluid && ex.getAmount() == 1000;
            if (su && !player.m_150110_().f_35937_) {
                h.drain(new FluidStack(fluid, SandFilterTileEntity.TANK_SIZE), IFluidHandler.FluidAction.EXECUTE);
                player.m_21008_(hand, h.getContainer());
            }
            return Boolean.valueOf(su);
        }).orElse(false));
    }
}
