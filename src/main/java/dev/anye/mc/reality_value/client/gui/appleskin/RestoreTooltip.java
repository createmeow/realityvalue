package dev.anye.mc.reality_value.client.gui.appleskin;

import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * AppleSkin风格图形Tooltip数据：每种属性的恢复量渲染为一行图标条。
 * 每格代表2点，恢复量超过10格时显示1格 + "xN"文字。
 */
public class RestoreTooltip implements TooltipComponent {
    /** vOffset=纹理行（0=健康/9=理智/18=精力/27=免疫力），value=取整恢复量 */
    public record Bar(int vOffset, int value, int bars, String overflowText) {
    }

    private final List<Bar> bars = new ArrayList<>();

    public RestoreTooltip(ItemStack stack) {
        addEntry(0, PlayerExCap.itemHealthRestore(stack));
        addEntry(27, PlayerExCap.itemImmunityRestore(stack));
        addEntry(9, PlayerExCap.itemSanityRestore(stack));
        addEntry(18, PlayerExCap.itemEnergyRestore(stack));
    }

    private void addEntry(int vOffset, float value) {
        int rounded = Math.round(value);
        if (rounded == 0) return;
        int count = (int) Math.ceil(Math.abs(rounded) / 2.0f);
        String overflow = null;
        if (count > 10) {
            overflow = "x" + (rounded < 0 ? -1 : 1) * (int) Math.ceil(Math.abs(rounded) / 2.0f);
            count = 1;
        }
        bars.add(new Bar(vOffset, rounded, count, overflow));
    }

    public List<Bar> getBars() {
        return bars;
    }
}
