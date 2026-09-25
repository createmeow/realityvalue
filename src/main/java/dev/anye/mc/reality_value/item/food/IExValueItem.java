package dev.anye.mc.reality_value.item.food;

import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.server.level.ServerPlayer;

/**
 * 模组自带"药品/消耗品"恢复量接口：单一数据源。
 * HUD 手持补充量预览与物品框恢复量图形Tooltip均读取此接口定义，
 * 因此这些物品无需写入 JSON 配置（避免 useItem 流程导致双重恢复）。
 */
public interface IExValueItem {
    /** 原版生命恢复量（0=不恢复） */
    default float healAmount() {
        return 0;
    }

    /** 是否回满原版生命（如急救针） */
    default boolean fullHeal() {
        return false;
    }

    /** 模组健康恢复范围 [min, max]（闭区间），null=不恢复 */
    default int[] healthRestore() {
        return null;
    }

    /** 模组理智恢复范围 [min, max]（闭区间），null=不恢复 */
    default int[] sanityRestore() {
        return null;
    }

    /** 模组免疫力恢复范围 [min, max]（闭区间），null=不恢复 */
    default int[] immunityRestore() {
        return null;
    }

    /** 模组精力恢复范围 [min, max]（闭区间），null=不恢复 */
    default int[] energyRestore() {
        return null;
    }

    /** 使用完毕时按接口定义应用全部恢复效果：100%恢复对应最大值（实际恢复与显示预览共用同一数据） */
    default void applyExRestore(ServerPlayer player) {
        if (fullHeal()) {
            player.setHealth(player.getMaxHealth());
        } else if (healAmount() > 0) {
            player.heal(healAmount());
        }
        PlayerExCap cap = PlayerExCap.get(player);
        int[] range = healthRestore();
        if (range != null) cap.addHealth(range[1], player);
        range = sanityRestore();
        if (range != null) cap.addSanity(range[1], player);
        range = immunityRestore();
        if (range != null) cap.addImmunity(range[1], player);
        range = energyRestore();
        if (range != null) cap.addEnergy(range[1], player);
    }
}
