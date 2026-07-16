package dev.ghen.thirst.foundation.network.message;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/network/message/PlayerThirstSyncMessage.class */
public class PlayerThirstSyncMessage {
    public int thirst;
    public int quenched;
    public float exhaustion;
    public boolean enable;

    public PlayerThirstSyncMessage(int thirst, int quenched, float exhaustion, boolean enable) {
        this.thirst = thirst;
        this.quenched = quenched;
        this.exhaustion = exhaustion;
        this.enable = enable;
    }

    public PlayerThirstSyncMessage(boolean enable) {
        this.enable = enable;
    }

    public static void encode(PlayerThirstSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.thirst);
        buffer.writeInt(message.quenched);
        buffer.writeFloat(message.exhaustion);
        buffer.writeBoolean(message.enable);
    }

    public static PlayerThirstSyncMessage decode(FriendlyByteBuf buffer) {
        return new PlayerThirstSyncMessage(buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readBoolean());
    }

    public static void handle(PlayerThirstSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
                    return () -> {
                        ClientThirstSyncMessage.handlePacket(message, contextSupplier);
                    };
                });
            });
        }
        context.setPacketHandled(true);
    }
}
