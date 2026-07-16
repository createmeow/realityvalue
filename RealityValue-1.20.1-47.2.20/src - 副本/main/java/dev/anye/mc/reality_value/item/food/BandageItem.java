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

public class BandageItem extends Item {
    public static final FoodProperties Food = new FoodProperties.Builder().alwaysEat().build();
    public BandageItem() {
        super(new Properties().food(Food).stacksTo(16));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.reality_value.bandage"));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer serverPlayer){
            pStack.shrink(1);
            serverPlayer.heal(4f);
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                playerExCap.addHealth(serverPlayer.getRandom().nextInt(1,4),serverPlayer);
            });
            return pStack;
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 48;
    }
}
