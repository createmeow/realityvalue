package dev.ghen.thirst.content.purity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/purity/FillableWithPurity.class */
public class FillableWithPurity {
    private Block block;

    public FillableWithPurity(Block block) {
        this.block = block;
    }

    public int getPurity(BlockState blockState) {
        if (blockState.m_61138_(WaterPurity.BLOCK_PURITY)) {
            return ((Integer) blockState.m_61143_(WaterPurity.BLOCK_PURITY)).intValue();
        }
        return 3;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
