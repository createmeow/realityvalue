package dev.anye.mc.reality_value.config;

import com.google.gson.reflect.TypeToken;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.lib._File;
import dev.anye.mc.reality_value.lib._JsonConfig;

import java.util.ArrayList;

public class PlayerExBlockTags extends _JsonConfig<ArrayList<String>> {
    public static final PlayerExBlockTags I = new PlayerExBlockTags();

    public PlayerExBlockTags() {
        super(_File.getFilePath(RealityValue.CONFIG_DATA_DIR, "blocks.json"), """
                [
                    "#tag",
                    "minecraft:crimson_nylium",
                    "minecraft:warped_nylium",
                    "minecraft:netherrack",
                    "minecraft:soul_sand",
                    "minecraft:soul_soil",
                    "minecraft:blackstone",
                    "minecraft:basalt",
                    "minecraft:smooth_basalt",
                    "minecraft:nether_gold_ore",
                    "minecraft:nether_quartz_ore",
                    "minecraft:ancient_debris"
                ]
                """, new TypeToken<>() {
        });
    }

    @Override
    public ArrayList<String> getDatas() {
        if (this.datas == null)
            this.datas = new ArrayList<>();
        return super.getDatas();
    }

    public boolean contains(String id) {
        return getDatas().contains(id);
    }
}