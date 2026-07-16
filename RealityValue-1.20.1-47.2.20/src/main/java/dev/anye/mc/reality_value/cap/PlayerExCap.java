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
import net.minecraft.world.entity.monster.Zombie;
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
    /** 默认最大健康值 */
    public static final int DefaultMaxHealth = 20;
    /** 默认最大理智值 */
    public static final int DefaultMaxSanity = 20;
    /** 低理智时施加的随机负面效果列表 */
    private static final MobEffect[] sanityEffect = {
            MobEffects.MOVEMENT_SLOWDOWN,MobEffects.DARKNESS,MobEffects.BLINDNESS,MobEffects.CONFUSION
    };

    /** 当前健康值 */
    private int health = DefaultMaxHealth,
    /** 当前理智值 */
            sanity = DefaultMaxHealth;
    /** 低健康提示计时器（防止消息刷屏） */
    private int healthMsgTick = 0,
    /** 健康自然回复计时器 */
            healthAddTick = 0,
    /** 低理智状态计时器 */
            sanityLowTick = 0,
    /** 理智提示计时器（防止消息刷屏） */
            sanityMsgTick = 0,
    /** 检查负面方块的计时器（用于累计理智惩罚） */
            sanityCheckBlockTick = 0,
    /** 负面方块检查间隔计数器（每4tick检查一次） */
            sanityCheckBlockTickS = 0,
    /** 睡眠回复理智计时器 */
            sanitySleepTick = 0,
    /** 光源回复理智计时器 */
            sanityLightTick = 0,
    /** 雨中/水中理智惩罚计时器 */
            sanityRainWaterTick = 0,
    /** 理智自然衰减计时器 */
            sanityNaturalDecayTick = 0;
    /** 玩家是否在睡觉 */
    private boolean sleep = false;

    public PlayerExCap(){

    }


    /**
     * 增加健康值
     * @param health 增加的数值
     * @param serverPlayer 服务器玩家实体，用于同步到客户端
     */
    public void addHealth(int health,@Nullable ServerPlayer serverPlayer){
        if (health == 0) return;
        setHealth(getHealth() + health,serverPlayer);
    }

    /**
     * 设置健康值
     * @param health 目标健康值
     * @param serverPlayer 服务器玩家实体，用于同步到客户端
     */
    public void setHealth(int health,@Nullable ServerPlayer serverPlayer) {
        this.health = Math.min(health, DefaultMaxHealth);
        if (serverPlayer != null) sendToClient(serverPlayer);
    }

    /**
     * 获取当前健康值
     */
    public int getHealth() {
        return health;
    }

    /**
     * 增加理智值
     * @param sanity 增加的数值
     * @param serverPlayer 服务器玩家实体，用于同步到客户端
     */
    public void addSanity(int sanity,@Nullable ServerPlayer serverPlayer) {
        if (sanity == 0) return;
        setSanity(getSanity() + sanity,serverPlayer);
    }

    /**
     * 设置理智值
     * @param sanity 目标理智值
     * @param serverPlayer 服务器玩家实体，用于同步到客户端
     */
    public void setSanity(int sanity,@Nullable ServerPlayer serverPlayer) {
        this.sanity = Math.min(sanity, DefaultMaxSanity);
        if (serverPlayer != null) sendToClient(serverPlayer);
    }

    /**
     * 获取当前理智值
     */
    public int getSanity() {
        return sanity;
    }

    /**
     * 将玩家属性重置为默认值
     * @param serverPlayer 服务器玩家实体
     */
    public void setDefault(ServerPlayer serverPlayer){
        setHealth(DefaultMaxHealth,serverPlayer);
        setSanity(DefaultMaxSanity,serverPlayer);
    }



    /**
     * 创建发送给客户端的数据包
     */
    public PlayerExNet.SendToClient packet(){
        return new PlayerExNet.SendToClient(getHealth(),getSanity());
    }

    /**
     * 从源能力复制数据
     * @param source 源能力实例
     * @param serverPlayer 服务器玩家实体，用于同步
     */
    public void copyFrom(PlayerExCap source,ServerPlayer serverPlayer){
        setHealth(source.getHealth(),serverPlayer);
        setSanity(source.getSanity(),serverPlayer);
    }

    /**
     * 保存能力数据到NBT标签
     * @param nbt NBT复合标签
     */
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

    /**
     * 从NBT标签加载能力数据
     * @param nbt NBT复合标签
     */
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

    /**
     * 每tick执行的能力逻辑
     * 处理健康值和理智值的各种增减逻辑
     * @param serverPlayer 服务器玩家实体
     */
    public void tick(ServerPlayer serverPlayer) {
        // 低健康警告：健康值低于6时，每400tick（约20秒）发送一次提示
        if (getHealth() < 6) {
            if (healthMsgTick >= 400) {
                healthMsgTick = 0;
                serverPlayer.sendSystemMessage(Component.translatable("cap.reality_value.health.tip.low"));
            } else healthMsgTick++;
        } else if (getHealth() > 10) {
            // 健康自然回复：健康值在11-19之间时，每6000tick（约5分钟）回复1点健康
            if (healthAddTick >= 6000) {
                healthAddTick = 0;
                if (getHealth() < DefaultMaxHealth) {
                    addHealth(1, serverPlayer);
                }
            } else healthAddTick++;
        }

        // 低理智状态处理：理智值≤6时
        if (getSanity() <= 6) {
            // 施加负面效果：每120tick（约6秒）施加虚弱和随机负面效果
            if (sanityLowTick >= 120) {
                sanityLowTick = 0;
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                serverPlayer.addEffect(new MobEffectInstance(sanityEffect[serverPlayer.getRandom().nextInt(0, 4)], serverPlayer.getRandom().nextInt(1, 5) * 20, 0));
            } else sanityLowTick++;
            // 低理智提示消息：每400tick（约20秒）发送一次
            if (sanityMsgTick >= 400) {
                sanityMsgTick = 0;
                serverPlayer.sendSystemMessage(Component.translatable("cap.reality_value.sanity.tip.low"));
            } else sanityMsgTick++;
        }

        // 负面方块理智惩罚：检测玩家脚下的方块是否在惩罚列表中
        if (sanityCheckBlockTickS >= 4) {
            sanityCheckBlockTickS = 0;
            Holder<Block> blockHolder = serverPlayer.getBlockStateOn().getBlockHolder();
            if (blockHolder.unwrapKey().isPresent()) {
                // 检查是否为直接指定的方块
                if (PlayerExBlockTags.I.contains(blockHolder.unwrapKey().get().location().toString())) {
                    sanityCheckBlockTick++;
                } else {
                    // 检查是否属于指定的方块标签
                    boolean[] skip = {false};
                    blockHolder.getTagKeys().forEach(blockTagKey -> {
                        if (!skip[0] && PlayerExBlockTags.I.contains("#" + blockTagKey.location())) {
                            skip[0] = true;
                            sanityCheckBlockTick++;
                        }
                    });
                }
            }
            // 累计50tick（约2.5秒）后扣除1点理智
            if (sanityCheckBlockTick >= 50) {
                sanityCheckBlockTick = 0;
                addSanity(-1, serverPlayer);
            }
        }else sanityCheckBlockTickS++;

        // 睡眠回复理智：睡觉时每1800tick（约90秒）回复1-3点理智
        if (isSleep()){
            if (serverPlayer.isSleeping()){
                if (sanitySleepTick >= 1800){
                    sanitySleepTick = 0;
                    addSanity(serverPlayer.getRandom().nextInt(1,4),serverPlayer);
                }else sanitySleepTick++;
            }else {
                sanitySleepTick = 0;
                setSleep(false);
            }
        }

        // 光源回复理智：每14400tick（约12分钟）检测周围光源，有光源则回复1-2点理智
        if (sanityLightTick >= 14400){
            sanityLightTick = 0;
            if (!getNearbyLightSources(serverPlayer,8,0).isEmpty()){
                addSanity(serverPlayer.getRandom().nextInt(1,3),serverPlayer);
            }
        }else sanityLightTick++;

        // 雨中/水中理智惩罚：每2400tick（约2分钟）在水中或露天雨天时降低1点理智
        if (sanityRainWaterTick >= 2400){
            sanityRainWaterTick = 0;
            if (serverPlayer.isInWater() || (serverPlayer.level().isRaining() && serverPlayer.level().canSeeSky(serverPlayer.blockPosition())))
                addSanity(-1, serverPlayer);
        }else sanityRainWaterTick++;

        // 理智自然衰减：每7200tick（约6分钟）无条件降低1点理智，并发送提示消息
        if (sanityNaturalDecayTick >= 7200){
            sanityNaturalDecayTick = 0;
            addSanity(-1, serverPlayer);
            serverPlayer.sendSystemMessage(Component.literal("§8[§4!§8] §4你有些累了，理智值下降了"));
        }else sanityNaturalDecayTick++;

    }

    /**
     * 使用物品时触发，检查物品是否有恢复健康/理智的效果
     * @param player 使用的玩家
     * @param item 使用的物品
     */
    public void useItem(ServerPlayer player, @NotNull ItemStack item) {
        // 检查物品是否带有恢复健康的效果标签
        PlayerExDataTags.Range range = PlayerExDataTags.HEALTH_TAGS.getValue(item);
        if (range != null && range.checkNbt(item.getTag()) && range.isEffective()){
            addHealth(range.getValue(),player);
        }
        // 检查物品是否带有恢复理智的效果标签
        range  = PlayerExDataTags.SANITY_TAGS.getValue(item);
        if (range != null && range.checkNbt(item.getTag()) && range.isEffective()){
            addSanity(range.getValue(),player);
        }
    }

    /**
     * 发送数据到客户端进行同步
     * @param serverPlayer 服务器玩家实体
     */
    public void sendToClient(ServerPlayer serverPlayer) {
        Messages.sendToPlayer(packet(), serverPlayer);
    }

    /**
     * 玩家受伤时触发的逻辑
     * 仅当攻击者为僵尸、溺尸或尸壳时才会扣除健康与理智
     * @param serverPlayer 受伤的玩家
     * @param livingEntity 攻击来源
     */
    public void hurt(ServerPlayer serverPlayer, LivingEntity livingEntity) {
        // 仅对僵尸类怪物（僵尸、溺尸、尸壳）生效
        if (!(livingEntity instanceof Zombie)) return;
        // 75%概率损失1-3点健康
        int v = serverPlayer.getRandom().nextInt(1,101);
        if (v < 75) addHealth(-serverPlayer.getRandom().nextInt(1,4),serverPlayer);
        // 30%概率损失1-2点理智
        v = serverPlayer.getRandom().nextInt(1,101);
        if (v < 30) addSanity(-serverPlayer.getRandom().nextInt(1,3),serverPlayer);
    }

    /**
     * 设置玩家是否在睡觉状态
     */
    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    /**
     * 获取玩家是否在睡觉状态
     */
    public boolean isSleep() {
        return sleep;
    }

    /**
     * 获取玩家周围指定范围内的光源位置（排除灵魂光源）
     * 灵魂光源不会回复理智
     * @param player 玩家
     * @param radius 检测半径
     * @param minLight 最小光照等级
     * @return 光源位置列表
     */
    public static List<BlockPos> getNearbyLightSources(Player player, int radius,int minLight) {
        List<BlockPos> lightSources = new ArrayList<>();
        Level level = player.level();
        BlockPos center = player.blockPosition();

        // 遍历玩家周围的方块区域
        for (BlockPos checkPos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(checkPos);
            // 检查光照等级
            if (state.getLightEmission(level, checkPos) > minLight) {
                // 排除灵魂光源（灵魂火把、灵魂灯笼、灵魂营火、灵魂火）
                if (state.is(Blocks.SOUL_TORCH)
                        || state.is(Blocks.SOUL_WALL_TORCH)
                        || state.is(Blocks.SOUL_LANTERN)
                        || state.is(Blocks.SOUL_CAMPFIRE)
                        || state.is(Blocks.SOUL_FIRE)) continue;
                // 排除ID中包含"soul"的方块
                ResourceLocation location = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                if (location != null && location.getPath().contains("soul")) continue;
                lightSources.add(checkPos.immutable());
            }
        }
        return lightSources;
    }
}
