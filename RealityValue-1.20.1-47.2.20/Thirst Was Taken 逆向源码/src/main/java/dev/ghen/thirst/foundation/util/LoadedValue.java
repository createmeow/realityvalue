package dev.ghen.thirst.foundation.util;

import java.util.function.Supplier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/util/LoadedValue.class */
public class LoadedValue<T> {
    T value;
    Supplier<T> valueCreator;

    public LoadedValue(Supplier<T> valueCreator) {
        this.valueCreator = valueCreator;
        this.value = valueCreator.get();
        MinecraftForge.EVENT_BUS.register(this);
    }

    /* renamed from: of */
    public static <V> LoadedValue<V> m0of(Supplier<V> valueCreator) {
        return new LoadedValue<>(valueCreator);
    }

    @SubscribeEvent
    public void onLoaded(ServerStartedEvent event) {
        this.value = this.valueCreator.get();
    }

    public T get() {
        return this.value;
    }
}
