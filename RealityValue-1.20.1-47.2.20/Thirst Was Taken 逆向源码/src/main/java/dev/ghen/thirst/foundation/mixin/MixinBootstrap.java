package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.content.purity.WaterPurity;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Bootstrap.class})
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/mixin/MixinBootstrap.class */
public class MixinBootstrap {
    @Inject(method = {"bootStrap"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/core/cauldron/CauldronInteraction;bootStrap()V", shift = At.Shift.AFTER)}, remap = true)
    private static void modifyCauldronInteractions(CallbackInfo ci) {
        CauldronInteraction.f_175607_.remove(Items.f_42590_);
        CauldronInteraction.f_175607_.put(Items.f_42590_, (blockState, level, pos, player, hand, itemStack) -> {
            if (!level.m_5776_()) {
                Item item = itemStack.m_41720_();
                ItemStack result = PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_);
                WaterPurity.addPurity(result, pos, level);
                player.m_21008_(hand, ItemUtils.m_41813_(itemStack, player, result));
                player.m_36220_(Stats.f_12944_);
                player.m_36246_(Stats.f_12982_.m_12902_(item));
                LayeredCauldronBlock.m_153559_(blockState, level, pos);
                level.m_5594_((Player) null, pos, SoundEvents.f_11770_, SoundSource.BLOCKS, 1.0f, 1.0f);
                level.m_142346_((Entity) null, GameEvent.f_157816_, pos);
            }
            return InteractionResult.m_19078_(level.f_46443_);
        });
        CauldronInteraction.f_175607_.remove(Items.f_42446_);
        CauldronInteraction.f_175607_.put(Items.f_42446_, (blockState2, level2, pos2, player2, hand2, item) -> {
            return fillBucket(blockState2, level2, pos2, player2, hand2, item, WaterPurity.addPurity(new ItemStack(Items.f_42447_), pos2, level2), p_175660_ -> {
                return ((Integer) p_175660_.m_61143_(LayeredCauldronBlock.f_153514_)).intValue() == 3;
            }, SoundEvents.f_11781_);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static InteractionResult fillBucket(BlockState p_175636_, Level p_175637_, BlockPos p_175638_, Player p_175639_, InteractionHand p_175640_, ItemStack p_175641_, ItemStack p_175642_, Predicate<BlockState> p_175643_, SoundEvent p_175644_) {
        if (!p_175643_.test(p_175636_)) {
            return InteractionResult.PASS;
        }
        if (!p_175637_.m_5776_()) {
            Item item = p_175641_.m_41720_();
            p_175639_.m_21008_(p_175640_, ItemUtils.m_41813_(p_175641_, p_175639_, p_175642_));
            p_175639_.m_36220_(Stats.f_12944_);
            p_175639_.m_36246_(Stats.f_12982_.m_12902_(item));
            p_175637_.m_46597_(p_175638_, Blocks.f_50256_.m_49966_());
            p_175637_.m_5594_((Player) null, p_175638_, p_175644_, SoundSource.BLOCKS, 1.0f, 1.0f);
            p_175637_.m_142346_((Entity) null, GameEvent.f_157816_, p_175638_);
        }
        return InteractionResult.m_19078_(p_175637_.f_46443_);
    }
}
