package dev.anye.mc.reality_value.mixin;

import dev.anye.mc.reality_value.cap.ClientPlayerExData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void rv$render$render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick,
            CallbackInfo ci) {
        ClientPlayerExData.render(pGuiGraphics, this.leftPos + 127, this.height / 2 - 22, pPartialTick);
    }
}