package dev.anye.mc.reality_value.cap;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class PlayerExNet {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static class SendToClient{
        private final int health,sanity;
        public SendToClient(int health,int sanity){
            this.health = health;
            this.sanity = sanity;
        }
        public SendToClient(FriendlyByteBuf buf){
            this.health = buf.readInt();
            this.sanity = buf.readInt();
        }
        public void toBytes(FriendlyByteBuf buf){
            buf.writeInt(health);
            buf.writeInt(sanity);
        }
        public void handle(Supplier<NetworkEvent.Context> supplier){
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(()->{
                ClientPlayerExData.set(health,sanity);

            });
        }
    }
    public static class SendToServer {
    }





}
