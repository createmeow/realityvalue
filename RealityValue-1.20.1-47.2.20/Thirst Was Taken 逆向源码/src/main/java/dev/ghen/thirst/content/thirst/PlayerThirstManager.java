package dev.ghen.thirst.content.thirst;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.common.item.DrinkableItem;
import dev.ghen.thirst.foundation.config.CommonConfig;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/thirst/PlayerThirstManager.class */
public class PlayerThirstManager {
    @SubscribeEvent
    public static void attachCapabilityToEntityHandler(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            final IThirst playerThirstCap = new PlayerThirst();
            final LazyOptional<IThirst> capOptional = LazyOptional.of(() -> {
                return playerThirstCap;
            });
            final Capability<IThirst> capability = ModCapabilities.PLAYER_THIRST;
            event.addCapability(Thirst.asResource(Thirst.f0ID), new ICapabilitySerializable<CompoundTag>() { // from class: dev.ghen.thirst.content.thirst.PlayerThirstManager.1
                @Nonnull
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
                    if (cap == capability) {
                        return capOptional.cast();
                    }
                    return LazyOptional.empty();
                }

                /* renamed from: serializeNBT, reason: merged with bridge method [inline-methods] */
                public CompoundTag m7serializeNBT() {
                    return playerThirstCap.serializeNBT();
                }

                public void deserializeNBT(CompoundTag nbt) {
                    playerThirstCap.deserializeNBT(nbt);
                }
            });
        }
    }

    @SubscribeEvent
    public static void drinkByHand(PlayerInteractEvent.RightClickBlock event) {
        if (((Boolean) CommonConfig.CAN_DRINK_BY_HAND.get()).booleanValue() && event.getEntity().m_9236_().f_46443_) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
                return DrinkByHandClient::drinkByHand;
            });
        }
    }

    @SubscribeEvent
    public static void drinkByHand(PlayerInteractEvent.RightClickEmpty event) {
        if (((Boolean) CommonConfig.CAN_DRINK_BY_HAND.get()).booleanValue() && event.getEntity().m_9236_().f_46443_) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
                return DrinkByHandClient::drinkByHand;
            });
        }
    }

    @SubscribeEvent
    public static void drink(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player) || !ThirstHelper.itemRestoresThirst(event.getItem()) || (event.getItem().m_41720_() instanceof PotionItem) || event.getItem().m_41720_().m_41472_() || (event.getItem().m_41720_() instanceof DrinkableItem)) {
            return;
        }
        PlayerThirst.drink(event.getItem(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ServerPlayer serverPlayer = event.player;
            if (serverPlayer instanceof ServerPlayer) {
                ServerPlayer serverPlayer2 = serverPlayer;
                serverPlayer2.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(cap -> {
                    cap.tick(serverPlayer2);
                });
            }
        }
    }

    @SubscribeEvent
    public static void endFix(PlayerEvent.Clone event) {
        if (!event.getEntity().m_9236_().f_46443_) {
            Player oldPlayer = event.getOriginal();
            oldPlayer.reviveCaps();
            if (!event.isWasDeath()) {
                event.getEntity().getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(cap -> {
                    LazyOptional capability = oldPlayer.getCapability(ModCapabilities.PLAYER_THIRST);
                    Objects.requireNonNull(cap);
                    capability.ifPresent(cap::copy);
                });
            } else {
                event.getEntity().getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(cap2 -> {
                    oldPlayer.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(oldCap -> {
                        cap2.setShouldTickThirst(oldCap.getShouldTickThirst());
                    });
                });
            }
            oldPlayer.invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void initDrinks(ServerStartedEvent event) {
        ThirstHelper.init();
    }
}
