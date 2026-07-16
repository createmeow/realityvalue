package dev.anye.mc.reality_value.effect;

import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegister {
    public static final DeferredRegister<MobEffect> ITEMS = DeferredRegister.create(Registries.MOB_EFFECT,
            RealityValue.MOD_ID);

    public static void reg(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}