package dev.ghen.thirst.foundation.mixin.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.ghen.thirst.content.purity.WaterPurity;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = {IHaveGoggleInformation.class}, remap = false)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/create/MixinIHaveGoggleInformation.class */
public interface MixinIHaveGoggleInformation {
    @Overwrite
    default boolean containedFluidTooltip(List<Component> tooltip, boolean isPlayerSneaking, LazyOptional<IFluidHandler> handler) {
        Optional<IFluidHandler> resolve = handler.resolve();
        if (resolve.isEmpty()) {
            return false;
        }
        IFluidHandler tank = resolve.get();
        if (tank.getTanks() == 0) {
            return false;
        }
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets", new Object[0]);
        CreateLang.translate("gui.goggles.fluid_container", new Object[0]).forGoggles(tooltip);
        boolean isEmpty = true;
        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack fluidStack = tank.getFluidInTank(i);
            if (!fluidStack.isEmpty()) {
                if ((WaterPurity.hasPurity(fluidStack) || fluidStack.getFluid().equals(Fluids.f_76193_)) && WaterPurity.getPurity(fluidStack) != -1) {
                    int purity = WaterPurity.getPurity(fluidStack);
                    ChatFormatting color = getPurityColor(purity);
                    CreateLang.builder().text(WaterPurity.getPurityText(purity) + " ").add(fluidStack.getDisplayName().m_6881_()).style(color).forGoggles(tooltip, 1);
                } else {
                    CreateLang.fluidName(fluidStack).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
                }
                CreateLang.builder().add(CreateLang.number(fluidStack.getAmount()).add(mb).style(ChatFormatting.GOLD)).text(ChatFormatting.GRAY, " / ").add(CreateLang.number(tank.getTankCapacity(i)).add(mb).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
                isEmpty = false;
            }
        }
        if (tank.getTanks() > 1) {
            if (isEmpty) {
                tooltip.remove(tooltip.size() - 1);
                return true;
            }
            return true;
        }
        if (!isEmpty) {
            return true;
        }
        CreateLang.translate("gui.goggles.fluid_container.capacity", new Object[0]).add(CreateLang.number(tank.getTankCapacity(0)).add(mb).style(ChatFormatting.GOLD)).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
        return true;
    }

    default ChatFormatting getPurityColor(int purity) {
        if (purity == 3) {
            return ChatFormatting.AQUA;
        }
        if (purity == 2) {
            return ChatFormatting.BLUE;
        }
        if (purity == 1) {
            return ChatFormatting.GRAY;
        }
        if (purity == 0) {
            return ChatFormatting.DARK_GRAY;
        }
        return ChatFormatting.GRAY;
    }
}
