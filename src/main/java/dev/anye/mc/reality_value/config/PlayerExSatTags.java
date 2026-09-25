package dev.anye.mc.reality_value.config;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.lib._File;
import dev.anye.mc.reality_value.lib._JsonConfig;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 带饱和度的物品值配置，仿照原版饱和度机制。
 * 每个物品有主值范围(min~max)和饱和度范围(satMin~satMax)。
 */
public class PlayerExSatTags extends _JsonConfig<HashMap<String, PlayerExSatTags.SatRange>> {
    public static final PlayerExSatTags ENERGY_TAGS = new PlayerExSatTags(
            _File.getFilePath(RealityValue.CONFIG_DATA_DIR, "energy.json"), """
                    {
                        "minecraft:apple":{"min":2,"max":4,"satMin":0.3,"satMax":0.6},
                        "minecraft:bread":{"min":3,"max":5,"satMin":0.5,"satMax":0.8},
                        "minecraft:cooked_beef":{"min":4,"max":6,"satMin":0.8,"satMax":1.2},
                        "minecraft:cooked_porkchop":{"min":3,"max":5,"satMin":0.5,"satMax":0.8},
                        "minecraft:cooked_chicken":{"min":2,"max":4,"satMin":0.3,"satMax":0.6},
                        "minecraft:cooked_mutton":{"min":2,"max":4,"satMin":0.3,"satMax":0.6},
                        "minecraft:cooked_cod":{"min":2,"max":4,"satMin":0.3,"satMax":0.6},
                        "minecraft:cooked_salmon":{"min":3,"max":5,"satMin":0.5,"satMax":0.8},
                        "minecraft:cookie":{"min":1,"max":2,"satMin":0.1,"satMax":0.2},
                        "minecraft:melon_slice":{"min":1,"max":2,"satMin":0.1,"satMax":0.2},
                        "minecraft:sweet_berries":{"min":1,"max":2,"satMin":0.1,"satMax":0.2},
                        "minecraft:carrot":{"min":1,"max":3,"satMin":0.2,"satMax":0.4},
                        "minecraft:baked_potato":{"min":3,"max":5,"satMin":0.5,"satMax":0.8},
                        "minecraft:pumpkin_pie":{"min":3,"max":5,"satMin":0.5,"satMax":0.8}
                    }
                    """);
    public static final PlayerExSatTags IMMUNITY_TAGS = new PlayerExSatTags(
            _File.getFilePath(RealityValue.CONFIG_DATA_DIR, "immunity.json"), """
                    {
                        "minecraft:golden_apple":{"min":4,"max":6,"satMin":0.8,"satMax":1.2},
                        "minecraft:enchanted_golden_apple":{"min":8,"max":10,"satMin":1.5,"satMax":2.0},
                        "minecraft:honey_bottle":{"min":2,"max":4,"satMin":0.3,"satMax":0.6},
                        "minecraft:milk_bucket":{"min":2,"max":3,"satMin":0.3,"satMax":0.5}
                    }
                    """);
    private static final Logger LOGGER = LogUtils.getLogger();

    public PlayerExSatTags(String filePath, String defaultData) {
        super(filePath, defaultData, new TypeToken<>() {
        });
    }

    @Override
    public HashMap<String, SatRange> getDatas() {
        if (this.datas == null)
            this.datas = new HashMap<>();
        return super.getDatas();
    }

    public SatRange getValue(ItemStack item) {
        return getValue(item.getItemHolder());
    }

    public SatRange getValue(Holder<Item> item) {
        if (item.unwrapKey().isPresent()) {
            if (getDatas().containsKey(item.unwrapKey().get().location().toString())) {
                return getDatas().get(item.unwrapKey().get().location().toString());
            }
        }
        AtomicReference<SatRange> range = new AtomicReference<>();
        item.tags().forEach(itemTagKey -> {
            if (getDatas().containsKey("#" + itemTagKey.location())) {
                range.set(getDatas().get("#" + itemTagKey.location()));
            }
        });
        return range.get();
    }

    /**
     * 带饱和度范围的配置项。
     * min/max: 主值范围（对应原版食物值）
     * satMin/satMax: 饱和度范围（对应原版饱和度修饰值）
     */
    public record SatRange(int min, int max, float satMin, float satMax, CompoundTag nbt) {
        public SatRange(int min, int max, float satMin, float satMax) {
            this(min, max, satMin, satMax, new CompoundTag());
        }

        public int getValue() {
            int actualMin = Math.min(min, max);
            int actualMax = Math.max(min, max);
            return ThreadLocalRandom.current().nextInt(actualMin, actualMax + 1);
        }

        public float getSaturation() {
            float actualMin = Math.min(satMin, satMax);
            float actualMax = Math.max(satMin, satMax);
            return (float) ThreadLocalRandom.current().nextDouble(actualMin, actualMax + 0.001);
        }

        public boolean isEffective() {
            return min != 0 || max != 0 || satMin != 0 || satMax != 0;
        }

        public boolean checkNbt(CompoundTag itemNbt) {
            if (nbt == null || nbt.isEmpty())
                return true;
            for (String key : nbt.getAllKeys()) {
                if (itemNbt.contains(key)) {
                    Tag tag = itemNbt.get(key);
                    if (tag == null)
                        return false;
                    if (!tag.equals(nbt.get(key)))
                        return false;
                } else
                    return false;
            }
            return true;
        }
    }
}
