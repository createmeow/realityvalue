package dev.anye.mc.reality_value.cap;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.config.PlayerExBlockTags;
import dev.anye.mc.reality_value.config.PlayerExDataTags;
import dev.anye.mc.reality_value.config.PlayerExSatTags;
import dev.anye.mc.reality_value.net.Messages;
import dev.anye.mc.reality_value.item.food.IExValueItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class PlayerExCap {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String NBT_KEY = "RealityValueExData";
    public static final int DefaultMaxHealth = 20;
    public static final int DefaultMaxSanity = 20;
    public static final int DefaultMaxThirst = 20;
    public static final int DefaultMaxEnergy = 20;
    public static final int DefaultMaxImmunity = 20;
    private static final Holder<MobEffect>[] sanityEffect = new Holder[]{
            MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DARKNESS, MobEffects.BLINDNESS, MobEffects.CONFUSION
    };

    private int health = DefaultMaxHealth,
            sanity = DefaultMaxHealth,
            thirst = DefaultMaxThirst;
    // 精力：理智的"饱和度"缓冲，扣理智时优先扣精力
    private int energy = DefaultMaxEnergy;
    // 免疫力：健康的"饱和度"缓冲，扣健康时优先扣免疫力
    private int immunity = DefaultMaxImmunity;
    private int healthMsgTick = 0,
            healthAddTick = 0,
            healthNauseaTick = 0,
            sanityLowTick = 0,
            sanityMsgTick = 0,
            sanityWeaponDamageTick = 0,
            sanityCheckBlockTick = 0,
            sanityCheckBlockTickS = 0,
            sanitySleepTick = 0, sanityLightTick = 0,
            sanityRainWaterTick = 0, sanityNaturalDecayTick = 0;
    private boolean sleep = false;

    private PlayerExCap() {
    }

    public static PlayerExCap get(ServerPlayer player) {
        CompoundTag ourData = player.getPersistentData().getCompound(NBT_KEY);
        PlayerExCap cap = new PlayerExCap();
        cap.loadNBTData(ourData);
        return cap;
    }

    public static void save(ServerPlayer player, PlayerExCap cap) {
        CompoundTag nbt = new CompoundTag();
        cap.saveNBTData(nbt);
        player.getPersistentData().put(NBT_KEY, nbt);
    }

    // ==================== 健康（Health）+ 免疫力缓冲 ====================

    /**
     * 扣健康时优先扣免疫力缓冲，免疫力耗尽后才扣健康。
     * 恢复健康时直接加健康值。
     */
    public void addHealth(int amount, @Nullable ServerPlayer serverPlayer) {
        if (amount == 0) return;
        if (amount < 0) {
            int deduction = -amount;
            int immunityDeduct = Math.min(immunity, deduction);
            immunity -= immunityDeduct;
            deduction -= immunityDeduct;
            health = Math.max(0, health - deduction);
        } else {
            health = Math.min(health + amount, DefaultMaxHealth);
        }
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public void setHealth(int health, @Nullable ServerPlayer serverPlayer) {
        this.health = Math.min(health, DefaultMaxHealth);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public int getHealth() {
        return health;
    }

    // ==================== 理智（Sanity）+ 精力缓冲 ====================

    /**
     * 扣理智时优先扣精力缓冲，精力耗尽后才扣理智。
     * 恢复理智时直接加理智值。
     */
    public void addSanity(int amount, @Nullable ServerPlayer serverPlayer) {
        if (amount == 0) return;
        if (amount < 0) {
            int deduction = -amount;
            int energyDeduct = Math.min(energy, deduction);
            energy -= energyDeduct;
            deduction -= energyDeduct;
            sanity = Math.max(0, sanity - deduction);
        } else {
            sanity = Math.min(sanity + amount, DefaultMaxSanity);
        }
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public void setSanity(int sanity, @Nullable ServerPlayer serverPlayer) {
        this.sanity = Math.min(sanity, DefaultMaxSanity);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public int getSanity() {
        return sanity;
    }

    // ==================== 口渴（Thirst） ====================

    public void addThirst(int thirst, @Nullable ServerPlayer serverPlayer) {
        if (thirst == 0) return;
        setThirst(getThirst() + thirst, serverPlayer);
    }

    public void setThirst(int thirst, @Nullable ServerPlayer serverPlayer) {
        this.thirst = Math.min(thirst, DefaultMaxThirst);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public int getThirst() {
        return thirst;
    }

    // ==================== 精力（Energy）= 理智的饱和度 ====================

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy, @Nullable ServerPlayer serverPlayer) {
        this.energy = Math.min(Math.max(energy, 0), DefaultMaxEnergy);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public void addEnergy(int energy, @Nullable ServerPlayer serverPlayer) {
        if (energy == 0) return;
        setEnergy(getEnergy() + energy, serverPlayer);
    }

    /**
     * 消耗精力物品：恢复精力（缓冲）和理智（主值）。
     * 仿照原版食物恢复：value=精力恢复量，saturationModifier=理智恢复系数。
     */
    public void consumeEnergy(int value, float sanityModifier, @Nullable ServerPlayer serverPlayer) {
        energy = Math.min(energy + value, DefaultMaxEnergy);
        int sanityGain = Math.round(sanityModifier * value);
        if (sanityGain > 0) sanity = Math.min(sanity + sanityGain, DefaultMaxSanity);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    // ==================== 免疫力（Immunity）= 健康的饱和度 ====================

    public int getImmunity() {
        return immunity;
    }

    public void setImmunity(int immunity, @Nullable ServerPlayer serverPlayer) {
        this.immunity = Math.min(Math.max(immunity, 0), DefaultMaxImmunity);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    public void addImmunity(int immunity, @Nullable ServerPlayer serverPlayer) {
        if (immunity == 0) return;
        setImmunity(getImmunity() + immunity, serverPlayer);
    }

    /**
     * 消耗免疫力物品：恢复免疫力（缓冲）和健康（主值）。
     * 仿照原版食物恢复：value=免疫力恢复量，saturationModifier=健康恢复系数。
     */
    public void consumeImmunity(int value, float healthModifier, @Nullable ServerPlayer serverPlayer) {
        immunity = Math.min(immunity + value, DefaultMaxImmunity);
        int healthGain = Math.round(healthModifier * value);
        if (healthGain > 0) health = Math.min(health + healthGain, DefaultMaxHealth);
        if (serverPlayer != null) {
            save(serverPlayer, this);
            sendToClient(serverPlayer);
        }
    }

    // ==================== 存档/复制 ====================

    public void setDefault(ServerPlayer serverPlayer) {
        setHealth(DefaultMaxHealth, serverPlayer);
        setSanity(DefaultMaxSanity, serverPlayer);
        setThirst(DefaultMaxThirst, serverPlayer);
        setEnergy(DefaultMaxEnergy, serverPlayer);
        setImmunity(DefaultMaxImmunity, serverPlayer);
    }

    public void copyFrom(PlayerExCap source, ServerPlayer serverPlayer) {
        setHealth(source.getHealth(), serverPlayer);
        setSanity(source.getSanity(), serverPlayer);
        setThirst(source.getThirst(), serverPlayer);
        setEnergy(source.getEnergy(), serverPlayer);
        setImmunity(source.getImmunity(), serverPlayer);
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("ex.health", getHealth());
        nbt.putInt("ex.health.tick.msg", healthMsgTick);
        nbt.putInt("ex.health.tick.add", healthAddTick);
        nbt.putInt("ex.health.tick.nausea", healthNauseaTick);

        nbt.putInt("ex.sanity", getSanity());
        nbt.putInt("ex.sanity.tick.msg", sanityMsgTick);
        nbt.putInt("ex.sanity.tick.low", sanityLowTick);
        nbt.putInt("ex.sanity.tick.weapon", sanityWeaponDamageTick);
        nbt.putInt("ex.sanity.tick.check", sanityCheckBlockTick);
        nbt.putInt("ex.sanity.tick.sleep", sanitySleepTick);
        nbt.putInt("ex.sanity.tick.light", sanityLightTick);
        nbt.putInt("ex.sanity.tick.rain", sanityRainWaterTick);
        nbt.putInt("ex.sanity.tick.decay", sanityNaturalDecayTick);

        nbt.putInt("ex.thirst", getThirst());

        nbt.putInt("ex.energy", getEnergy());
        nbt.putInt("ex.immunity", getImmunity());
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("ex.health")) setHealth(nbt.getInt("ex.health"), null);
        healthMsgTick = nbt.getInt("ex.health.tick.msg");
        healthAddTick = nbt.getInt("ex.health.tick.add");
        healthNauseaTick = nbt.getInt("ex.health.tick.nausea");

        if (nbt.contains("ex.sanity")) setSanity(nbt.getInt("ex.sanity"), null);
        sanityMsgTick = nbt.getInt("ex.sanity.tick.msg");
        sanityLowTick = nbt.getInt("ex.sanity.tick.low");
        sanityWeaponDamageTick = nbt.getInt("ex.sanity.tick.weapon");
        sanityCheckBlockTick = nbt.getInt("ex.sanity.tick.check");
        sanitySleepTick = nbt.getInt("ex.sanity.tick.sleep");
        sanityLightTick = nbt.getInt("ex.sanity.tick.light");
        sanityRainWaterTick = nbt.getInt("ex.sanity.tick.rain");
        sanityNaturalDecayTick = nbt.getInt("ex.sanity.tick.decay");

        if (nbt.contains("ex.thirst")) setThirst(nbt.getInt("ex.thirst"), null);

        if (nbt.contains("ex.energy")) energy = Math.min(Math.max(nbt.getInt("ex.energy"), 0), DefaultMaxEnergy);
        if (nbt.contains("ex.immunity")) immunity = Math.min(Math.max(nbt.getInt("ex.immunity"), 0), DefaultMaxImmunity);
    }

    public void tick(ServerPlayer serverPlayer) {
        if (getHealth() < 6) {
            if (healthMsgTick >= 400) {
                healthMsgTick = 0;
                serverPlayer.sendSystemMessage(Component.translatable("cap.reality_value.health.tip.low"));
            } else healthMsgTick++;
            // 低健康→反胃效果
            if (healthNauseaTick >= 200) {
                healthNauseaTick = 0;
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
            } else healthNauseaTick++;
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
            // 低理智情绪低落消息（仅理智<4时触发）
            if (getSanity() < 4) {
                if (sanityWeaponDamageTick >= serverPlayer.getRandom().nextInt(200, 1000)) {
                    sanityWeaponDamageTick = 0;
                    // 动作栏显示随机情绪低落消息
                    int msgIndex = serverPlayer.getRandom().nextInt(5);
                    serverPlayer.displayClientMessage(
                            Component.translatable("cap.reality_value.sanity.low." + msgIndex),
                            true
                    );
                } else sanityWeaponDamageTick++;
            }
        }
        if (sanityCheckBlockTickS >= 4) {
            sanityCheckBlockTickS = 0;
            Block block = serverPlayer.getBlockStateOn().getBlock();
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            if (PlayerExBlockTags.I.contains(blockId.toString())) {
                sanityCheckBlockTick++;
            } else {
                boolean[] skip = {false};
                block.builtInRegistryHolder().tags().forEach(tagKey -> {
                    if (!skip[0] && PlayerExBlockTags.I.contains("#" + tagKey.location())) {
                        skip[0] = true;
                        sanityCheckBlockTick++;
                    }
                });
            }
            if (sanityCheckBlockTick >= 50) {
                sanityCheckBlockTick = 0;
                addSanity(-1, serverPlayer);
            }
        } else sanityCheckBlockTickS++;

        if (isSleep()) {
            if (serverPlayer.isSleeping()) {
                if (sanitySleepTick >= 1800) {
                    sanitySleepTick = 0;
                    addSanity(serverPlayer.getRandom().nextInt(1, 4), serverPlayer);
                } else sanitySleepTick++;
            } else {
                sanitySleepTick = 0;
                setSleep(false);
            }
        }
        if (sanityLightTick >= 14400) {
            sanityLightTick = 0;
            if (!getNearbyLightSources(serverPlayer, 8, 0).isEmpty()) {
                addSanity(serverPlayer.getRandom().nextInt(1, 3), serverPlayer);
            }
        } else sanityLightTick++;

        // 雨中/水中理智惩罚：每2400tick（约2分钟）在水中或露天雨天时降低1点理智
        if (sanityRainWaterTick >= 2400) {
            sanityRainWaterTick = 0;
            if (serverPlayer.isInWater() || (serverPlayer.level().isRaining() && serverPlayer.level().canSeeSky(serverPlayer.blockPosition())))
                addSanity(-1, serverPlayer);
        } else sanityRainWaterTick++;

        // 理智自然衰减：每7200tick（约6分钟）无条件降低1点理智，并发送提示消息
        if (sanityNaturalDecayTick >= 7200) {
            sanityNaturalDecayTick = 0;
            addSanity(-1, serverPlayer);
            serverPlayer.sendSystemMessage(Component.literal("§8[§4!§8] §4你有些累了，理智值下降了"));
        } else sanityNaturalDecayTick++;

        // 和平模式缓慢恢复精力和免疫力
        if (serverPlayer.level().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
            if (serverPlayer.tickCount % 60 == 0) {
                if (energy < DefaultMaxEnergy) energy++;
                if (immunity < DefaultMaxImmunity) immunity++;
                sendToClient(serverPlayer);
            }
        }
    }

    public void useItem(ServerPlayer player, @NotNull ItemStack item) {
        CompoundTag emptyTag = new CompoundTag();
        PlayerExDataTags.Range range = PlayerExDataTags.HEALTH_TAGS.getValue(item);
        if (range != null && range.checkNbt(emptyTag) && range.isEffective()) {
            addHealth(range.getValue(), player);
        }
        range = PlayerExDataTags.SANITY_TAGS.getValue(item);
        if (range != null && range.checkNbt(emptyTag) && range.isEffective()) {
            addSanity(range.getValue(), player);
        }
        // 精力恢复（同时恢复理智，仿照原版食物恢复机制）
        PlayerExSatTags.SatRange satRange = PlayerExSatTags.ENERGY_TAGS.getValue(item);
        if (satRange != null && satRange.checkNbt(emptyTag) && satRange.isEffective()) {
            consumeEnergy(satRange.getValue(), satRange.getSaturation(), player);
        }
        // 免疫力恢复（同时恢复健康）
        satRange = PlayerExSatTags.IMMUNITY_TAGS.getValue(item);
        if (satRange != null && satRange.checkNbt(emptyTag) && satRange.isEffective()) {
            consumeImmunity(satRange.getValue(), satRange.getSaturation(), player);
        }
    }

    // ==================== 物品恢复量计算（供 HUD 预览与 Tooltip 使用，取平均值避免随机跳动） ====================

    private static float avgRange(PlayerExDataTags.Range range) {
        return (range.min() + range.max()) / 2.0f;
    }

    private static float avgSatValue(PlayerExSatTags.SatRange range) {
        return (range.min() + range.max()) / 2.0f;
    }

    private static float avgSatModifier(PlayerExSatTags.SatRange range) {
        return (range.satMin() + range.satMax()) / 2.0f;
    }

    /** 物品的健康恢复量：接口物品(取最大值) + JSON配置直接恢复 + 免疫力物品按系数换算的健康恢复 */
    public static float itemHealthRestore(ItemStack item) {
        CompoundTag emptyTag = new CompoundTag();
        float restore = 0;
        if (item.getItem() instanceof IExValueItem exItem) {
            int[] r = exItem.healthRestore();
            if (r != null) restore += r[1];
        }
        PlayerExDataTags.Range range = PlayerExDataTags.HEALTH_TAGS.getValue(item);
        if (range != null && range.checkNbt(emptyTag) && range.isEffective()) restore += avgRange(range);
        PlayerExSatTags.SatRange satRange = PlayerExSatTags.IMMUNITY_TAGS.getValue(item);
        if (satRange != null && satRange.checkNbt(emptyTag) && satRange.isEffective()) {
            restore += avgSatModifier(satRange) * avgSatValue(satRange);
        }
        return restore;
    }

    /** 物品的理智恢复量：接口物品(取最大值) + JSON配置直接恢复 + 精力物品按系数换算的理智恢复 */
    public static float itemSanityRestore(ItemStack item) {
        CompoundTag emptyTag = new CompoundTag();
        float restore = 0;
        if (item.getItem() instanceof IExValueItem exItem) {
            int[] r = exItem.sanityRestore();
            if (r != null) restore += r[1];
        }
        PlayerExDataTags.Range range = PlayerExDataTags.SANITY_TAGS.getValue(item);
        if (range != null && range.checkNbt(emptyTag) && range.isEffective()) restore += avgRange(range);
        PlayerExSatTags.SatRange satRange = PlayerExSatTags.ENERGY_TAGS.getValue(item);
        if (satRange != null && satRange.checkNbt(emptyTag) && satRange.isEffective()) {
            restore += avgSatModifier(satRange) * avgSatValue(satRange);
        }
        return restore;
    }

    /** 物品的免疫力（缓冲）恢复量：接口物品(取最大值) + JSON配置 */
    public static float itemImmunityRestore(ItemStack item) {
        CompoundTag emptyTag = new CompoundTag();
        float restore = 0;
        if (item.getItem() instanceof IExValueItem exItem) {
            int[] r = exItem.immunityRestore();
            if (r != null) restore += r[1];
        }
        PlayerExSatTags.SatRange satRange = PlayerExSatTags.IMMUNITY_TAGS.getValue(item);
        if (satRange != null && satRange.checkNbt(emptyTag) && satRange.isEffective()) restore += avgSatValue(satRange);
        return restore;
    }

    /** 物品的精力（缓冲）恢复量：接口物品(取最大值) + JSON配置 */
    public static float itemEnergyRestore(ItemStack item) {
        CompoundTag emptyTag = new CompoundTag();
        float restore = 0;
        if (item.getItem() instanceof IExValueItem exItem) {
            int[] r = exItem.energyRestore();
            if (r != null) restore += r[1];
        }
        PlayerExSatTags.SatRange satRange = PlayerExSatTags.ENERGY_TAGS.getValue(item);
        if (satRange != null && satRange.checkNbt(emptyTag) && satRange.isEffective()) restore += avgSatValue(satRange);
        return restore;
    }

    public void sendToClient(ServerPlayer serverPlayer) {
        Messages.sendToPlayer(new Messages.ExDataPayload(
                getHealth(), getSanity(), getThirst(),
                getEnergy(), getImmunity()), serverPlayer);
    }

    public void hurt(ServerPlayer serverPlayer, LivingEntity livingEntity) {
        int v = serverPlayer.getRandom().nextInt(1, 101);
        if (v < 75) addHealth(-serverPlayer.getRandom().nextInt(1, 4), serverPlayer);
        v = serverPlayer.getRandom().nextInt(1, 101);
        if (v < 30) addSanity(-serverPlayer.getRandom().nextInt(1, 3), serverPlayer);
    }

    public void hurt(ServerPlayer serverPlayer, LivingEntity livingEntity, boolean isBlocking) {
        int v = serverPlayer.getRandom().nextInt(1, 101);
        if (v < 75) addHealth(-serverPlayer.getRandom().nextInt(1, 4), serverPlayer);
        // 举盾时免疫理智损失
        if (!isBlocking) {
            v = serverPlayer.getRandom().nextInt(1, 101);
            if (v < 30) addSanity(-serverPlayer.getRandom().nextInt(1, 3), serverPlayer);
        }
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public boolean isSleep() {
        return sleep;
    }

    public static List<BlockPos> getNearbyLightSources(Player player, int radius, int minLight) {
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
                ResourceLocation location = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                if (location != null && location.getPath().contains("soul")) continue;
                lightSources.add(checkPos.immutable());
            }
        }
        return lightSources;
    }
}
