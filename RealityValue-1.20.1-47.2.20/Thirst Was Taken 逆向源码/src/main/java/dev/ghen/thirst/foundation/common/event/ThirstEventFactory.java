package dev.ghen.thirst.foundation.common.event;

import net.minecraftforge.common.MinecraftForge;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/common/event/ThirstEventFactory.class */
public class ThirstEventFactory {
    public static void onRegisterThirstValue() {
        MinecraftForge.EVENT_BUS.post(new RegisterThirstValueEvent());
    }
}
