package dev.anye.mc.reality_value.event;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.PlayerExCap;
import dev.anye.mc.reality_value.cap.PlayerExProvider;
import dev.anye.mc.reality_value.config.PlaceBlockList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = RealityValue.MOD_ID)
public class ForgeEvent {
    private static final Logger LOGGER = LogUtils.getLogger();
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerExCap.class);
    }
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(PlayerExProvider.PlayerExCap).ifPresent(oldStore -> serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(newStore -> newStore.copyFrom(oldStore,serverPlayer)));
            }
        }
    }
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player serverPlayer) {
            if (!serverPlayer.getCapability(PlayerExProvider.PlayerExCap).isPresent()) {
                event.addCapability(new ResourceLocation(RealityValue.MOD_ID, "player_ex"), new PlayerExProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            if (event.player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerSan -> playerSan.tick(serverPlayer));
            }
        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerSan -> playerSan.setDefault(serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerSan -> playerSan.sendToClient(serverPlayer));
        }
    }


    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerSan -> {
                playerSan.useItem(player,event.getItem());
            });
        }
    }
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event){
        if (event.getSource().getEntity() instanceof LivingEntity livingEntity && event.getEntity() instanceof ServerPlayer serverPlayer){
            if (!livingEntity.getType().getCategory().isFriendly()){
                if (!serverPlayer.isDamageSourceBlocked(event.getSource())){
                    serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerSan -> playerSan.hurt(serverPlayer,livingEntity));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFished(ItemFishedEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerSan -> playerSan.addSanity(serverPlayer.getRandom().nextInt(0,3),serverPlayer));
        }
    }
    static BooleanProperty IsPlace = BooleanProperty.create("is.place");
    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getState().getBlock() instanceof FlowerBlock){
            PlaceBlockList.get(event.getLevel().getChunk(event.getPos())).add(event.getPos());
        }
    }
    @SubscribeEvent
    public static void onMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getState().getBlock() instanceof FlowerBlock){
            PlaceBlockList.get(event.getLevel().getChunk(event.getPos())).add(event.getPos());
            event.getReplacedBlockSnapshots().forEach(blockSnapshot -> {
                BlockState blockState = !(event.getEntity() instanceof Player) ? blockSnapshot.getReplacedBlock() : blockSnapshot.getCurrentBlock();
                if (blockState.getBlock() instanceof FlowerBlock){
                    PlaceBlockList.get(event.getLevel().getChunk(blockSnapshot.getPos())).add(blockSnapshot.getPos());
                }
            });
        }
    }
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer){
            if (event.getState().getBlock() instanceof FlowerBlock){
                if (!PlaceBlockList.get(event.getLevel().getChunk(event.getPos())).has(event.getPos())) {
                    serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                        playerExCap.addSanity(serverPlayer.getRandom().nextInt(0, 3), serverPlayer);
                    });
                }
            }
        }
    }
    @SubscribeEvent
    public static void onSleep(PlayerSleepInBedEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                playerExCap.setSleep(true);
            });
        }
    }
    @SubscribeEvent
    public static void onSleep(SleepFinishedTimeEvent event){
        event.getLevel().players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer){
                serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                    if (playerExCap.isSleep()) {
                        playerExCap.setSanity(20,serverPlayer);
                        playerExCap.setSleep(false);
                    }

                });
            }
        });
    }
    @SubscribeEvent
    public static void onSleep(SleepingTimeCheckEvent event){
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            if (event.getResult().equals(Event.Result.DENY)){
                serverPlayer.getCapability(PlayerExProvider.PlayerExCap).ifPresent(playerExCap -> {
                    playerExCap.setSleep(false);
                });
            }
        }
    }
}
