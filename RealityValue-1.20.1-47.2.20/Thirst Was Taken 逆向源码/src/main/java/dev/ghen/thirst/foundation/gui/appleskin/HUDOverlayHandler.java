package dev.ghen.thirst.foundation.gui.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.gui.ThirstBarRenderer;
import java.util.Random;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import squeek.appleskin.ModConfig;
import squeek.appleskin.util.IntPoint;

@OnlyIn(Dist.CLIENT)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/appleskin/HUDOverlayHandler.class */
public class HUDOverlayHandler {
    private static float unclampedFlashAlpha;
    private static float flashAlpha;
    private static byte alphaDir;
    protected static int foodIconsOffset;
    public static final Vector<IntPoint> foodBarOffsets;
    private static final Random random;
    private static final ResourceLocation modIcons;
    static ResourceLocation THIRST_LEVEL_ELEMENT;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !HUDOverlayHandler.class.desiredAssertionStatus();
        unclampedFlashAlpha = 0.0f;
        flashAlpha = 0.0f;
        alphaDir = (byte) 1;
        foodBarOffsets = new Vector<>();
        random = new Random();
        modIcons = Thirst.asResource("textures/gui/appleskin_icons.png");
        THIRST_LEVEL_ELEMENT = Thirst.asResource("thirst_level");
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new HUDOverlayHandler());
    }

    @SubscribeEvent
    public void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == GuiOverlayManager.findOverlay(THIRST_LEVEL_ELEMENT)) {
            Minecraft mc = Minecraft.m_91087_();
            ForgeGui gui = mc.f_91065_;
            boolean isMounted = mc.f_91074_.m_20202_() instanceof LivingEntity;
            boolean isAlive = mc.f_91074_.m_6084_();
            if (isAlive && ((Boolean) ModConfig.SHOW_FOOD_EXHAUSTION_UNDERLAY.get()).booleanValue() && !isMounted && !mc.f_91066_.f_92062_ && gui.shouldDrawSurvivalElements() && !ThirstBarRenderer.cancelRender.booleanValue()) {
                renderExhaustion(gui, event.getGuiGraphics());
            }
        }
    }

    @SubscribeEvent
    public void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == GuiOverlayManager.findOverlay(THIRST_LEVEL_ELEMENT)) {
            Minecraft mc = Minecraft.m_91087_();
            ForgeGui gui = mc.f_91065_;
            boolean isMounted = mc.f_91074_.m_20202_() instanceof LivingEntity;
            boolean isAlive = mc.f_91074_.m_6084_();
            if (isAlive && !isMounted && !mc.f_91066_.f_92062_ && gui.shouldDrawSurvivalElements() && !ThirstBarRenderer.cancelRender.booleanValue()) {
                renderThirstOverlay(event.getGuiGraphics());
            }
        }
    }

    public static void renderExhaustion(ForgeGui gui, GuiGraphics mStack) {
        foodIconsOffset = gui.rightHeight;
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer localPlayer = mc.f_91074_;
        if (!$assertionsDisabled && localPlayer == null) {
            throw new AssertionError();
        }
        int right = (mc.m_91268_().m_85445_() / 2) + 91 + ((Integer) ClientConfig.THIRST_BAR_X_OFFSET.get()).intValue();
        int top = (mc.m_91268_().m_85446_() - foodIconsOffset) + ((Integer) ClientConfig.THIRST_BAR_Y_OFFSET.get()).intValue();
        float exhaustion = ((IThirst) localPlayer.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null)).getExhaustion();
        drawExhaustionOverlay(exhaustion, mStack, right, top);
    }

    public static void renderThirstOverlay(GuiGraphics guiGraphics) {
        if (!shouldRenderAnyOverlays()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer localPlayer = mc.f_91074_;
        if (!$assertionsDisabled && localPlayer == null) {
            throw new AssertionError();
        }
        IThirst thirstData = (IThirst) localPlayer.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
        int top = (mc.m_91268_().m_85446_() - foodIconsOffset) + ((Integer) ClientConfig.THIRST_BAR_Y_OFFSET.get()).intValue();
        int right = (mc.m_91268_().m_85445_() / 2) + 91 + ((Integer) ClientConfig.THIRST_BAR_X_OFFSET.get()).intValue();
        generateHungerBarOffsets(top, right, mc.f_91065_.m_93079_(), localPlayer);
        if (((Boolean) ModConfig.SHOW_SATURATION_OVERLAY.get()).booleanValue()) {
            drawSaturationOverlay(0.0f, thirstData.getQuenched(), guiGraphics, right, top, 1.0f);
        }
        ItemStack heldItem = localPlayer.m_21205_();
        if (((Boolean) ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get()).booleanValue() && !ThirstHelper.itemRestoresThirst(heldItem)) {
            heldItem = localPlayer.m_21206_();
        }
        boolean shouldRenderHeldItemValues = !heldItem.m_41619_() && ThirstHelper.itemRestoresThirst(heldItem);
        if (!shouldRenderHeldItemValues) {
            resetFlash();
            return;
        }
        ThirstValues thirstValues = new ThirstValues(ThirstHelper.getThirst(heldItem), ThirstHelper.getQuenched(heldItem));
        int drinkThirst = thirstValues.thirst;
        thirstValues.getQuenchedIncrement();
        if (thirstData.getThirst() < 20) {
            drawHungerOverlay(drinkThirst, thirstData.getThirst(), guiGraphics, right, top, flashAlpha);
        }
        if (!ThirstHelper.isFood(heldItem) || localPlayer.m_36324_().m_38702_() < 20) {
            drawSaturationOverlay(thirstValues.quenchedModifier, thirstData.getQuenched(), guiGraphics, right, top, flashAlpha);
        }
    }

    public static void drawSaturationOverlay(float saturationGained, float saturationLevel, GuiGraphics guiGraphics, int right, int top, float alpha) {
        if (saturationLevel + saturationGained < 0.0f) {
            return;
        }
        enableAlpha(alpha);
        RenderSystem.setShaderTexture(0, modIcons);
        float modifiedSaturation = Math.max(0.0f, Math.min(saturationLevel + saturationGained, 20.0f));
        int startSaturationBar = 0;
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0f);
        if (saturationGained != 0.0f) {
            startSaturationBar = (int) Math.max(saturationLevel / 2.0f, 0.0f);
        }
        for (int i = startSaturationBar; i < endSaturationBar; i++) {
            IntPoint offset = foodBarOffsets.get(i);
            if (offset != null) {
                int x = right + offset.x;
                int y = top + offset.y;
                int u = 0;
                float effectiveSaturationOfBar = (modifiedSaturation / 2.0f) - i;
                if (effectiveSaturationOfBar >= 1.0f) {
                    u = 3 * 9;
                } else if (effectiveSaturationOfBar > 0.5d) {
                    u = 2 * 9;
                } else if (effectiveSaturationOfBar > 0.25d) {
                    u = 9;
                }
                guiGraphics.m_280218_(modIcons, x, y, u, 0, 9, 9);
            }
        }
        RenderSystem.setShaderTexture(0, ThirstBarRenderer.MC_ICONS);
        disableAlpha();
    }

    public static void drawHungerOverlay(int hungerRestored, int foodLevel, GuiGraphics guiGraphics, int right, int top, float alpha) {
        if (hungerRestored <= 0) {
            return;
        }
        enableAlpha(alpha);
        RenderSystem.setShaderTexture(0, ThirstBarRenderer.THIRST_ICONS);
        int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));
        int startFoodBars = Math.max(0, foodLevel / 2);
        int endFoodBars = (int) Math.ceil(modifiedFood / 2.0f);
        for (int i = startFoodBars; i < endFoodBars; i++) {
            IntPoint offset = foodBarOffsets.get(i);
            if (offset != null) {
                int x = right + offset.x;
                int y = top + offset.y;
                int v = 3 * 9;
                int u = 5 + (4 * 9);
                if ((i * 2) + 1 == modifiedFood) {
                    u -= 9 - 1;
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                guiGraphics.m_280163_(ThirstBarRenderer.THIRST_ICONS, x, y, u, v, 9, 9, 25, 9);
            }
        }
        disableAlpha();
    }

    public static void drawExhaustionOverlay(float exhaustion, GuiGraphics guiGraphics, int right, int top) {
        RenderSystem.setShaderTexture(0, modIcons);
        float ratio = Math.min(1.0f, Math.max(0.0f, exhaustion / 4.0f));
        int width = (int) (ratio * 81.0f);
        enableAlpha(0.75f);
        guiGraphics.m_280218_(modIcons, right - width, top, 81 - width, 18, width, 9);
        disableAlpha();
        RenderSystem.setShaderTexture(0, ThirstBarRenderer.MC_ICONS);
    }

    public static void enableAlpha(float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.blendFunc(770, 771);
    }

    public static void disableAlpha() {
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        unclampedFlashAlpha += alphaDir * 0.125f;
        if (unclampedFlashAlpha >= 1.5f) {
            alphaDir = (byte) -1;
        } else if (unclampedFlashAlpha <= -0.5f) {
            alphaDir = (byte) 1;
        }
        flashAlpha = Math.max(0.0f, Math.min(1.0f, unclampedFlashAlpha)) * 0.65f;
    }

    public static void resetFlash() {
        flashAlpha = 0.0f;
        unclampedFlashAlpha = 0.0f;
        alphaDir = (byte) 1;
    }

    private static boolean shouldRenderAnyOverlays() {
        return true;
    }

    private static void generateHungerBarOffsets(int top, int right, int ticks, Player player) {
        IThirst thirstData = (IThirst) player.getCapability(ModCapabilities.PLAYER_THIRST).orElse((Object) null);
        float quenched = thirstData.getQuenched();
        int thirst = thirstData.getThirst();
        boolean shouldAnimatedFood = quenched <= 0.0f && ticks % ((thirst * 3) + 1) == 0;
        if (foodBarOffsets.size() != 10) {
            foodBarOffsets.setSize(10);
        }
        for (int i = 0; i < 10; i++) {
            int x = (right - (i * 8)) - 9;
            int y = top;
            if (shouldAnimatedFood) {
                y += random.nextInt(3) - 1;
            }
            IntPoint point = foodBarOffsets.get(i);
            if (point == null) {
                point = new IntPoint();
                foodBarOffsets.set(i, point);
            }
            point.x = x - right;
            point.y = y - top;
        }
    }
}
