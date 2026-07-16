package dev.ghen.thirst.foundation.mixin.farmersrespite;

import dev.ghen.thirst.content.purity.WaterPurity;
import java.util.Objects;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import umpaz.farmersrespite.client.gui.KettleScreen;

@Mixin(value = {KettleScreen.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/farmersrespite/MixinKettleScreen.class */
public class MixinKettleScreen {
    @Redirect(method = {"renderTankTooltip"}, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    private Component renderTankTooltip(FluidStack instance) {
        if (WaterPurity.hasPurity(instance) && WaterPurity.getPurity(instance) != -1) {
            return Component.m_237113_(((String) Objects.requireNonNull(WaterPurity.getPurityText(WaterPurity.getPurity(instance)))) + " " + I18n.m_118938_(instance.getTranslationKey(), new Object[0]));
        }
        return instance.getDisplayName();
    }
}
