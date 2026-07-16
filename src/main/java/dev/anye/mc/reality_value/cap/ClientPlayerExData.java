package dev.anye.mc.reality_value.cap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.RealityValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ClientPlayerExData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation PlayerExDataImage_Health_Full = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "textures/cap/player_ex/health_full.png");
    private static final ResourceLocation PlayerExDataImage_Health_Empty = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "textures/cap/player_ex/health_empty.png");
    private static final ResourceLocation PlayerExDataImage_Sanity_Full = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "textures/cap/player_ex/sanity_full.png");
    private static final ResourceLocation PlayerExDataImage_Sanity_Empty = ResourceLocation.fromNamespaceAndPath(RealityValue.MOD_ID, "textures/cap/player_ex/sanity_empty.png");
    private static int health, sanity, thirst;

    public static void set(int newHealth, int newSanity, int newThirst) {
        health = newHealth;
        sanity = newSanity;
        thirst = newThirst;
    }

    public static int getHealth() {
        return health;
    }

    public static int getSanity() {
        return sanity;
    }

    public static int getThirst() {
        return thirst;
    }

    public static void render(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 99);
        poseStack.scale(0.35f, 0.35f, 1f);

        guiGraphics.blit(PlayerExDataImage_Health_Empty, 0, 0, 0, 0, 128, 16, 128, 16);

        int w = Math.max(128 * health / PlayerExCap.DefaultMaxHealth, 0);
        poseStack.pushPose();
        poseStack.translate(128 - w, 0, 0);
        guiGraphics.blit(PlayerExDataImage_Health_Full, 0, 0, 128 - w, 0, w, 16, 128, 16);
        poseStack.popPose();

        guiGraphics.blit(PlayerExDataImage_Sanity_Empty, 0, 24, 0, 0, 128, 16, 128, 16);
        w = Math.max(128 * sanity / PlayerExCap.DefaultMaxSanity, 0);
        poseStack.pushPose();
        poseStack.translate(128 - w, 0, 0);
        guiGraphics.blit(PlayerExDataImage_Sanity_Full, 0, 24, 128 - w, 0, w, 16, 128, 16);
        poseStack.popPose();

        poseStack.popPose();
    }
}