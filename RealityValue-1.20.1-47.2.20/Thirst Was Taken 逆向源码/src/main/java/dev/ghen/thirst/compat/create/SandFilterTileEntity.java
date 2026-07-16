package dev.ghen.thirst.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.config.CommonConfig;
import java.util.List;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/compat/create/SandFilterTileEntity.class */
public class SandFilterTileEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public static final int TANK_SIZE = 1000;
    SmartFluidTankBehaviour dirtyTank;
    SmartFluidTankBehaviour purifiedTank;

    public SandFilterTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.dirtyTank = SmartFluidTankBehaviour.single(this, TANK_SIZE);
        behaviours.add(this.dirtyTank);
        this.purifiedTank = SmartFluidTankBehaviour.single(this, TANK_SIZE);
        behaviours.add(this.purifiedTank);
    }

    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().m_82363_(0.0d, -2.0d, 0.0d);
    }

    private boolean trackFoods() {
        return getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent();
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side != null && side.m_122434_() == Direction.Axis.Y) {
            if (side == Direction.DOWN) {
                return this.purifiedTank.getCapability().cast();
            }
            return this.dirtyTank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

    public void tick() {
        super.tick();
        if (!this.f_58857_.m_5776_() && this.dirtyTank.getPrimaryHandler().getFluidAmount() >= ((Number) CommonConfig.SAND_FILTER_MB_PER_TICK.get()).intValue() && this.purifiedTank.getPrimaryHandler().getFluidAmount() < 1000) {
            FluidStack water = this.dirtyTank.getPrimaryHandler().drain(((Number) CommonConfig.SAND_FILTER_MB_PER_TICK.get()).intValue(), IFluidHandler.FluidAction.EXECUTE);
            if (water.getFluid().equals(Fluids.f_76193_)) {
                WaterPurity.addPurity(water, Math.min(WaterPurity.getPurity(water) + ((Number) CommonConfig.SAND_FILTER_FILTRATION_AMOUNT.get()).intValue(), 3));
            }
            this.purifiedTank.getPrimaryHandler().fill(water, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets", new Object[0]);
        CreateLang.translate("gui.goggles.fluid_container", new Object[0]).forGoggles(tooltip);
        int dirtyWaterAmount = this.dirtyTank.getPrimaryHandler().getFluidAmount();
        int purifiedWaterAmount = this.purifiedTank.getPrimaryHandler().getFluidAmount();
        buildTooltip(tooltip, mb, dirtyWaterAmount, this.dirtyTank);
        buildTooltip(tooltip, mb, purifiedWaterAmount, this.purifiedTank);
        if (this.dirtyTank.isEmpty() && this.purifiedTank.isEmpty()) {
            CreateLang.translate("gui.goggles.fluid_container.capacity", new Object[0]).add(CreateLang.number(this.dirtyTank.getPrimaryHandler().getTankCapacity(0)).add(mb).style(ChatFormatting.GOLD)).style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
        }
        return (this.dirtyTank.isEmpty() && this.purifiedTank.isEmpty()) ? false : true;
    }

    private void buildTooltip(List<Component> tooltip, LangBuilder mb, int purifiedWaterAmount, SmartFluidTankBehaviour purifiedTank) {
        if (!purifiedTank.isEmpty()) {
            CreateLang.builder().text(WaterPurity.getPurityText(WaterPurity.getPurity(purifiedTank.getPrimaryHandler().getFluid()))).add(CreateLang.text(" ")).add(CreateLang.fluidName(purifiedTank.getPrimaryHandler().getFluid())).style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.builder().add(CreateLang.number(purifiedWaterAmount).add(mb).style(ChatFormatting.GOLD)).text(ChatFormatting.GRAY, " / ").add(CreateLang.number(purifiedTank.getPrimaryHandler().getCapacity()).add(mb).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
        }
    }
}
