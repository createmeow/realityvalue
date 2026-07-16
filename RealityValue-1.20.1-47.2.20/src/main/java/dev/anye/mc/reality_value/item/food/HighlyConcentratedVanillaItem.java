package dev.anye.mc.reality_value.item.food;

import dev.anye.mc.reality_value.cap.PlayerExProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HighlyConcentratedVanillaItem extends Item {
    public static final FoodProperties Food = new FoodProperties.Builder().alwaysEat().build();
    public HighlyConcentratedVanillaItem() {
        super(new Properties().food(Food).stacksTo(32));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.reality_value.highly_concentrated_vanilla"));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer serverPlayer){
            pStack.shrink(1);
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                playerExCap.addSanity(serverPlayer.getRandom().nextInt(2,6),serverPlayer);
            });
            return pStack;
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 64;
    }
}