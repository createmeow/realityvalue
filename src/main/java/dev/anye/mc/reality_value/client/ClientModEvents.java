package dev.anye.mc.reality_value.client;

import dev.anye.mc.reality_value.client.gui.ItemUseProgressRenderer;
import dev.anye.mc.reality_value.client.gui.PlayerExHudRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = "reality_value", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        PlayerExHudRenderer.register(event);
        ItemUseProgressRenderer.register(event);
    }
}