package dev.ghen.thirst.foundation.network.message;

import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

/* compiled from: PlayerThirstSyncMessage.java */
@OnlyIn(Dist.CLIENT)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/network/message/ClientThirstSyncMessage.class */
class ClientThirstSyncMessage {
    ClientThirstSyncMessage() {
    }

    public static void handlePacket(PlayerThirstSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
        if (localPlayer != null) {
            localPlayer.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(cap -> {
                cap.setThirst(message.thirst);
                cap.setQuenched(message.quenched);
                cap.setExhaustion(message.exhaustion);
                cap.setShouldTickThirst(message.enable);
            });
        }
    }
}
