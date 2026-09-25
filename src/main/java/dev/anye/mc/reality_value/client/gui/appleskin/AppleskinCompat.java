package dev.anye.mc.reality_value.client.gui.appleskin;

import com.mojang.datafixers.util.Either;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.client.gui.PlayerExHudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * AppleSkin 联动入口（ThirstWasTaken 同款方式）：
 * - 编译期依赖 AppleSkin，运行时通过 ModList.isLoaded("appleskin") 守卫
 * - 读取 AppleSkin 的 ModConfig 配置开关控制 HUD 预览/缓冲层/Tooltip 显示
 * - 通过 RenderTooltipEvent.GatherComponents 注入图形化恢复量Tooltip
 * - 通过 ClientTickEvent.Pre 按游戏tick更新闪烁透明度
 */
@OnlyIn(Dist.CLIENT)
public class AppleskinCompat {
    private static boolean appleskinLoaded = false;

    public static void init() {
        appleskinLoaded = ModList.get().isLoaded("appleskin");
        NeoForge.EVENT_BUS.register(new AppleskinCompat());
    }

    public static boolean isLoaded() {
        return appleskinLoaded;
    }

    /** HUD 是否显示手持物品恢复量预览（未安装AppleSkin时默认显示） */
    public static boolean showFoodValuesOverlay() {
        return !appleskinLoaded || AppleSkinConfigProxy.showFoodValuesOverlay();
    }

    /** HUD 是否显示缓冲层（精力/免疫力，对应AppleSkin饱和度层） */
    public static boolean showSaturationOverlay() {
        return !appleskinLoaded || AppleSkinConfigProxy.showSaturationOverlay();
    }

    /** 主手无可显示物品时是否检查副手 */
    public static boolean checkOffhand() {
        return !appleskinLoaded || AppleSkinConfigProxy.showFoodValuesOverlayWhenOffhand();
    }

    @SubscribeEvent
    public void onGatherTooltips(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !shouldShowTooltip()) {
            return;
        }
        RestoreTooltip tooltip = new RestoreTooltip(stack);
        if (!tooltip.getBars().isEmpty()) {
            event.getTooltipElements().add(Either.right(tooltip));
        }
    }

    /** 显示条件与AppleSkin一致：始终显示 或 (配置允许 且 按住Shift)；未安装AppleSkin时默认始终显示 */
    private boolean shouldShowTooltip() {
        if (!appleskinLoaded) {
            return true;
        }
        if (AppleSkinConfigProxy.alwaysShowFoodValuesTooltip()) {
            return true;
        }
        return AppleSkinConfigProxy.showFoodValuesInTooltip() && Screen.hasShiftDown();
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        // 每游戏tick更新一次闪烁透明度（与AppleSkin节奏一致）
        PlayerExHudRenderer.tickFlash();
    }

    /** 获取手持物品（优先主手；按AppleSkin配置决定是否回退副手） */
    public static ItemStack getHeldItem(Minecraft mc) {
        ItemStack main = mc.player.getMainHandItem();
        if (!main.isEmpty()) {
            return main;
        }
        if (checkOffhand()) {
            return mc.player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }
}
