package dev.ghen.thirst.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.util.Helper;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/ThirstBarRenderer.class */
public class ThirstBarRenderer {
    public static IThirst PLAYER_THIRST = null;
    public static ResourceLocation THIRST_ICONS = Thirst.asResource("textures/gui/thirst_icons.png");
    public static final ResourceLocation MC_ICONS = new ResourceLocation("textures/gui/icons.png");
    public static Boolean cancelRender = false;
    public static Boolean checkIfPlayerIsVampire = false;
    static Minecraft minecraft = Minecraft.m_91087_();
    protected static final RandomSource random = RandomSource.m_216327_();
    public static IGuiOverlay THIRST_OVERLAY = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
        boolean isMounted = minecraft.f_91074_.m_20202_() instanceof LivingEntity;
        cancelRender = false;
        if (!isMounted && !minecraft.f_91066_.f_92062_ && gui.shouldDrawSurvivalElements()) {
            if (checkIfPlayerIsVampire.booleanValue() && Helper.isVampire(gui.getMinecraft().f_91074_)) {
                cancelRender = true;
            } else if (minecraft.f_91074_.m_6084_() && !((IThirst) minecraft.f_91074_.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null)).getShouldTickThirst()) {
                cancelRender = true;
            } else {
                gui.setupOverlayRenderState(true, false);
                render(gui, screenWidth, screenHeight, poseStack);
            }
        }
    };

    public static void registerThirstOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "thirst_level", THIRST_OVERLAY);
    }

    public static void render(ForgeGui gui, int width, int height, GuiGraphics guiGraphics) {
        minecraft.m_91307_().m_6180_(Thirst.f0ID);
        if (PLAYER_THIRST == null || minecraft.f_91074_.f_19797_ % 40 == 0) {
            PLAYER_THIRST = (IThirst) minecraft.f_91074_.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, THIRST_ICONS);
        int left = (width / 2) + 91 + ((Integer) ClientConfig.THIRST_BAR_X_OFFSET.get()).intValue();
        int top = (height - gui.rightHeight) + ((Integer) ClientConfig.THIRST_BAR_Y_OFFSET.get()).intValue();
        gui.rightHeight += 10;
        int level = PLAYER_THIRST.getThirst();
        for (int i = 0; i < 10; i++) {
            int idx = (i * 2) + 1;
            int x = (left - (i * 8)) - 9;
            int y = top;
            if (PLAYER_THIRST.getQuenched() <= 0.0f && gui.m_93079_() % ((level * 3) + 1) == 0) {
                y = top + (random.m_188503_(3) - 1);
            }
            guiGraphics.m_280163_(THIRST_ICONS, x, y, 0.0f, 0.0f, 9, 9, 25, 9);
            if (idx < level) {
                guiGraphics.m_280163_(THIRST_ICONS, x, y, 16.0f, 0.0f, 9, 9, 25, 9);
            } else if (idx == level) {
                guiGraphics.m_280163_(THIRST_ICONS, x, y, 8.0f, 0.0f, 9, 9, 25, 9);
            }
        }
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, MC_ICONS);
        minecraft.m_91307_().m_7238_();
    }
}
