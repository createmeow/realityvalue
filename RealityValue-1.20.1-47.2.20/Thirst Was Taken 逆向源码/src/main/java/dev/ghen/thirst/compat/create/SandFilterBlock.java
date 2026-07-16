package dev.ghen.thirst.compat.create;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/compat/create/SandFilterBlock.class */
public class SandFilterBlock extends Block implements IWrenchable, IBE<SandFilterTileEntity> {
    public SandFilterBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @NotNull
    public VoxelShape m_5940_(@NotNull BlockState p_220053_1_, @NotNull BlockGetter p_220053_2_, @NotNull BlockPos p_220053_3_, @NotNull CollisionContext p_220053_4_) {
        return AllShapes.SPOUT;
    }

    public void m_6402_(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, LivingEntity pPlacer, @NotNull ItemStack pStack) {
        super.m_6402_(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public Class<SandFilterTileEntity> getBlockEntityClass() {
        return SandFilterTileEntity.class;
    }

    public BlockEntityType<? extends SandFilterTileEntity> getBlockEntityType() {
        return (BlockEntityType) CreateRegistry.SAND_FILTER_TE.get();
    }
}
