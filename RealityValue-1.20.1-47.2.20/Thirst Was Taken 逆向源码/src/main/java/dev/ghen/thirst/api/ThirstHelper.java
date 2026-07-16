package dev.ghen.thirst.api;

import com.momosoftworks.coldsweat.api.util.Temperature;
import dev.ghen.thirst.content.purity.ContainerWithPurity;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.common.event.ThirstEventFactory;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.foundation.config.ContainerConfig;
import dev.ghen.thirst.foundation.config.ItemSettingsConfig;
import dev.ghen.thirst.foundation.config.KeyWordConfig;
import dev.ghen.thirst.foundation.util.ConfigHelper;
import dev.ghen.thirst.foundation.util.LoadedValue;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/api/ThirstHelper.class */
public class ThirstHelper {
    private static boolean useColdSweatCaps;
    private static final float MODIFIER_HARSHNESS = 0.5f;
    public static Map<Item, Number[]> VALID_DRINKS;
    public static Map<Item, Number[]> VALID_FOODS;
    public static List<Item> containers;
    public static String keywordBlackList;
    public static String keywordDrink;
    public static String keywordSoup;
    public static String keywordFruit;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ThirstHelper.class.desiredAssertionStatus();
        useColdSweatCaps = false;
        VALID_DRINKS = (Map) LoadedValue.m0of(() -> {
            return ConfigHelper.getItemsWithValues((List) ItemSettingsConfig.DRINKS.get());
        }).get();
        VALID_FOODS = (Map) LoadedValue.m0of(() -> {
            return ConfigHelper.getItemsWithValues((List) ItemSettingsConfig.FOODS.get());
        }).get();
        containers = (List) LoadedValue.m0of(() -> {
            return ConfigHelper.getItems((List) ContainerConfig.CONTAINERS.get());
        }).get();
        keywordBlackList = (String) KeyWordConfig.KEYWORD_BLACKLIST.get();
        keywordDrink = (String) KeyWordConfig.KEYWORD_DRINK.get();
        keywordSoup = (String) KeyWordConfig.KEYWORD_SOUP.get();
        keywordFruit = (String) KeyWordConfig.KEYWORD_FRUIT.get();
    }

    public static void init() {
        ThirstEventFactory.onRegisterThirstValue();
        for (Item item : containers) {
            if (!item.equals(Items.f_41852_)) {
                WaterPurity.addContainer(new ContainerWithPurity(new ItemStack(item)));
            }
        }
        VALID_DRINKS.forEach((item2, numbers) -> {
            if (item2.m_41473_() != null && !((Boolean) CommonConfig.ENABLE_DRINKS_NUTRITION.get()).booleanValue()) {
                item2.m_41473_().f_38723_ = 0;
            }
        });
    }

    public static boolean itemRestoresThirst(ItemStack itemStack) {
        return isDrink(itemStack) || isFood(itemStack) || checkKeywords(itemStack);
    }

    public static boolean isDrink(ItemStack itemStack) {
        return !((List) ItemSettingsConfig.ITEMS_BLACKLIST.get()).contains(itemStack.m_41720_().toString()) && VALID_DRINKS.containsKey(itemStack.m_41720_());
    }

    public static boolean isFood(ItemStack itemStack) {
        return !((List) ItemSettingsConfig.ITEMS_BLACKLIST.get()).contains(itemStack.m_41720_().toString()) && VALID_FOODS.containsKey(itemStack.m_41720_());
    }

    @Deprecated
    public static void addFood(Item item, int thirst, int quenched) {
    }

    @Deprecated
    public static void addDrink(Item item, int thirst, int quenched) {
    }

    public static int getThirst(ItemStack itemStack) {
        Item item = itemStack.m_41720_();
        if (VALID_DRINKS.containsKey(item)) {
            return VALID_DRINKS.get(item)[0].intValue();
        }
        return VALID_FOODS.get(item)[0].intValue();
    }

    public static int getQuenched(ItemStack itemStack) {
        Item item = itemStack.m_41720_();
        if (VALID_DRINKS.containsKey(item)) {
            return VALID_DRINKS.get(item)[1].intValue();
        }
        return VALID_FOODS.get(item)[1].intValue();
    }

    public static int getPurity(ItemStack item) {
        if (!WaterPurity.hasPurity(item)) {
            return ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue();
        }
        if ($assertionsDisabled || item.m_41783_() != null) {
            return item.m_41783_().m_128451_("Purity");
        }
        throw new AssertionError();
    }

    public static void shouldUseColdSweatCaps(boolean should) {
        useColdSweatCaps = should;
    }

    public static float getExhaustionFireProtModifier(Player player) {
        int totalLevels = EnchantmentHelper.m_44856_(player.m_6168_(), player.m_269291_().m_269549_()) / 2;
        if (totalLevels > 12) {
            totalLevels = 12;
        }
        return 1.0f - ((totalLevels * 0.0625f) * 0.75f);
    }

    public static float getExhaustionFireResistanceModifier(Player player) {
        if (player.m_21023_(MobEffects.f_19607_)) {
            return ((Integer) CommonConfig.FIRE_RESISTANCE_DEHYDRATION.get()).intValue() / 100.0f;
        }
        return 1.0f;
    }

    public static float getExhaustionBiomeModifier(Player player) {
        BlockPos pos = player.m_20097_();
        Level level = player.m_9236_();
        if (level.m_6042_().f_63857_()) {
            return ((Double) CommonConfig.NETHER_THIRST_DEPLETION_MODIFIER.get()).floatValue();
        }
        Biome biome = (Biome) level.m_204166_(pos).m_203334_();
        float humidity = biome.getModifiedClimateSettings().f_47683_() + 0.6f;
        if (humidity <= 0.6d) {
            humidity = (float) (humidity + 0.5d);
        }
        float temp = biome.m_47554_() + 0.2f;
        if (useColdSweatCaps) {
            temp = (float) (Temperature.get(player, Temperature.Type.BODY) / 100.0d);
        } else if (temp <= 0.0f) {
            temp = (float) Math.exp(temp);
        } else if (temp > 1.0f) {
            temp /= 2.0f;
        }
        float thirstModifier = ((Number) CommonConfig.THIRST_DEPLETION_MODIFIER.get()).floatValue() * (temp / humidity);
        if (thirstModifier < 1.0f) {
            float modifierOffset = 1.0f - thirstModifier;
            thirstModifier = 1.0f - (modifierOffset * MODIFIER_HARSHNESS);
        }
        return thirstModifier;
    }

    private static boolean checkKeywords(ItemStack itemStack) {
        if (!((Boolean) KeyWordConfig.ENABLE_KEYWORD_CONFIG.get()).booleanValue() || !itemStack.m_41614_()) {
            return false;
        }
        String pattern = keywordBlackList;
        Matcher matcher = Pattern.compile(pattern, 2).matcher(itemStack.m_41778_());
        if (matcher.find()) {
            return false;
        }
        String pattern2 = keywordDrink;
        Matcher matcher2 = Pattern.compile(pattern2, 2).matcher(itemStack.m_41778_());
        boolean hasWater = matcher2.find();
        if (hasWater) {
            VALID_DRINKS.put(itemStack.m_41720_(), new Number[]{Integer.valueOf(KeyWordConfig.getDrinkHydration()), Integer.valueOf(KeyWordConfig.getDrinkQuenchness())});
            return true;
        }
        String pattern3 = keywordSoup;
        Matcher matcher3 = Pattern.compile(pattern3, 2).matcher(itemStack.m_41778_());
        boolean hasWater2 = matcher3.find();
        if (hasWater2) {
            VALID_FOODS.put(itemStack.m_41720_(), new Number[]{Integer.valueOf(KeyWordConfig.getSoupHydration()), Integer.valueOf(KeyWordConfig.getSoupQuenchness())});
            return true;
        }
        String pattern4 = keywordFruit;
        Matcher matcher4 = Pattern.compile(pattern4, 2).matcher(itemStack.m_41778_());
        boolean hasWater3 = matcher4.find();
        if (hasWater3) {
            VALID_FOODS.put(itemStack.m_41720_(), new Number[]{Integer.valueOf(KeyWordConfig.getFruitHydration()), Integer.valueOf(KeyWordConfig.getFruitQuenchness())});
        }
        return hasWater3;
    }
}
