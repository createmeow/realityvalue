package dev.anye.mc.reality_value.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.ClientPlayerExData;
import dev.anye.mc.reality_value.cap.PlayerExCap;
import dev.anye.mc.reality_value.client.gui.appleskin.AppleskinCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * HUD渲染器：精力/免疫力作为理智/健康的"AppleSkin风格饱和度"重叠显示。
 * 精力（紫色，纹理自带半透明）显示在理智图标上方，免疫力（绿色，纹理自带半透明）显示在健康图标上方。
 */
public class PlayerExHudRenderer {
    static final ResourceLocation PLAYER_EX_ICONS = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "textures/gui/player_ex_icons.png");

    // 健康条（左侧）+ 免疫力叠加显示（免疫在上层）
    public static final LayeredDraw.Layer HEALTH_OVERLAY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;

        int health = ClientPlayerExData.getHealth();
        int immunity = ClientPlayerExData.getImmunity();
        if (health <= 0 && immunity <= 0) return;

        RenderSystem.enableBlend();
        // 先渲染健康条（底层）
        renderStatBar(guiGraphics, health, PlayerExCap.DefaultMaxHealth, 0, true);
        // 再渲染免疫力覆盖层（同一位置，后渲染=上层；显示状态跟随AppleSkin的饱和度配置）
        if (AppleskinCompat.showSaturationOverlay()) {
            renderBufferOverlay(guiGraphics, immunity, PlayerExCap.DefaultMaxImmunity, 27, true);
        }
        // AppleSkin风格：手持恢复物品时闪烁预览增量（闪烁透明度由AppleskinCompat按tick更新）
        // 注意：不可在此调用 resetFlash()，否则当手持物品只恢复理智/精力时会把健康侧的闪烁清零（反之亦然）
        ItemStack held = AppleskinCompat.getHeldItem(mc);
        float healthRestore = held.isEmpty() ? 0 : PlayerExCap.itemHealthRestore(held);
        float immunityRestore = held.isEmpty() ? 0 : PlayerExCap.itemImmunityRestore(held);
        if (AppleskinCompat.showFoodValuesOverlay() && (healthRestore > 0 || immunityRestore > 0)) {
            renderRestorePreview(guiGraphics, health, PlayerExCap.DefaultMaxHealth, 0, healthRestore, true);
            renderRestorePreview(guiGraphics, immunity, PlayerExCap.DefaultMaxImmunity, 27, immunityRestore, true);
        }
        RenderSystem.disableBlend();
        // 主值与缓冲都渲染完后再递增高度，确保二者重叠在同一行
        mc.gui.leftHeight += 10;
    };

    // 理智条（右侧）+ 精力叠加显示（精力在上层）
    public static final LayeredDraw.Layer SANITY_OVERLAY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;

        int sanity = ClientPlayerExData.getSanity();
        int energy = ClientPlayerExData.getEnergy();
        if (sanity <= 0 && energy <= 0) return;

        RenderSystem.enableBlend();
        // 先渲染理智条（底层）
        renderStatBar(guiGraphics, sanity, PlayerExCap.DefaultMaxSanity, 9, false);
        // 再渲染精力覆盖层（同一位置，后渲染=上层；显示状态跟随AppleSkin的饱和度配置）
        if (AppleskinCompat.showSaturationOverlay()) {
            renderBufferOverlay(guiGraphics, energy, PlayerExCap.DefaultMaxEnergy, 18, false);
        }
        // AppleSkin风格：手持恢复物品时闪烁预览增量（闪烁透明度由AppleskinCompat按tick更新）
        // 注意：不可在此调用 resetFlash()，否则当手持物品只恢复健康/免疫时会把理智侧的闪烁清零（反之亦然）
        ItemStack held = AppleskinCompat.getHeldItem(mc);
        float sanityRestore = held.isEmpty() ? 0 : PlayerExCap.itemSanityRestore(held);
        float energyRestore = held.isEmpty() ? 0 : PlayerExCap.itemEnergyRestore(held);
        if (AppleskinCompat.showFoodValuesOverlay() && (sanityRestore > 0 || energyRestore > 0)) {
            renderRestorePreview(guiGraphics, sanity, PlayerExCap.DefaultMaxSanity, 9, sanityRestore, false);
            renderRestorePreview(guiGraphics, energy, PlayerExCap.DefaultMaxEnergy, 18, energyRestore, false);
        }
        RenderSystem.disableBlend();
        // 主值与缓冲都渲染完后再递增高度，确保二者重叠在同一行
        mc.gui.rightHeight += 10;
    };

    // ==================== AppleSkin 风格恢复量预览 ====================
    private static float flashAlpha = 0.0f;
    private static float unclampedFlashAlpha = 0.0f;
    private static byte alphaDir = 1;

    /**
     * AppleSkin式闪烁透明度（由AppleskinCompat在ClientTickEvent.Pre中每游戏tick调用一次）：
     * -0.5→1.5→-0.5摆动，完整周期32tick（约1.6秒），clamp后乘0.65。
     * 手持物品无任何恢复量时重置闪烁（避免层间互相清零：两个HUD层分别渲染，单侧无恢复不再调resetFlash）。
     */
    public static void tickFlash() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && AppleskinCompat.showFoodValuesOverlay()) {
            ItemStack held = AppleskinCompat.getHeldItem(mc);
            if (!held.isEmpty()
                    && (PlayerExCap.itemHealthRestore(held) > 0 || PlayerExCap.itemImmunityRestore(held) > 0
                    || PlayerExCap.itemSanityRestore(held) > 0 || PlayerExCap.itemEnergyRestore(held) > 0)) {
                unclampedFlashAlpha += alphaDir * 0.125f;
                if (unclampedFlashAlpha >= 1.5f) {
                    alphaDir = -1;
                } else if (unclampedFlashAlpha <= -0.5f) {
                    alphaDir = 1;
                }
                flashAlpha = Math.max(0.0f, Math.min(1.0f, unclampedFlashAlpha)) * 0.65f;
                return;
            }
        }
        flashAlpha = 0.0f;
        unclampedFlashAlpha = 0.0f;
        alphaDir = 1;
    }

    /** 无可渲染的预览物品时重置闪烁状态（ThirstWasTaken同款行为） */
    public static void resetFlash() {
        flashAlpha = 0.0f;
        unclampedFlashAlpha = 0.0f;
        alphaDir = 1;
    }

    /**
     * AppleSkin风格恢复量预览：从当前值位置开始，用闪烁半透明图标画出物品可恢复的增量。
     */
    private static void renderRestorePreview(GuiGraphics guiGraphics, int value, int max, int vOffset,
                                             float restore, boolean leftSide) {
        if (restore <= 0) return;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, flashAlpha);

        int modifiedValue = Math.max(0, Math.min(Math.round(value + restore), max));
        int startBars = Math.max(0, value / 2);
        int endBars = (int) Math.ceil(modifiedValue / 2.0);

        for (int i = startBars; i < endBars && i < max / 2; i++) {
            int x, y;
            if (leftSide) {
                int left = (guiGraphics.guiWidth() / 2) - 91;
                int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.leftHeight;
                x = left + (i * 8);
                y = top;
            } else {
                int right = (guiGraphics.guiWidth() / 2) + 91;
                int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.rightHeight;
                x = right - (i * 8) - 9;
                y = top;
            }

            int idx = i * 2 + 1;
            if (idx < modifiedValue) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 9, vOffset, 9, 9, 27, 36);
            } else if (idx == modifiedValue) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 18, vOffset, 9, 9, 27, 36);
            }
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * 渲染缓冲值覆盖层（AppleSkin风格：叠加在主值图标上方）。
     * 纹理本身已带半透明（精力/免疫力alpha=130），不做额外透明处理。
     * @param vOffset 纹理行偏移（18=精力/紫色，27=免疫力/绿色）
     * @param leftSide true=左侧（健康侧），false=右侧（理智侧）
     */
    private static void renderBufferOverlay(GuiGraphics guiGraphics, int value, int max, int vOffset,
                                           boolean leftSide) {
        if (value <= 0) return;
        RenderSystem.setShaderTexture(0, PLAYER_EX_ICONS);

        int fullIcons = value / 2;
        int hasHalf = value % 2;

        for (int i = 0; i < max / 2; i++) {
            int x, y;
            if (leftSide) {
                int left = (guiGraphics.guiWidth() / 2) - 91;
                int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.leftHeight;
                x = left + (i * 8);
                y = top;
            } else {
                int right = (guiGraphics.guiWidth() / 2) + 91;
                int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.rightHeight;
                x = right - (i * 8) - 9;
                y = top;
            }

            if (i < fullIcons) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 9, vOffset, 9, 9, 27, 36);
            } else if (i == fullIcons && hasHalf > 0) {
                // 完整读取半格图标的9x9区域
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 18, vOffset, 9, 9, 27, 36);
            }
        }
    }

    /**
     * 渲染主值图标条（健康/理智）。
     * @param vOffset 纹理行偏移（0=健康/红色，9=理智/粉色）
     * @param leftSide true=左侧，false=右侧
     */
    private static void renderStatBar(GuiGraphics guiGraphics, int value, int max, int vOffset, boolean leftSide) {
        RenderSystem.setShaderTexture(0, PLAYER_EX_ICONS);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        int fullIcons = value / 2;
        int hasHalf = value % 2;

        for (int i = 0; i < max / 2; i++) {
            int x, y;
            if (leftSide) {
                int left = (guiGraphics.guiWidth() / 2) - 91;
                int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.leftHeight;
                x = left + (i * 8);
                y = top;
            } else {
                int right = (guiGraphics.guiWidth() / 2) + 91;
                int top = guiGraphics.guiHeight() - Minecraft.getInstance().gui.rightHeight;
                x = right - (i * 8) - 9;
                y = top;
            }

            // 空图标
            guiGraphics.blit(PLAYER_EX_ICONS, x, y, 0, vOffset, 9, 9, 27, 36);

            if (i < fullIcons) {
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 9, vOffset, 9, 9, 27, 36);
            } else if (i == fullIcons && hasHalf > 0) {
                // 完整读取半格图标的9x9区域
                guiGraphics.blit(PLAYER_EX_ICONS, x, y, 18, vOffset, 9, 9, 27, 36);
            }
        }
    }

    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH,
                ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "player_ex_health"), HEALTH_OVERLAY);
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "player_ex_sanity"), SANITY_OVERLAY);
    }
}
