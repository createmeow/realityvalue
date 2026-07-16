package dev.anye.mc.reality_value.item.food;

import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FirstAidInjectionItem extends Item {

    public FirstAidInjectionItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents,
            TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.reality_value.first_aid_injection"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer serverPlayer) {
            pStack.shrink(1);
            serverPlayer.setHealth(serverPlayer.getMaxHealth());
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            cap.addHealth(10, serverPlayer);
            return pStack;
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 64;
    }
}