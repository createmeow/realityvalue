package dev.anye.mc.reality_value.cap;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.config.PlayerExBlockTags;
import dev.anye.mc.reality_value.config.PlayerExDataTags;
import dev.anye.mc.reality_value.net.Messages;
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
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
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
    private static final Holder<MobEffect>[] sanityEffect = new Holder[]{
            MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DARKNESS, MobEffects.BLINDNESS, MobEffects.CONFUSION
    };

    private int health = DefaultMaxHealth,
            sanity = DefaultMaxHealth,
            thirst = DefaultMaxThirst;
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

    public void addHealth(int health, @Nullable ServerPlayer serverPlayer) {
        if (health == 0) return;
        setHealth(getHealth() + health, serverPlayer);
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

    public void addSanity(int sanity, @Nullable ServerPlayer serverPlayer) {
        if (sanity == 0) return;
        setSanity(getSanity() + sanity, serverPlayer);
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

    public void setDefault(ServerPlayer serverPlayer) {
        setHealth(DefaultMaxHealth, serverPlayer);
        setSanity(DefaultMaxSanity, serverPlayer);
        setThirst(DefaultMaxThirst, serverPlayer);
    }

    public void copyFrom(PlayerExCap source, ServerPlayer serverPlayer) {
        setHealth(source.getHealth(), serverPlayer);
        setSanity(source.getSanity(), serverPlayer);
        setThirst(source.getThirst(), serverPlayer);
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
            // 低理智 + 手持剑/斧 → 每10~50秒1点伤害 + 负面消息
            if (sanityWeaponDamageTick >= serverPlayer.getRandom().nextInt(200, 1000)) {
                sanityWeaponDamageTick = 0;
                boolean holdingWeapon = false;
                var mainItem = serverPlayer.getMainHandItem().getItem();
                var offItem = serverPlayer.getOffhandItem().getItem();
                if (mainItem instanceof SwordItem || mainItem instanceof AxeItem
                        || offItem instanceof SwordItem || offItem instanceof AxeItem) {
                    holdingWeapon = true;
                }
                if (holdingWeapon) {
                    serverPlayer.hurt(serverPlayer.damageSources().generic(), 1.0f);
                    String[] messages = {
                            "§8[§4!§8] §4好…累…",
                            "§8[§4!§8] §4为什么！为什么要这样对我",
                            "§8[§4!§8] §4一切都毫无意义…",
                            "§8[§4!§8] §4我受够了…",
                            "§8[§4!§8] §4活着好痛苦…"
                    };
                    serverPlayer.sendSystemMessage(Component.literal(messages[serverPlayer.getRandom().nextInt(messages.length)]));
                }
            } else sanityWeaponDamageTick++;
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
    }

    public void sendToClient(ServerPlayer serverPlayer) {
        Messages.sendToPlayer(new Messages.ExDataPayload(getHealth(), getSanity(), getThirst()), serverPlayer);
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