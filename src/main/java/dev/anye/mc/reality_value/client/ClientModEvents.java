package dev.anye.mc.reality_value.client;

import dev.anye.mc.reality_value.client.gui.ItemUseProgressRenderer;
import dev.anye.mc.reality_value.client.gui.PlayerExHudRenderer;
import dev.anye.mc.reality_value.client.gui.appleskin.AppleskinCompat;
import dev.anye.mc.reality_value.client.gui.appleskin.RestoreTooltip;
import dev.anye.mc.reality_value.client.gui.appleskin.RestoreTooltipRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = "reality_value", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        PlayerExHudRenderer.register(event);
        ItemUseProgressRenderer.register(event);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        AppleskinCompat.init();
    }

    @SubscribeEvent
    public static void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(RestoreTooltip.class, RestoreTooltipRenderer::new);
    }
}
