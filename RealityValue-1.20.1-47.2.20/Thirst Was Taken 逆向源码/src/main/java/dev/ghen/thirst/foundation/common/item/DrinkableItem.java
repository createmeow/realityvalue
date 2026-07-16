package dev.ghen.thirst.foundation.common.item;

import dev.ghen.thirst.content.thirst.PlayerThirst;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/common/item/DrinkableItem.class */
public class DrinkableItem extends Item {
    private int drinkDuration;
    private Item container;

    public DrinkableItem() {
        super(new Item.Properties().m_41487_(64));
        this.drinkDuration = 32;
    }

    public DrinkableItem(Item.Properties p_41383_) {
        super(p_41383_);
        this.drinkDuration = 32;
    }

    public DrinkableItem setContainer(Item item) {
        this.container = item;
        return this;
    }

    public DrinkableItem setDrinkDuration(int duration) {
        this.drinkDuration = duration;
        return this;
    }

    @NotNull
    public ItemStack m_5922_(@NotNull ItemStack item, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.f_10592_.m_23682_((ServerPlayer) player, item);
        }
        if (player != null) {
            PlayerThirst.drink(item, player);
        }
        if (player != null) {
            player.m_36246_(Stats.f_12982_.m_12902_(this));
            if (!player.m_150110_().f_35937_) {
                item.m_41774_(1);
            }
        }
        if (player == null || !player.m_150110_().f_35937_) {
            if (item.m_41619_()) {
                return new ItemStack(this.container);
            }
            if (player != null) {
                ItemStack container = new ItemStack(this.container);
                if (!player.m_150109_().m_36054_(container)) {
                    player.m_36176_(container, false);
                }
            }
        }
        level.m_220400_(entity, GameEvent.f_223697_, entity.m_146892_());
        return item;
    }

    public int m_8105_(@NotNull ItemStack p_43001_) {
        return this.drinkDuration;
    }

    @NotNull
    public UseAnim m_6164_(@NotNull ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    @NotNull
    public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level p_42993_, @NotNull Player p_42994_, @NotNull InteractionHand p_42995_) {
        return ItemUtils.m_150959_(p_42993_, p_42994_, p_42995_);
    }
}
