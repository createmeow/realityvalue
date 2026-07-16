package dev.anye.mc.reality_value.item.food;

import dev.anye.mc.reality_value.cap.PlayerExCap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class RumItem extends Item {

    public RumItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.RARE)
                .food(new FoodProperties.Builder()
                        .alwaysEdible()
                        .build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents,
            TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.reality_value.rum"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer serverPlayer) {
            pStack.shrink(1);
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            cap.addSanity(serverPlayer.getRandom().nextInt(6, 15), serverPlayer);
            cap.addThirst(serverPlayer.getRandom().nextInt(3, 7), serverPlayer);

            // 联动 ThirstWasTaken（可选软依赖）
            try {
                Class<?> thirstHelper = Class.forName("dev.ghen.thirst.api.ThirstHelper");
                var method = thirstHelper.getMethod("itemRestoresThirst", ItemStack.class);
                if ((boolean) method.invoke(null, pStack)) {
                    Class<?> playerThirst = Class.forName("dev.ghen.thirst.content.thirst.PlayerThirst");
                    var drinkMethod = playerThirst.getMethod("drink", ItemStack.class, Player.class);
                    drinkMethod.invoke(null, pStack, serverPlayer);
                }
            } catch (Exception ignored) {
                // ThirstWasTaken 未安装，使用自有口渴系统
            }

            return pStack;
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 32;
    }
}