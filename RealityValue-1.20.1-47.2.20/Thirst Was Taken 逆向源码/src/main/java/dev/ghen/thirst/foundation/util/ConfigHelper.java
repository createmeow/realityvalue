package dev.ghen.thirst.foundation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/util/ConfigHelper.class */
public class ConfigHelper {
    public static Map<Item, Number[]> getItemsWithValues(List<? extends List<?>> source) {
        Map<Item, Number[]> map = new HashMap<>();
        for (List<?> entry : source) {
            String itemID = (String) entry.get(0);
            if (itemID.startsWith("#")) {
                String tagID = itemID.replace("#", "");
                Optional<ITag<Item>> optionalTag = ForgeRegistries.ITEMS.tags().stream().filter(tag -> {
                    return tag.getKey().f_203868_().toString().equals(tagID);
                }).findFirst();
                optionalTag.ifPresent(itemITag -> {
                    for (Item item : ((ITag) optionalTag.get()).stream().toList()) {
                        map.put(item, new Number[]{(Number) entry.get(1), (Number) entry.get(2)});
                    }
                });
            } else {
                Item newItem = (Item) ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemID));
                if (newItem != null) {
                    map.put(newItem, new Number[]{(Number) entry.get(1), (Number) entry.get(2)});
                }
            }
        }
        return map;
    }

    public static List<Item> getItems(List<? extends String> source) {
        List<Item> list = new ArrayList<>();
        for (String itemID : source) {
            if (itemID.startsWith("#")) {
                String tagID = itemID.replace("#", "");
                Optional<ITag<Item>> optionalTag = ForgeRegistries.ITEMS.tags().stream().filter(tag -> {
                    return tag.getKey().f_203868_().toString().equals(tagID);
                }).findFirst();
                optionalTag.ifPresent(itemITag -> {
                    list.addAll(((ITag) optionalTag.get()).stream().toList());
                });
            } else {
                Item newItem = (Item) ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemID));
                if (newItem != null) {
                    list.add(newItem);
                }
            }
        }
        return list;
    }
}
