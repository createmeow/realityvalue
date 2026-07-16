package dev.ghen.thirst.foundation.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/util/MathHelper.class */
public class MathHelper {
    public static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        float f = p_41437_.m_146909_();
        float f1 = p_41437_.m_146908_();
        Vec3 vec3 = p_41437_.m_146892_();
        float f2 = Mth.m_14089_(((-f1) * 0.017453292f) - 3.1415927f);
        float f3 = Mth.m_14031_(((-f1) * 0.017453292f) - 3.1415927f);
        float f4 = -Mth.m_14089_((-f) * 0.017453292f);
        float f5 = Mth.m_14031_((-f) * 0.017453292f);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = p_41437_.m_21051_((Attribute) ForgeMod.BLOCK_REACH.get()).m_22135_();
        Vec3 vec31 = vec3.m_82520_(f6 * d0, f5 * d0, f7 * d0);
        return p_41436_.m_45547_(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
    }

    public static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_) {
        return getPlayerPOVHitResult(p_41436_, p_41437_, ClipContext.Fluid.NONE);
    }
}
