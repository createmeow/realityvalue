package dev.anye.mc.reality_value.cap;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.config.PlayerExBlockTags;
import dev.anye.mc.reality_value.config.PlayerExDataTags;
import dev.anye.mc.reality_value.net.Messages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class PlayerExCap {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DefaultMaxHealth = 20;
    public static final int DefaultMaxSanity = 20;
    private static final MobEffect[] sanityEffect = {
            MobEffects.MOVEMENT_SLOWDOWN,MobEffects.DARKNESS,MobEffects.BLINDNESS,MobEffects.CONFUSION
    };

    private int health = DefaultMaxHealth,
            sanity = DefaultMaxHealth;
    private int healthMsgTick = 0,
            healthAddTick = 0,
            sanityLowTick = 0,
            sanityMsgTick = 0,
            sanityCheckBlockTick = 0,
            sanityCheckBlockTickS = 0,
            sanitySleepTick = 0, sanityLightTick = 0;
    private boolean sleep = false;
    public PlayerExCap(){

    }


    public void addHealth(int health,@Nullable ServerPlayer serverPlayer){
        if (health == 0) return;
        setHealth(getHealth() + health,serverPlayer);
    }
    public void setHealth(int health,@Nullable ServerPlayer serverPlayer) {
        this.health = Math.min(health, DefaultMaxHealth);
        if (serverPlayer != null) sendToClient(serverPlayer);
    }
    public int getHealth() {
        return health;
    }

    public void addSanity(int sanity,@Nullable ServerPlayer serverPlayer) {
        if (sanity == 0) return;
        setSanity(getSanity() + sanity,serverPlayer);
    }
    public void setSanity(int sanity,@Nullable ServerPlayer serverPlayer) {
        this.sanity = Math.min(sanity, DefaultMaxSanity);
        if (serverPlayer != null) sendToClient(serverPlayer);
    }
    public int getSanity() {
        return sanity;
    }
    public void setDefault(ServerPlayer serverPlayer){
        setHealth(DefaultMaxHealth,serverPlayer);
        setSanity(DefaultMaxSanity,serverPlayer);
    }



    public PlayerExNet.SendToClient packet(){
        return new PlayerExNet.SendToClient(getHealth(),getSanity());
    }

    public void copyFrom(PlayerExCap source,ServerPlayer serverPlayer){
        setHealth(source.getHealth(),serverPlayer);
        setSanity(source.getSanity(),serverPlayer);
    }

    public void saveNBTData(CompoundTag nbt)
    {
        nbt.putInt("ex.health",getHealth());
        nbt.putInt("ex.health.tick.msg",healthMsgTick);
        nbt.putInt("ex.health.tick.add",healthAddTick);



        nbt.putInt("ex.sanity",getSanity());
        nbt.putInt("ex.sanity.tick.msg",sanityMsgTick);
        nbt.putInt("ex.sanity.tick.low",sanityLowTick);
        nbt.putInt("ex.sanity.tick.check",sanityCheckBlockTick);
        nbt.putInt("ex.sanity.tick.sleep",sanitySleepTick);
        nbt.putInt("ex.sanity.tick.light",sanityLightTick);

    }
    public void loadNBTData(CompoundTag nbt)
    {
        setHealth(nbt.getInt("ex.health"),null);
        healthMsgTick = nbt.getInt("ex.health.tick.msg");
        healthAddTick = nbt.getInt("ex.health.tick.add");

        setSanity(nbt.getInt("ex.sanity"),null);
        sanityMsgTick = nbt.getInt("ex.sanity.tick.msg");
        sanityLowTick = nbt.getInt("ex.sanity.tick.low");
        sanityCheckBlockTick = nbt.getInt("ex.sanity.tick.check");
        sanitySleepTick = nbt.getInt("ex.sanity.tick.sleep");
        sanityLightTick = nbt.getInt("ex.sanity.tick.light");
    }

    public void tick(ServerPlayer serverPlayer) {
        if (getHealth() < 6) {
            if (healthMsgTick >= 400) {
                healthMsgTick = 0;
                serverPlayer.sendSystemMessage(Component.translatable("cap.reality_value.health.tip.low"));
            } else healthMsgTick++;
        } else if (getHealth() > 10) {
            if (healthAddTick >= 6000) {
                healthAddTick = 0;
                if (getHealth() < DefaultMaxHealth) {
                    addHealth(1, serverPlayer);
                }
            } else healthAddTick++;
        }
        if (getSanity() <= 6) {
            if (sanityLowTick >= 120) {
                sanityLowTick = 0;
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                serverPlayer.addEffect(new MobEffectInstance(sanityEffect[serverPlayer.getRandom().nextInt(0, 4)], serverPlayer.getRandom().nextInt(1, 5) * 20, 0));
            } else sanityLowTick++;
            if (sanityMsgTick >= 400) {
                sanityMsgTick = 0;
                serverPlayer.sendSystemMessage(Component.translatable("cap.reality_value.sanity.tip.low"));
            } else sanityMsgTick++;
        }
        if (sanityCheckBlockTickS >= 4) {
            sanityCheckBlockTickS = 0;
            Holder<Block> blockHolder = serverPlayer.getBlockStateOn().getBlockHolder();
            if (blockHolder.unwrapKey().isPresent()) {
                if (PlayerExBlockTags.I.contains(blockHolder.unwrapKey().get().location().toString())) {
                    //addSanity(-1, serverPlayer);
                    sanityCheckBlockTick++;
                } else {
                    boolean[] skip = {false};
                    blockHolder.getTagKeys().forEach(blockTagKey -> {
                        if (!skip[0] && PlayerExBlockTags.I.contains("#" + blockTagKey.location())) {
                            skip[0] = true;
                            sanityCheckBlockTick++;
                        }
                    });
                }
            }
            if (sanityCheckBlockTick >= 50) {
                sanityCheckBlockTick = 0;
                addSanity(-1, serverPlayer);
            }
        }else sanityCheckBlockTickS ++;

        if (isSleep()){
            if (serverPlayer.isSleeping()){
                if (sanitySleepTick >= 1800){
                    sanitySleepTick = 0;
                    addSanity(serverPlayer.getRandom().nextInt(1,4),serverPlayer);
                }else sanitySleepTick ++;
            }else {
                sanitySleepTick = 0;
                setSleep(false);
            }
        }
        if (sanityLightTick >= 14400){
            sanityLightTick = 0;
            if (!getNearbyLightSources(serverPlayer,8,0).isEmpty()){
                addSanity(serverPlayer.getRandom().nextInt(1,3),serverPlayer);
            }
        }else sanityLightTick++;

    }

    public void useItem(ServerPlayer player, @NotNull ItemStack item) {
        PlayerExDataTags.Range range = PlayerExDataTags.HEALTH_TAGS.getValue(item);
        if (range != null && range.checkNbt(item.getTag()) && range.isEffective()){

            addHealth(range.getValue(),player);
        }
        range  = PlayerExDataTags.SANITY_TAGS.getValue(item);
        if (range != null && range.checkNbt(item.getTag()) && range.isEffective()){
            addSanity(range.getValue(),player);
        }
    }

    public void sendToClient(ServerPlayer serverPlayer) {
        Messages.sendToPlayer(packet(), serverPlayer);
    }

    public void hurt(ServerPlayer serverPlayer, LivingEntity livingEntity) {
        int v = serverPlayer.getRandom().nextInt(1,101);
        if (v < 75) addHealth(-serverPlayer.getRandom().nextInt(1,4),serverPlayer);
        v = serverPlayer.getRandom().nextInt(1,101);
        if (v < 30) addSanity(-serverPlayer.getRandom().nextInt(1,3),serverPlayer);
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public boolean isSleep() {
        return sleep;
    }

    public static List<BlockPos> getNearbyLightSources(Player player, int radius,int minLight) {
        List<BlockPos> lightSources = new ArrayList<>();
        Level level = player.level();
        BlockPos center = player.blockPosition();

        for (BlockPos checkPos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(checkPos);
            if (state.getLightEmission(level, checkPos) > minLight) {
                if (state.is(Blocks.SOUL_TORCH)
                        || state.is(Blocks.SOUL_WALL_TORCH)
                        || state.is(Blocks.SOUL_LANTERN)
                        || state.is(Blocks.SOUL_CAMPFIRE)
                        || state.is(Blocks.SOUL_FIRE)) continue;
                ResourceLocation location = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                if (location != null && location.getPath().contains("soul")) continue;
                lightSources.add(checkPos.immutable());
            }
        }
        /*
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    ResourceLocation location = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                    if (location != null && location.getPath().contains("soul")) continue;
                    if (state.is(Blocks.SOUL_TORCH)
                            || state.is(Blocks.SOUL_WALL_TORCH)
                            || state.is(Blocks.SOUL_LANTERN)
                            || state.is(Blocks.SOUL_CAMPFIRE)
                            || state.is(Blocks.SOUL_FIRE)) continue;
                    if (state.getLightEmission(level, checkPos) > light) {
                        lightSources.add(checkPos);
                    }
                }
            }
        }

         */
        return lightSources;
    }
}
