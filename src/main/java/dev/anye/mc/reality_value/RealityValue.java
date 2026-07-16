package dev.anye.mc.reality_value;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.effect.EffectRegister;
import dev.anye.mc.reality_value.item.CreativeTabs;
import dev.anye.mc.reality_value.item.ItemRegister;
import dev.anye.mc.reality_value.lib._File;
import dev.anye.mc.reality_value.loot.LootRegister;
import dev.anye.mc.reality_value.net.Messages;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(RealityValue.MOD_ID)
public class RealityValue {
    public static final String MOD_ID = "reality_value";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String CONFIG_DIR = _File.getFileFullPathWithRun("config", MOD_ID);
    public static final String CONFIG_DATA_DIR = _File.getFilePath(CONFIG_DIR, "data");
    public static final String CONFIG_PlaceBlock_DIR = _File.getFilePath(CONFIG_DIR, "PlaceBlock");

    static {
        _File.checkAndCreateDir(CONFIG_DIR);
        _File.checkAndCreateDir(CONFIG_DATA_DIR);
        _File.checkAndCreateDir(CONFIG_PlaceBlock_DIR);
    }

    public RealityValue(IEventBus modEventBus) {
        modEventBus.addListener(this::registerNetwork);
        LootRegister.reg(modEventBus);
        EffectRegister.reg(modEventBus);
        ItemRegister.register(modEventBus);
        CreativeTabs.reg(modEventBus);
    }

    private void registerNetwork(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                Messages.ExDataPayload.TYPE,
                Messages.ExDataPayload.STREAM_CODEC,
                (payload, context) -> {
                    dev.anye.mc.reality_value.cap.ClientPlayerExData.set(payload.health(), payload.sanity(), payload.thirst());
                });
    }
}