package dev.ghen.thirst.foundation.gui.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.gui.ThirstBarRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.helpers.KeyHelper;

@OnlyIn(Dist.CLIENT)
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/appleskin/TooltipOverlayHandler.class */
public class TooltipOverlayHandler {
    private static final ResourceLocation modIcons = Thirst.asResource("textures/gui/appleskin_icons.png");
    private static final TextureOffsets normalBarTextureOffsets = new TextureOffsets();
    private static final TextureOffsets rottenBarTextureOffsets;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new TooltipOverlayHandler());
    }

    public static void register(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(FoodTooltip.class, FoodTooltipRenderer::new);
    }

    @SubscribeEvent
    public void gatherTooltips(RenderTooltipEvent.GatherComponents event) {
        if (!event.isCanceled()) {
            ItemStack hoveredStack = event.getItemStack();
            if (shouldShowTooltip(hoveredStack)) {
                FoodTooltip foodTooltip = new FoodTooltip(hoveredStack);
                if (foodTooltip.shouldRenderHungerBars()) {
                    event.getTooltipElements().add(Either.right(foodTooltip));
                }
            }
        }
    }

    private static boolean shouldShowTooltip(ItemStack hoveredStack) {
        if (hoveredStack.m_41619_()) {
            return false;
        }
        boolean shouldShowTooltip = (((Boolean) ModConfig.SHOW_FOOD_VALUES_IN_TOOLTIP.get()).booleanValue() && KeyHelper.isShiftKeyDown()) || ((Boolean) ModConfig.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP.get()).booleanValue();
        if (!shouldShowTooltip) {
            return false;
        }
        return ThirstHelper.itemRestoresThirst(hoveredStack);
    }

    static {
        normalBarTextureOffsets.containerNegativeHunger = 43;
        normalBarTextureOffsets.containerExtraHunger = 133;
        normalBarTextureOffsets.containerNormalHunger = 16;
        normalBarTextureOffsets.containerPartialHunger = 124;
        normalBarTextureOffsets.containerMissingHunger = 34;
        normalBarTextureOffsets.shankMissingFull = 70;
        normalBarTextureOffsets.shankMissingPartial = normalBarTextureOffsets.shankMissingFull + 9;
        normalBarTextureOffsets.shankFull = 52;
        normalBarTextureOffsets.shankPartial = normalBarTextureOffsets.shankFull + 9;
        rottenBarTextureOffsets = new TextureOffsets();
        rottenBarTextureOffsets.containerNegativeHunger = normalBarTextureOffsets.containerNegativeHunger;
        rottenBarTextureOffsets.containerExtraHunger = normalBarTextureOffsets.containerExtraHunger;
        rottenBarTextureOffsets.containerNormalHunger = normalBarTextureOffsets.containerNormalHunger;
        rottenBarTextureOffsets.containerPartialHunger = normalBarTextureOffsets.containerPartialHunger;
        rottenBarTextureOffsets.containerMissingHunger = normalBarTextureOffsets.containerMissingHunger;
        rottenBarTextureOffsets.shankMissingFull = 106;
        rottenBarTextureOffsets.shankMissingPartial = rottenBarTextureOffsets.shankMissingFull + 9;
        rottenBarTextureOffsets.shankFull = 88;
        rottenBarTextureOffsets.shankPartial = rottenBarTextureOffsets.shankFull + 9;
    }

    /* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/appleskin/TooltipOverlayHandler$FoodTooltip.class */
    static class FoodTooltip implements TooltipComponent {
        private FoodValues defaultFood;
        private FoodValues modifiedFood;
        private final int biggestHunger;
        private final float biggestSaturationIncrement;
        private int hungerBars;
        private String hungerBarsText;
        private int saturationBars;
        private String saturationBarsText;
        private final ItemStack itemStack;

        FoodTooltip(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.biggestHunger = ThirstHelper.getThirst(itemStack);
            this.biggestSaturationIncrement = ThirstHelper.getQuenched(itemStack);
            this.hungerBars = (int) Math.ceil(Math.abs(this.biggestHunger) / 2.0f);
            if (this.hungerBars > 10) {
                this.hungerBarsText = "x" + ((this.biggestHunger < 0 ? -1 : 1) * this.hungerBars);
                this.hungerBars = 1;
            }
            this.saturationBars = (int) Math.ceil(Math.abs(this.biggestSaturationIncrement) / 2.0f);
            if (this.saturationBars > 10 || this.saturationBars == 0) {
                this.saturationBarsText = "x" + ((this.biggestSaturationIncrement < 0.0f ? -1 : 1) * this.saturationBars);
                this.saturationBars = 1;
            }
        }

        boolean shouldRenderHungerBars() {
            return this.hungerBars > 0;
        }
    }

    /* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/appleskin/TooltipOverlayHandler$TextureOffsets.class */
    static class TextureOffsets {
        int containerNegativeHunger;
        int containerExtraHunger;
        int containerNormalHunger;
        int containerPartialHunger;
        int containerMissingHunger;
        int shankMissingFull;
        int shankMissingPartial;
        int shankFull;
        int shankPartial;

        TextureOffsets() {
        }
    }

    /* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/appleskin/TooltipOverlayHandler$FoodTooltipRenderer.class */
    static class FoodTooltipRenderer implements ClientTooltipComponent {
        private final FoodTooltip foodTooltip;

        FoodTooltipRenderer(FoodTooltip foodTooltip) {
            this.foodTooltip = foodTooltip;
        }

        public int m_142103_() {
            return 20;
        }

        public int m_142069_(@NotNull Font font) {
            int hungerBarsWidth = this.foodTooltip.hungerBars * 9;
            if (this.foodTooltip.hungerBarsText != null) {
                hungerBarsWidth += font.m_92895_(this.foodTooltip.hungerBarsText);
            }
            int saturationBarsWidth = this.foodTooltip.saturationBars * 7;
            if (this.foodTooltip.saturationBarsText != null) {
                saturationBarsWidth += font.m_92895_(this.foodTooltip.saturationBarsText);
            }
            return Math.max(hungerBarsWidth, saturationBarsWidth) + 2;
        }

        public void m_183452_(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
            ItemStack itemStack = this.foodTooltip.itemStack;
            Minecraft mc = Minecraft.m_91087_();
            if (!TooltipOverlayHandler.shouldShowTooltip(itemStack)) {
                return;
            }
            Screen gui = mc.f_91080_;
            if (gui == null) {
                return;
            }
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int thirst = ThirstHelper.getThirst(itemStack);
            int offsetX = x + ((this.foodTooltip.hungerBars - 1) * 9);
            RenderSystem.setShaderTexture(0, ThirstBarRenderer.THIRST_ICONS);
            for (int i = 0; i < this.foodTooltip.hungerBars * 2; i += 2) {
                if (thirst == i + 1) {
                    guiGraphics.m_280398_(ThirstBarRenderer.THIRST_ICONS, offsetX, y, 0, 8.0f, 0.0f, 9, 9, 25, 9);
                } else {
                    guiGraphics.m_280398_(ThirstBarRenderer.THIRST_ICONS, offsetX, y, 0, 16.0f, 0.0f, 9, 9, 25, 9);
                }
                offsetX -= 9;
            }
            if (this.foodTooltip.hungerBarsText != null) {
                PoseStack poseStack = guiGraphics.m_280168_();
                poseStack.m_85836_();
                poseStack.m_252880_(offsetX + 18, y, 0.0f);
                poseStack.m_85841_(0.75f, 0.75f, 0.75f);
                guiGraphics.m_280137_(font, this.foodTooltip.hungerBarsText, 2, 2, -5592406);
                poseStack.m_85849_();
            }
            int offsetY = y + 10;
            float modifiedSaturationIncrement = ThirstHelper.getQuenched(itemStack);
            float absModifiedSaturationIncrement = Math.abs(modifiedSaturationIncrement);
            int offsetX2 = x + ((this.foodTooltip.saturationBars - 1) * 7);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, TooltipOverlayHandler.modIcons);
            for (int i2 = 0; i2 < this.foodTooltip.saturationBars * 2; i2 += 2) {
                float effectiveSaturationOfBar = (absModifiedSaturationIncrement - i2) / 2.0f;
                boolean shouldBeFaded = absModifiedSaturationIncrement <= ((float) i2);
                if (shouldBeFaded) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
                }
                guiGraphics.m_280398_(TooltipOverlayHandler.modIcons, offsetX2, offsetY, 0, effectiveSaturationOfBar >= 1.0f ? 21.0f : ((double) effectiveSaturationOfBar) > 0.5d ? 14.0f : ((double) effectiveSaturationOfBar) > 0.25d ? 7.0f : effectiveSaturationOfBar > 0.0f ? 0.0f : 28.0f, modifiedSaturationIncrement >= 0.0f ? 27.0f : 34.0f, 7, 7, 256, 256);
                if (shouldBeFaded) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
                offsetX2 -= 7;
            }
            if (this.foodTooltip.saturationBarsText != null) {
                PoseStack poseStack2 = guiGraphics.m_280168_();
                poseStack2.m_85836_();
                poseStack2.m_252880_(offsetX2 + 14, offsetY, 0.0f);
                poseStack2.m_85841_(0.75f, 0.75f, 0.75f);
                guiGraphics.m_280137_(font, this.foodTooltip.saturationBarsText, 2, 1, -5592406);
                poseStack2.m_85849_();
            }
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, TooltipOverlayHandler.modIcons);
            RenderSystem.disableDepthTest();
        }
    }
}
