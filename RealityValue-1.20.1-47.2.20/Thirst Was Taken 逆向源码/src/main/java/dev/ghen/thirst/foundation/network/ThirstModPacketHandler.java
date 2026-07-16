package dev.ghen.thirst.foundation.network;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.network.message.DrinkByHandMessage;
import dev.ghen.thirst.foundation.network.message.PlayerThirstSyncMessage;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/network/ThirstModPacketHandler.class */
public class ThirstModPacketHandler {
    private static final String PROTOCOL_VERSION = "0.1.2";
    public static final SimpleChannel INSTANCE;

    static {
        ResourceLocation resourceLocationAsResource = Thirst.asResource("main");
        Supplier supplier = () -> {
            return PROTOCOL_VERSION;
        };
        String str = PROTOCOL_VERSION;
        Predicate predicate = (v1) -> {
            return r2.equals(v1);
        };
        String str2 = PROTOCOL_VERSION;
        INSTANCE = NetworkRegistry.newSimpleChannel(resourceLocationAsResource, supplier, predicate, (v1) -> {
            return r3.equals(v1);
        });
    }

    public static void init() {
        INSTANCE.registerMessage(0, PlayerThirstSyncMessage.class, PlayerThirstSyncMessage::encode, PlayerThirstSyncMessage::decode, PlayerThirstSyncMessage::handle);
        INSTANCE.registerMessage(1, DrinkByHandMessage.class, DrinkByHandMessage::encode, DrinkByHandMessage::decode, DrinkByHandMessage::handle);
    }
}
