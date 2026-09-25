package dev.anye.mc.reality_value.client.gui.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * AppleSkin风格图形Tooltip渲染器：从右往左画图标条（与原版饥饿条方向一致），
 * 负恢复量用灰色调显示，超过10格显示 "xN" 文字。
 */
public class RestoreTooltipRenderer implements ClientTooltipComponent {
    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(
            RealityValue.MOD_ID, "textures/gui/player_ex_icons.png");
    private static final int TEXT_COLOR = 0xFFAAAAAA;

    private final RestoreTooltip tooltip;

    public RestoreTooltipRenderer(RestoreTooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public int getHeight() {
        return tooltip.getBars().size() * 10 + 2;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int w = 0;
        for (RestoreTooltip.Bar bar : tooltip.getBars()) {
            int bw = bar.bars() * 9;
            if (bar.overflowText() != null) {
                bw += font.width(bar.overflowText());
            }
            w = Math.max(w, bw);
        }
        return w + 2;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, ICONS);

        int rowY = y + 1;
        for (RestoreTooltip.Bar bar : tooltip.getBars()) {
            // 负恢复量（如生食惩罚）用灰色调
            if (bar.value() < 0) {
                RenderSystem.setShaderColor(0.55f, 0.55f, 0.55f, 1.0f);
            }
            // 从左往右画：满格在前、半格在后（满半排序，与AppleSkin tooltip一致）
            int offsetX = x;
            int total = Math.abs(bar.value());
            for (int i = 0; i < bar.bars(); i++) {
                int idx = i * 2 + 1;
                // u=9满图标 / u=18完整半图标
                int u = idx < total ? 9 : 18;
                guiGraphics.blit(ICONS, offsetX, rowY, u, bar.vOffset(), 9, 9, 27, 36);
                offsetX += 9;
            }
            if (bar.overflowText() != null) {
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(offsetX + 2, rowY, 0.0f);
                poseStack.scale(0.75f, 0.75f, 0.75f);
                guiGraphics.drawCenteredString(font, bar.overflowText(), 2, 2, TEXT_COLOR);
                poseStack.popPose();
            }
            if (bar.value() < 0) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
            rowY += 10;
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
