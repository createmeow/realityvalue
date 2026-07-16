package dev.ghen.thirst;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.compat.create.CreateRegistry;
import dev.ghen.thirst.compat.create.ponder.ThirstPonderPlugin;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.content.registry.ItemInit;
import dev.ghen.thirst.content.thirst.PlayerThirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.foundation.config.ContainerConfig;
import dev.ghen.thirst.foundation.config.ItemSettingsConfig;
import dev.ghen.thirst.foundation.config.KeyWordConfig;
import dev.ghen.thirst.foundation.gui.ThirstBarRenderer;
import dev.ghen.thirst.foundation.gui.appleskin.HUDOverlayHandler;
import dev.ghen.thirst.foundation.gui.appleskin.TooltipOverlayHandler;
import dev.ghen.thirst.foundation.network.ThirstModPacketHandler;
import dev.ghen.thirst.foundation.tab.ThirstTab;
import java.io.IOException;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Thirst.f0ID)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/Thirst.class */
public class Thirst {

    /* renamed from: ID */
    public static final String f0ID = "thirst";

    public Thirst() throws IOException {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::registerCapabilities);
        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener(ThirstBarRenderer::registerThirstOverlay);
            if (ModList.get().isLoaded("appleskin")) {
                HUDOverlayHandler.init();
                TooltipOverlayHandler.init();
                modBus.addListener(this::onRegisterClientTooltipComponentFactories);
            }
        }
        ItemInit.ITEMS.register(modBus);
        if (ModList.get().isLoaded("create")) {
            CreateRegistry.register();
        }
        ThirstTab.register(modBus);
        ItemSettingsConfig.setup();
        CommonConfig.setup();
        ClientConfig.setup();
        KeyWordConfig.setup();
        ContainerConfig.setup();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        WaterPurity.init();
        ThirstModPacketHandler.init();
        if (ModList.get().isLoaded("coldsweat")) {
            ThirstHelper.shouldUseColdSweatCaps(true);
        }
        if (ModList.get().isLoaded("tombstone")) {
            PlayerThirst.checkTombstoneEffects = true;
        }
        if (ModList.get().isLoaded("vampirism")) {
            PlayerThirst.checkVampirismEffects = true;
        }
        if (ModList.get().isLoaded("farmersdelight")) {
            PlayerThirst.checkFDEffects = true;
        }
        if (ModList.get().isLoaded("bakery")) {
            PlayerThirst.checkLetsDoBakeryEffects = true;
        }
        if (ModList.get().isLoaded("brewery")) {
            PlayerThirst.checkLetsDoBreweryEffects = true;
        }
    }

    private void clientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("create")) {
            event.enqueueWork(() -> {
                new Object() { // from class: dev.ghen.thirst.Thirst.1
                    public void registerPonderPlugin() {
                        PonderIndex.addPlugin(new ThirstPonderPlugin());
                    }
                }.registerPonderPlugin();
            });
        }
        if (ModList.get().isLoaded("vampirism")) {
            ThirstBarRenderer.checkIfPlayerIsVampire = true;
        }
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IThirst.class);
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(f0ID, path);
    }

    private void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        TooltipOverlayHandler.register(event);
    }
}
