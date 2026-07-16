package dev.anye.mc.reality_value;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.effect.EffectRegister;
import dev.anye.mc.reality_value.item.CreativeTabs;
import dev.anye.mc.reality_value.item.ItemRegister;
import dev.anye.mc.reality_value.lib._File;
import dev.anye.mc.reality_value.loot.LootRegister;
import dev.anye.mc.reality_value.net.Messages;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RealityValue.MOD_ID)
public class RealityValue {
    public static final String MOD_ID = "reality_value";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String CONFIG_DIR = _File.getFileFullPathWithRun("config",MOD_ID);
    public static final String CONFIG_DATA_DIR = _File.getFilePath(CONFIG_DIR,"data");
    public static final String CONFIG_PlaceBlock_DIR = _File.getFilePath(CONFIG_DIR,"PlaceBlock");

    static {
        _File.checkAndCreateDir(CONFIG_DIR);
        _File.checkAndCreateDir(CONFIG_DATA_DIR);
        _File.checkAndCreateDir(CONFIG_PlaceBlock_DIR);
    }
    public RealityValue() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        LootRegister.reg(modEventBus);
        EffectRegister.reg(modEventBus);
        ItemRegister.register(modEventBus);
        CreativeTabs.reg(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        Messages.register();
    }



}
