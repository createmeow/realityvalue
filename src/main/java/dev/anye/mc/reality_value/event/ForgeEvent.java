package dev.anye.mc.reality_value.event;

import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.cap.PlayerExCap;
import dev.anye.mc.reality_value.config.PlaceBlockList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FlowerBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

import java.util.Set;

@EventBusSubscriber(modid = RealityValue.MOD_ID)
public class ForgeEvent {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Create 模组需要恢复理智的物品列表
    private static final Set<String> CREATE_SANITY_ITEMS = Set.of(
            "create:bar_of_chocolate",
            "create:chocolate_glazed_berries",
            "create:builders_tea",
            "create:honeyed_apple",
            "create:sweet_roll"
    );

    // 生食列表：食用这些物品会降低免疫力
    private static final Set<String> RAW_FOOD_PENALTY = Set.of(
            "minecraft:beef",
            "minecraft:chicken",
            "minecraft:porkchop",
            "minecraft:mutton",
            "minecraft:cod",
            "minecraft:salmon",
            "minecraft:rotten_flesh",
            "minecraft:spider_eye",
            "minecraft:pufferfish",
            "minecraft:poisonous_potato"
    );

    // FarmersDelight 菜品标签（meals/drinks/sweets/snacks）
    private static final TagKey<Item> FD_MEALS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "meals"));
    private static final TagKey<Item> FD_DRINKS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "drinks"));
    private static final TagKey<Item> FD_SWEETS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "sweets"));
    private static final TagKey<Item> FD_SNACKS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "snacks"));

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (event.isWasDeath()) {
                PlayerExCap oldCap = PlayerExCap.get((ServerPlayer) event.getOriginal());
                PlayerExCap newCap = PlayerExCap.get(serverPlayer);
                newCap.copyFrom(oldCap, serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            cap.tick(serverPlayer);
            PlayerExCap.save(serverPlayer, cap);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            cap.setDefault(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            cap.sendToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerExCap cap = PlayerExCap.get(player);
            cap.useItem(player, event.getItem());

            // 处理低纯度水：损失健康和理智
            int purity = getWaterPurity(event.getItem());
            if (purity >= 0 && purity <= 1) {
                cap.addHealth(-player.getRandom().nextInt(1, 4), player);
                cap.addSanity(-player.getRandom().nextInt(1, 3), player);
                if (purity == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                }
            }

            // 联动 Create 模组：特定食物恢复 3~4 点理智
            String itemId = getItemId(event.getItem());
            if (CREATE_SANITY_ITEMS.contains(itemId)) {
                cap.addSanity(player.getRandom().nextInt(3, 5), player);
            }

            // 联动 FarmersDelight 模组：通过其菜品标签判断，仅真正的菜品恢复 3~6 点理智
            if (isFarmersDelightDish(event.getItem())) {
                cap.addSanity(player.getRandom().nextInt(3, 7), player);
            }

            // 生食惩罚：食用生肉/腐肉降低免疫力与理智
            String foodId = getItemId(event.getItem());
            if (foodId != null && RAW_FOOD_PENALTY.contains(foodId)) {
                cap.addImmunity(-player.getRandom().nextInt(1, 3), player);
                cap.addSanity(-player.getRandom().nextInt(1, 3), player);
            }

            PlayerExCap.save(player, cap);
        }
    }

    /**
     * 获取物品的完整ID（如 "create:bar_of_chocolate"）
     */
    private static String getItemId(ItemStack stack) {
        try {
            return stack.getItem().builtInRegistryHolder().key().location().toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查物品是否是 FarmersDelight 菜品（通过官方标签 meals/drinks/sweets/snacks 判断）
     */
    private static boolean isFarmersDelightDish(ItemStack stack) {
        return stack.is(FD_MEALS)
                || stack.is(FD_DRINKS)
                || stack.is(FD_SWEETS)
                || stack.is(FD_SNACKS);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity livingEntity
                && event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 只允许僵尸类生物攻击时触发健康/理智损失
            if (livingEntity instanceof Zombie) {
                PlayerExCap cap = PlayerExCap.get(serverPlayer);
                cap.hurt(serverPlayer, livingEntity, serverPlayer.isBlocking());
                PlayerExCap.save(serverPlayer, cap);
            }
        }
    }

    @SubscribeEvent
    public static void onFished(ItemFishedEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getRandom().nextBoolean()) {
                PlayerExCap cap = PlayerExCap.get(serverPlayer);
                cap.addSanity(1, serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getState().getBlock() instanceof FlowerBlock) {
            PlaceBlockList.get(event.getLevel().getChunk(event.getPos())).add(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getState().getBlock() instanceof FlowerBlock) {
            PlaceBlockList.get(event.getLevel().getChunk(event.getPos())).add(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            if (event.getState().getBlock() instanceof FlowerBlock) {
                if (!PlaceBlockList.get(event.getLevel().getChunk(event.getPos())).has(event.getPos())) {
                    if (serverPlayer.getRandom().nextBoolean()) {
                        PlayerExCap cap = PlayerExCap.get(serverPlayer);
                        cap.addSanity(1, serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSleep(CanPlayerSleepEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerExCap cap = PlayerExCap.get(serverPlayer);
            cap.setSleep(true);
            PlayerExCap.save(serverPlayer, cap);
        }
    }

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        event.getLevel().players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                PlayerExCap cap = PlayerExCap.get(serverPlayer);
                cap.setSanity(20, serverPlayer);
                cap.setSleep(false);
            }
        });
    }

    @SubscribeEvent
    public static void onSleep(CanContinueSleepingEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (!event.mayContinueSleeping()) {
                PlayerExCap cap = PlayerExCap.get(serverPlayer);
                cap.setSleep(false);
                PlayerExCap.save(serverPlayer, cap);
            }
        }
    }

    /**
     * 获取物品的纯净度（ThirstWasTaken联动）
     * 返回值：0=肮脏, 1=有点脏, 2=可接受, 3=纯净, -1=无纯净度
     */
    private static int getWaterPurity(ItemStack item) {
        // 1. 尝试 ThirstWasTaken API（反射调用）
        try {
            Class<?> thirstHelper = Class.forName("dev.ghen.thirst.api.ThirstHelper");
            var getPurity = thirstHelper.getMethod("getPurity", ItemStack.class);
            return (int) getPurity.invoke(null, item);
        } catch (Exception ignored) {}

        // 2. 尝试读取自定义数据组件 thirst:purity
        try {
            var registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
            var key = ResourceLocation.fromNamespaceAndPath("thirst", "purity");
            var componentType = registry.get(key);
            if (componentType != null) {
                var purity = item.get(componentType);
                if (purity instanceof Integer i) {
                    return i;
                }
            }
        } catch (Exception ignored) {}

        // 3. 回退：检查 CUSTOM_DATA 中的 "Purity" 标签（旧版兼容）
        try {
            var customData = item.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                var tag = customData.copyTag();
                if (tag.contains("Purity")) {
                    return tag.getInt("Purity");
                }
            }
        } catch (Exception ignored) {}

        return -1;
    }
}