package dev.ghen.thirst.content.thirst;

import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.network.ThirstModPacketHandler;
import dev.ghen.thirst.foundation.network.message.DrinkByHandMessage;
import dev.ghen.thirst.foundation.util.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/thirst/DrinkByHandClient.class */
public class DrinkByHandClient {
    public static void drinkByHand() {
        boolean HandAvailable;
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer localPlayer = mc.f_91074_;
        ClientLevel clientLevel = mc.f_91073_;
        BlockPos blockPos = MathHelper.getPlayerPOVHitResult(clientLevel, localPlayer, ClipContext.Fluid.ANY).m_82425_();
        if (clientLevel.m_6425_(blockPos).m_205070_(FluidTags.f_13131_) && localPlayer.m_6047_() && !localPlayer.m_20147_()) {
            if (!((Boolean) ClientConfig.DRINK_BOTH_HAND_NEEDED.get()).booleanValue()) {
                HandAvailable = localPlayer.m_21120_(InteractionHand.MAIN_HAND).m_41619_();
            } else {
                HandAvailable = localPlayer.m_21120_(InteractionHand.MAIN_HAND).m_41619_() && localPlayer.m_21120_(InteractionHand.OFF_HAND).m_41619_();
            }
            if (HandAvailable) {
                clientLevel.m_6263_(localPlayer, localPlayer.m_20185_(), localPlayer.m_20186_(), localPlayer.m_20189_(), SoundEvents.f_11911_, SoundSource.NEUTRAL, 1.0f, 1.0f);
                ThirstModPacketHandler.INSTANCE.sendToServer(new DrinkByHandMessage(blockPos));
            }
        }
    }
}
