package dev.anye.mc.reality_value.effect;

import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectRegister {
    public static final DeferredRegister<MobEffect> ITEMS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RealityValue.MOD_ID);

    public static final RegistryObject<SideOfEpinephrineEffect> SideOfEpinephrine = ITEMS.register("side_of_epinephrine",SideOfEpinephrineEffect::new);
    public static final RegistryObject<SideOfAntibioticsEffect> SideOfAntibiotics = ITEMS.register("side_of_antibiotics", SideOfAntibioticsEffect::new);



    public static void reg(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
