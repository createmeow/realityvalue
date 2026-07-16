package dev.ghen.thirst.foundation.mixin.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import dev.ghen.thirst.content.purity.WaterPurity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BasinRecipe.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/create/MixinBasinRecipe.class */
public class MixinBasinRecipe {
    @Inject(method = {"apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z", ordinal = WaterPurity.MIN_PURITY)}, remap = false)
    private static void setPurity(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir) {
        int purity = getWaterPurity(basin);
        NonNullList<FluidStack> outputFluids = ((BasinRecipe) recipe).getFluidResults();
        Pattern pattern = Pattern.compile("tea", 2);
        outputFluids.forEach(fluid -> {
            Matcher matcher = pattern.matcher(fluid.getTranslationKey());
            if (matcher.find()) {
                WaterPurity.addPurity(fluid, Math.min(purity, 3));
            }
        });
    }

    private static int getWaterPurity(BasinBlockEntity basin) {
        IFluidHandler availableFluids = (IFluidHandler) basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse((Object) null);
        if (availableFluids == null) {
            return 3;
        }
        for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
            FluidStack fluidStack = availableFluids.getFluidInTank(tank);
            if (WaterPurity.hasPurity(fluidStack)) {
                return WaterPurity.getPurity(fluidStack);
            }
        }
        return -1;
    }
}
