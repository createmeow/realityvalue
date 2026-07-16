package dev.anye.mc.reality_value.item.food;

import dev.anye.mc.reality_value.cap.PlayerExProvider;
import dev.anye.mc.reality_value.effect.EffectRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AntibioticsItem  extends Item {
    public static final FoodProperties Food = new FoodProperties.Builder().alwaysEat().build();
    public AntibioticsItem() {
        super(new Properties().food(Food).stacksTo(1));
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.reality_value.antibiotics"));
    }


    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof ServerPlayer serverPlayer){
            if (serverPlayer.hasEffect(EffectRegister.SideOfAntibiotics.get())){
                serverPlayer.kill();
                return pStack;
            }
            pStack.shrink(1);
            serverPlayer.addEffect(new MobEffectInstance(EffectRegister.SideOfAntibiotics.get(),600,0));
            serverPlayer.heal(8f);
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                playerExCap.addHealth(serverPlayer.getRandom().nextInt(4,8),serverPlayer);
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
