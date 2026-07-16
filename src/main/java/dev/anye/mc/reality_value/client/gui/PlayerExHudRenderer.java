package dev.anye.mc.reality_value.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.ClientPlayerExData;
import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class PlayerExHudRenderer {
    private static final ResourceLocation PLAYER_EX_ICONS = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "textures/gui/player_ex_icons.png");

    public static final LayeredDraw.Layer HEALTH_OVERLAY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;

        int health = ClientPlayerExData.getHealth();
        if (health <= 0) return;

        RenderSystem.enableBlend();
        renderHealth(guiGraphics, health);
    };

    public static final LayeredDraw.Layer SANITY_OVERLAY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;

        int sanity = ClientPlayerExData.getSanity();
        if (sanity <= 0) return;

        RenderSystem.enableBlend();
        renderSanity(guiGraphics, sanity);
    };

    public static void renderHealth(GuiGraphics guiGraphics, int health) {
        RenderSystem.setShaderTexture(0, PLAYER_EX_ICONS);

        int left = (guiGraphics.guiWidth() / 2) - 91;
        int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.leftHeight;

        int fullHealthIcons = health / 2;
        int hasHalfHealthIcon = health % 2;

        for (int i = 0; i < PlayerExCap.DefaultMaxHealth / 2; i++) {
            int x = left + (i * 8);
            int y = top;

            guiGraphics.blit(PLAYER_EX_ICONS, x, y, 0, 0, 9, 9, 27, 18);

            if (i < fullHealthIcons) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 9, 0, 9, 9, 27, 18);
            } else if (i == fullHealthIcons && hasHalfHealthIcon > 0) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 18, 0, 5, 9, 27, 18);
            }
        }

        // Increase leftHeight AFTER rendering so other left-side overlays get pushed up
        Minecraft.getInstance().gui.leftHeight += 10;

        RenderSystem.disableBlend();
    }

    public static void renderSanity(GuiGraphics guiGraphics, int sanity) {
        RenderSystem.setShaderTexture(0, PLAYER_EX_ICONS);

        int fullSanityIcons = sanity / 2;
        int hasHalfSanityIcon = sanity % 2;
        int sanityLeft = (guiGraphics.guiWidth() / 2) + 91;
        // Match Thirst mod's position calculation: height - rightHeight (no extra offset)
        int sanityTop = guiGraphics.guiHeight() - Minecraft.getInstance().gui.rightHeight;

        for (int i = 0; i < PlayerExCap.DefaultMaxSanity / 2; i++) {
            int x = sanityLeft - (i * 8) - 9;
            int y = sanityTop;

            guiGraphics.blit(PLAYER_EX_ICONS, x, y, 0, 9, 9, 9, 27, 18);

            if (i < fullSanityIcons) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 9, 9, 9, 9, 27, 18);
            } else if (i == fullSanityIcons && hasHalfSanityIcon > 0) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 18, 9, 5, 9, 27, 18);
            }
        }

        // Increase rightHeight AFTER rendering so other mods' overlays above hunger bar get pushed up
        Minecraft.getInstance().gui.rightHeight += 10;

        RenderSystem.disableBlend();
    }

    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "player_ex_health"), HEALTH_OVERLAY);
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "player_ex_sanity"), SANITY_OVERLAY);
    }
}