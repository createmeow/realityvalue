package dev.anye.mc.reality_value.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.ClientPlayerExData;
import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public class PlayerExHudRenderer {
    private static final ResourceLocation PLAYER_EX_ICONS = new ResourceLocation(RealityValue.MOD_ID, "textures/gui/player_ex_icons.png");
    private static final ResourceLocation MC_ICONS = new ResourceLocation("textures/gui/icons.png");

    public static final IGuiOverlay HEALTH_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;
        
        int health = ClientPlayerExData.getHealth();
        if (health <= 0) return;

        gui.setupOverlayRenderState(true, false);
        renderHealth(gui, screenWidth, screenHeight, guiGraphics, health);
    };

    public static final IGuiOverlay SANITY_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;
        
        int sanity = ClientPlayerExData.getSanity();
        if (sanity <= 0) return;

        gui.setupOverlayRenderState(true, false);
        renderSanity(gui, screenWidth, screenHeight, guiGraphics, sanity);
    };

    public static void renderHealth(ForgeGui gui, int width, int height, GuiGraphics guiGraphics, int health) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, PLAYER_EX_ICONS);

        int left = (width / 2) - 91;
        int top = height - gui.rightHeight - 10;

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

        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, MC_ICONS);
    }

    public static void renderSanity(ForgeGui gui, int width, int height, GuiGraphics guiGraphics, int sanity) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, PLAYER_EX_ICONS);

        int fullSanityIcons = sanity / 2;
        int hasHalfSanityIcon = sanity % 2;
        int sanityLeft = (width / 2) + 91;
        // Match Thirst mod's position calculation: height - rightHeight (no extra offset)
        int sanityTop = height - gui.rightHeight;
        
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
        gui.rightHeight += 10;

        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, MC_ICONS);
    }

    public static void register(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "player_ex_health", HEALTH_OVERLAY);
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "player_ex_sanity", SANITY_OVERLAY);
    }
}
