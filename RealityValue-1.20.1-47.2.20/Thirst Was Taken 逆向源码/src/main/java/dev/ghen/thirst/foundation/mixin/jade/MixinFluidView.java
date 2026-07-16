package dev.ghen.thirst.foundation.mixin.jade;

import dev.ghen.thirst.content.purity.WaterPurity;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.view.FluidView;
import snownee.jade.util.CommonProxy;

@Mixin(value = {FluidView.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/jade/MixinFluidView.class */
public class MixinFluidView {
    @Redirect(method = {"readDefault"}, at = @At(value = "INVOKE", target = "Lsnownee/jade/util/CommonProxy;getFluidName(Lsnownee/jade/api/fluid/JadeFluidObject;)Lnet/minecraft/network/chat/Component;"))
    private static Component read(JadeFluidObject fluid) {
        FluidStack instance = CommonProxy.toFluidStack(fluid);
        if (instance.isEmpty()) {
            return CommonProxy.getFluidName(fluid);
        }
        if ((WaterPurity.hasPurity(instance) || instance.getFluid().equals(Fluids.f_76193_)) && WaterPurity.getPurity(instance) != -1) {
            return Component.m_237113_((String) Objects.requireNonNull(WaterPurity.getPurityText(WaterPurity.getPurity(instance)))).m_130946_(" ").m_7220_(instance.getDisplayName());
        }
        return CommonProxy.getFluidName(fluid);
    }
}
