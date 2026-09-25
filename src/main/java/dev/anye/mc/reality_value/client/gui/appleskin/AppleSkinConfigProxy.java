package dev.anye.mc.reality_value.client.gui.appleskin;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import squeek.appleskin.ModConfig;

/**
 * AppleSkin 配置读取代理（ThirstWasTaken 同款联动方式）。
 * 仅在 ModList.isLoaded("appleskin") 为 true 时才会被调用（调用点均有守卫），
 * 避免未安装 AppleSkin 时的 NoClassDefFoundError。
 */
@OnlyIn(Dist.CLIENT)
public class AppleSkinConfigProxy {
    /** HUD 是否显示手持物品恢复量预览 */
    public static boolean showFoodValuesOverlay() {
        return ModConfig.SHOW_FOOD_VALUES_OVERLAY.get();
    }

    /** HUD 是否显示缓冲层（对应 AppleSkin 饱和度层） */
    public static boolean showSaturationOverlay() {
        return ModConfig.SHOW_SATURATION_OVERLAY.get();
    }

    /** Tooltip 是否随 shift 键显示恢复量 */
    public static boolean showFoodValuesInTooltip() {
        return ModConfig.SHOW_FOOD_VALUES_IN_TOOLTIP.get();
    }

    /** Tooltip 是否始终显示恢复量 */
    public static boolean alwaysShowFoodValuesTooltip() {
        return ModConfig.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP.get();
    }

    /** 主手无可显示物品时是否检查副手 */
    public static boolean showFoodValuesOverlayWhenOffhand() {
        return ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get();
    }
}
