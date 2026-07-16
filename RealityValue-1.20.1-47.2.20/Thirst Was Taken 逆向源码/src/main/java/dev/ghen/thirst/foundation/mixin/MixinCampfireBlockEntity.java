package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CampfireBlockEntity.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinCampfireBlockEntity.class */
public class MixinCampfireBlockEntity {
    @Inject(method = {"particleTick"}, at = {@At("HEAD")}, cancellable = true)
    private static void waterVapour(Level level, BlockPos pos, BlockState blockState, CampfireBlockEntity campfire, CallbackInfo ci) {
        RandomSource random = level.m_213780_();
        int l = blockState.m_61143_(CampfireBlock.f_51230_).m_122416_();
        boolean cancel = false;
        for (int i = 0; i < campfire.m_59065_().size(); i++) {
            ItemStack itemstack = (ItemStack) campfire.m_59065_().get(i);
            if (WaterPurity.isWaterFilledContainer(itemstack)) {
                cancel = true;
                if (random.m_188501_() < 0.2f) {
                    Direction direction = Direction.m_122407_(Math.floorMod(i + l, 4));
                    double d0 = ((pos.m_123341_() + 0.5d) - (direction.m_122429_() * 0.3125f)) + (direction.m_122427_().m_122429_() * 0.3125f);
                    double d1 = pos.m_123342_() + 0.6d;
                    double d2 = ((pos.m_123343_() + 0.5d) - (direction.m_122431_() * 0.3125f)) + (direction.m_122427_().m_122431_() * 0.3125f);
                    level.m_7106_(ParticleTypes.f_123806_, d0, d1, d2, 0.0d, 0.001d, 0.0d);
                }
            }
        }
        if (cancel) {
            if (random.m_188501_() < 0.11f) {
                for (int i2 = 0; i2 < random.m_188503_(2) + 2; i2++) {
                    CampfireBlock.m_51251_(level, pos, ((Boolean) blockState.m_61143_(CampfireBlock.f_51228_)).booleanValue(), false);
                }
            }
            ci.cancel();
        }
    }
}
