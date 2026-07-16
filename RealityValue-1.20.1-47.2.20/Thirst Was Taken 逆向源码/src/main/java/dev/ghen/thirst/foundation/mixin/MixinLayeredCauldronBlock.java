package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayeredCauldronBlock.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinLayeredCauldronBlock.class */
public abstract class MixinLayeredCauldronBlock {
    @Inject(method = {"createBlockStateDefinition"}, at = {@At("HEAD")})
    protected void addPurityBlockState(StateDefinition.Builder<Block, BlockState> p_153549_, CallbackInfo ci) {
        p_153549_.m_61104_(new Property[]{WaterPurity.BLOCK_PURITY});
    }
}
