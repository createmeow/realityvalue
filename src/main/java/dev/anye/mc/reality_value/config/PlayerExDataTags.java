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

public class PlayerExDataTags extends _JsonConfig<HashMap<String, PlayerExDataTags.Range>> {
    public static final PlayerExDataTags HEALTH_TAGS = new PlayerExDataTags(
            _File.getFilePath(RealityValue.CONFIG_DATA_DIR, "health.json"), """
                    {
                        "minecraft:rotten_flesh":{"min":-1,"max":-3,"nbt":{}},
                        "minecraft:poisonous_potato":{"min":-3,"max":-6},
                        "minecraft:mutton":{"min":-1,"max":-3},
                        "minecraft:beef":{"min":-1,"max":-3},
                        "minecraft:porkchop":{"min":-1,"max":-3},
                        "minecraft:chicken":{"min":-1,"max":-3},
                        "minecraft:rabbit":{"min":-1,"max":-3},
                        "minecraft:cod":{"min":-1,"max":-3},
                        "minecraft:salmon":{"min":-1,"max":-3},
                        "minecraft:pufferfish":{"min":-4,"max":-5},
                        "minecraft:spider_eye":{"min":-2,"max":-4}
                    }
                    """);
    public static final PlayerExDataTags SANITY_TAGS = new PlayerExDataTags(
            _File.getFilePath(RealityValue.CONFIG_DATA_DIR, "sanity.json"), """
                    {
                        "minecraft:rotten_flesh":{"min":-1,"max":-3}
                    }
                    """);
    private static final Logger LOGGER = LogUtils.getLogger();

    public PlayerExDataTags(String filePath, String defaultData) {
        super(filePath, defaultData, new TypeToken<>() {
        });
    }

    @Override
    public HashMap<String, Range> getDatas() {
        if (this.datas == null)
            this.datas = new HashMap<>();
        return super.getDatas();
    }

    public Range getValue(ItemStack item) {
        return getValue(item.getItemHolder());
    }

    public Range getValue(Holder<Item> item) {
        if (item.unwrapKey().isPresent()) {
            if (getDatas().containsKey(item.unwrapKey().get().location().toString())) {
                return getDatas().get(item.unwrapKey().get().location().toString());
            }
        }
        AtomicReference<Range> range = new AtomicReference<>();
        item.tags().forEach(itemTagKey -> {
            if (getDatas().containsKey("#" + itemTagKey.location())) {
                range.set(getDatas().get("#" + itemTagKey.location()));
            }
        });
        return range.get();
    }

    public record Range(int min, int max, CompoundTag nbt) {
        public Range(int min, int max) {
            this(min, max, new CompoundTag());
        }

        public int getValue() {
            int actualMin = Math.min(min, max);
            int actualMax = Math.max(min, max);
            return ThreadLocalRandom.current().nextInt(actualMin, actualMax + 1);
        }

        public boolean isEffective() {
            return min != 0 || max != 0;
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