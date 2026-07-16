package dev.ghen.thirst.content.purity;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.content.registry.ItemInit;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.foundation.util.MathHelper;
import dev.ghen.thirst.foundation.util.ReflectionUtil;
import dev.ghen.thirst.foundation.util.TickHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import toughasnails.api.item.TANItems;
import toughasnails.item.EmptyCanteenItem;
import umpaz.brewinandchewin.common.registry.BnCItems;
import umpaz.farmersrespite.common.registry.FRItems;

@Mod.EventBusSubscriber
/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/purity/WaterPurity.class */
public class WaterPurity {
    private static final List<ContainerWithPurity> waterContainers;
    private static final List<Block> fillablesWithPurity;
    public static final int MIN_PURITY = 0;
    public static final int MAX_PURITY = 3;
    public static final IntegerProperty BLOCK_PURITY;
    public static boolean tanLoaded;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !WaterPurity.class.desiredAssertionStatus();
        waterContainers = new ArrayList();
        fillablesWithPurity = new ArrayList();
        BLOCK_PURITY = IntegerProperty.m_61631_("purity", 0, 4);
        tanLoaded = false;
    }

    public static void init() {
        registerDispenserBehaviours();
        registerContainers();
        registerFillables();
        if (ModList.get().isLoaded("farmersrespite")) {
            registerFarmersRespiteContainers();
        }
        if (ModList.get().isLoaded("brewinandchewin")) {
            registerBrewinAndChewinContainers();
        }
        if (ModList.get().isLoaded("toughasnails")) {
            registerToughAsNailsContainers();
            tanLoaded = true;
        }
    }

    private static void registerContainers() {
        waterContainers.add(new ContainerWithPurity(new ItemStack(Items.f_42590_), PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_)).setEqualsFilled(itemStack -> {
            return itemStack.m_150930_(Items.f_42589_) && PotionUtils.m_43579_(itemStack) == Potions.f_43599_;
        }));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) ItemInit.TERRACOTTA_BOWL.get()), new ItemStack((ItemLike) ItemInit.TERRACOTTA_WATER_BOWL.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack(Items.f_42446_), new ItemStack(Items.f_42447_), false).canHarvestRunningWater(false));
    }

    private static void registerFillables() {
        fillablesWithPurity.add(Blocks.f_50256_);
        fillablesWithPurity.add(Blocks.f_152476_);
    }

    private static void registerFarmersRespiteContainers() {
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.GREEN_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.YELLOW_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.BLACK_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.ROSE_HIP_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.DANDELION_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.COFFEE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.GAMBLERS_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.PURULENT_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_APPLE_CIDER.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_COFFEE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_BLACK_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_DANDELION_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_GREEN_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_GAMBLERS_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_PURULENT_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_ROSE_HIP_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.LONG_YELLOW_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_APPLE_CIDER.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_COFFEE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_BLACK_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_GREEN_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_HOT_COCOA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_GAMBLERS_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_MELON_JUICE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_PURULENT_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_ROSE_HIP_TEA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) FRItems.STRONG_YELLOW_TEA.get())));
    }

    private static void registerBrewinAndChewinContainers() {
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.BEER.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.VODKA.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.RICE_WINE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.STRONGROOT_ALE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.PALE_JANE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.SALTY_FOLLY.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.STEEL_TOE_STOUT.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.GLITTERING_GRENADINE.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.BLOODY_MARY.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.RED_RUM.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.WITHERING_DROSS.get())));
        waterContainers.add(new ContainerWithPurity(new ItemStack((ItemLike) BnCItems.KOMBUCHA.get())));
    }

    private static void registerToughAsNailsContainers() {
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.LEATHER_DIRTY_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.COPPER_DIRTY_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.IRON_DIRTY_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.GOLD_DIRTY_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.DIAMOND_DIRTY_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.NETHERITE_DIRTY_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.LEATHER_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.COPPER_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.IRON_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.GOLD_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.DIAMOND_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.NETHERITE_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.LEATHER_PURIFIED_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.COPPER_PURIFIED_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.IRON_PURIFIED_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.GOLD_PURIFIED_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.DIAMOND_PURIFIED_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.NETHERITE_PURIFIED_WATER_CANTEEN)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.PURIFIED_WATER_BOTTLE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.DIRTY_WATER_BOTTLE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.APPLE_JUICE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.CACTUS_JUICE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.CHORUS_FRUIT_JUICE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.GLOW_BERRY_JUICE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.MELON_JUICE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.PUMPKIN_JUICE)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(TANItems.SWEET_BERRY_JUICE)));
    }

    @SubscribeEvent
    static void fillablesHandler(PlayerInteractEvent.RightClickBlock event) {
        if ((event.getEntity() instanceof ServerPlayer) && isWaterFilledContainer(event.getItemStack())) {
            Player player = event.getEntity();
            Level level = player.m_9236_();
            BlockPos pos = event.getHitVec().m_82425_();
            BlockState blockState = level.m_8055_(pos);
            if (isFillableBlock(blockState)) {
                int purity = getPurity(event.getItemStack());
                int iIntValue = (blockState.m_61138_(BLOCK_PURITY) && ((Integer) blockState.m_61143_(BLOCK_PURITY)).intValue() - 1 >= 0) ? ((Integer) blockState.m_61143_(BLOCK_PURITY)).intValue() - 1 : 3;
                int blockPurity = iIntValue;
                TickHelper.nextTick(level, () -> {
                    BlockState blockState1 = level.m_8055_(pos);
                    if (!blockState1.m_61138_(BLOCK_PURITY)) {
                        return;
                    }
                    level.m_7731_(pos, (BlockState) blockState1.m_61124_(BLOCK_PURITY, Integer.valueOf(Math.min(purity, blockPurity) + 1)), 0);
                });
            }
        }
    }

    @Deprecated
    public static void addContainer(ContainerWithPurity container) {
        waterContainers.add(container);
    }

    public static ItemStack getFilledContainer(ItemStack container, boolean fromFilled) {
        for (ContainerWithPurity waterContainer : waterContainers) {
            if ((!fromFilled && waterContainer.equalsEmpty(container)) || (fromFilled && waterContainer.equalsFilled(container))) {
                return waterContainer.getFilledItem().m_41777_();
            }
        }
        return ItemStack.f_41583_.m_41777_();
    }

    @SubscribeEvent
    static void harvestRunningWater(PlayerInteractEvent.RightClickItem event) {
        SoundEvent sound;
        ItemStack filledItem;
        if (event.getEntity() == null) {
            return;
        }
        ItemStack item = event.getItemStack();
        if (!canHarvestRunningWater(item)) {
            return;
        }
        Player player = event.getEntity();
        Level level = player.m_9236_();
        BlockPos blockPos = MathHelper.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).m_82425_();
        if (!level.m_6425_(blockPos).m_205070_(FluidTags.f_13131_)) {
            return;
        }
        if (item.m_41720_() == Items.f_42590_ && !level.m_6425_(blockPos).m_76170_()) {
            sound = SoundEvents.f_11770_;
            filledItem = PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_);
        } else if (item.m_41720_() == ItemInit.TERRACOTTA_BOWL.get()) {
            sound = SoundEvents.f_11781_;
            filledItem = new ItemStack((ItemLike) ItemInit.TERRACOTTA_WATER_BOWL.get());
        } else {
            return;
        }
        level.m_6263_(player, player.m_20185_(), player.m_20186_(), player.m_20189_(), sound, SoundSource.NEUTRAL, 1.0f, 1.0f);
        level.m_142346_(player, GameEvent.f_157816_, blockPos);
        CompoundTag tag = filledItem.m_41784_();
        tag.m_128405_("Purity", getBlockPurity(level, blockPos));
        ItemStack result = ItemUtils.m_41813_(item, player, filledItem);
        player.m_21008_(event.getHand(), result);
        event.setCanceled(true);
    }

    @SubscribeEvent
    static void renderPurityTooltip(ItemTooltipEvent event) {
        int purity;
        if (isWaterFilledContainer(event.getItemStack()) && (purity = getPurity(event.getItemStack())) >= 0 && purity <= 3) {
            String purityText = getPurityText(purity);
            int purityColor = getPurityColor(purity);
            if (!$assertionsDisabled && purityText == null) {
                throw new AssertionError();
            }
            event.getToolTip().add(MutableComponent.m_237204_(new LiteralContents(purityText)).m_6270_(Style.f_131099_.m_178520_(purityColor)));
        }
    }

    public static boolean isWaterFilledContainer(ItemStack item) {
        for (ContainerWithPurity waterContainer : waterContainers) {
            if (waterContainer.equalsFilled(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmptyWaterContainer(ItemStack item) {
        for (ContainerWithPurity waterContainer : waterContainers) {
            if (waterContainer.equalsEmpty(item)) {
                return true;
            }
        }
        return false;
    }

    static boolean isFillableBlock(Block block) {
        for (Block fillable : fillablesWithPurity) {
            if (fillable == block) {
                return true;
            }
        }
        return false;
    }

    static boolean isFillableBlock(BlockState blockState) {
        return isFillableBlock(blockState.m_60734_());
    }

    static boolean canHarvestRunningWater(ItemStack item) {
        for (ContainerWithPurity waterContainer : waterContainers) {
            if (waterContainer.equalsEmpty(item) && waterContainer.canHarvestRunningWater()) {
                return true;
            }
        }
        return false;
    }

    public static int getPurity(ItemStack item) {
        if (!item.m_41784_().m_128441_("Purity")) {
            if (tanLoaded && Objects.equals(item.m_41720_().getCreatorModId(item), "toughasnails")) {
                return tanPurity(item);
            }
            return ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue();
        }
        return ((CompoundTag) Objects.requireNonNull(item.m_41783_())).m_128451_("Purity");
    }

    public static int tanPurity(ItemStack item) {
        if (item.m_150930_(TANItems.DIRTY_WATER_BOTTLE)) {
            return 0;
        }
        EmptyCanteenItem emptyCanteenItemM_41720_ = item.m_41720_();
        if (emptyCanteenItemM_41720_ instanceof EmptyCanteenItem) {
            EmptyCanteenItem canteenItem = emptyCanteenItemM_41720_;
            if (item.m_41720_().equals(canteenItem.getDirtyWaterCanteen())) {
                return 0;
            }
            if (item.m_41720_().equals(canteenItem.getWaterCanteen())) {
                return 2;
            }
            return 3;
        }
        return 3;
    }

    public static int getPurity(FluidStack fluid) {
        if (!fluid.getOrCreateTag().m_128441_("Purity")) {
            return ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue();
        }
        return fluid.getTag().m_128451_("Purity");
    }

    public static String getPurityText(int purity) {
        String str;
        if (purity == -1) {
            return null;
        }
        if (purity == 0) {
            str = "dirty";
        } else if (purity == 1) {
            str = "slightly_dirty";
        } else {
            str = purity == 2 ? "acceptable" : "purified";
        }
        String purityText = str;
        return MutableComponent.m_237204_(new TranslatableContents("thirst.purity." + purityText, purityText, TranslatableContents.f_237494_)).getString();
    }

    public static int getPurityColor(int purity) {
        if (purity == 0) {
            return 11028517;
        }
        if (purity == 1) {
            return 7957617;
        }
        return purity == 2 ? 6128285 : 2208255;
    }

    public static int getBlockPurity(BlockState blockState) {
        if (blockState.m_61138_(BLOCK_PURITY)) {
            return ((Integer) blockState.m_61143_(BLOCK_PURITY)).intValue() - 1;
        }
        return -1;
    }

    public static boolean hasPurity(ItemStack item) {
        if (!item.m_41782_()) {
            return false;
        }
        if ($assertionsDisabled || item.m_41783_() != null) {
            return item.m_41783_().m_128441_("Purity");
        }
        throw new AssertionError();
    }

    public static boolean hasPurity(FluidStack fluid) {
        if (!fluid.hasTag()) {
            return false;
        }
        return fluid.getTag().m_128441_("Purity");
    }

    public static ItemStack addPurity(ItemStack item, BlockPos pos, Level level) {
        CompoundTag tag = item.m_41784_();
        tag.m_128405_("Purity", getBlockPurity(level, pos));
        return item;
    }

    public static ItemStack addPurity(ItemStack item, int purity) {
        CompoundTag tag = item.m_41784_();
        if (purity == ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue()) {
            tag.m_128473_("Purity");
        } else {
            tag.m_128405_("Purity", purity);
        }
        return item;
    }

    public static FluidStack addPurity(FluidStack fluid, int purity) {
        CompoundTag tag = fluid.getOrCreateTag();
        if (purity == ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue()) {
            tag.m_128473_("Purity");
        } else {
            tag.m_128405_("Purity", purity);
        }
        return fluid;
    }

    public static int getBlockPurity(Level level, BlockPos pos) {
        int purity = ((pos.m_123342_() > ((Number) CommonConfig.MOUNTAINS_Y.get()).intValue() || pos.m_123342_() < ((Number) CommonConfig.CAVES_Y.get()).intValue()) && pos.m_123342_() < ((Number) CommonConfig.MOUNTAINS_Y.get()).intValue() - 32) ? 1 : 0;
        if (level.m_6425_(pos).m_205070_(FluidTags.f_13131_)) {
            if (!level.m_6425_(pos).m_76170_()) {
                purity = Math.min(purity + ((Number) CommonConfig.RUNNING_WATER_PURIFICATION_AMOUNT.get()).intValue(), 3);
            }
            return purity;
        }
        if (level.m_8055_(pos).m_60713_(Blocks.f_152476_)) {
            return ((Integer) level.m_8055_(pos).m_61143_(BLOCK_PURITY)).intValue() - 1;
        }
        return ((Integer) CommonConfig.DEFAULT_PURITY.get()).intValue();
    }

    public static boolean givePurityEffects(Player player, ItemStack item) {
        if (isWaterFilledContainer(item) && hasPurity(item)) {
            return givePurityEffects(player, ThirstHelper.getPurity(item));
        }
        return true;
    }

    public static boolean givePurityEffects(Player player, int purity) {
        boolean shouldRegenerate = true;
        Random random = new Random();
        float chance = random.nextFloat();
        switch (purity) {
            case MIN_PURITY /* 0 */:
                if (chance < ((Number) CommonConfig.DIRTY_NAUSEA_PERCENTAGE.get()).intValue() / 100.0f && (player instanceof ServerPlayer)) {
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 100, 0));
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19612_, 600, 0));
                }
                if (chance <= ((Number) CommonConfig.DIRTY_POISON_PERCENTAGE.get()).intValue() / 100.0f) {
                    if (player instanceof ServerPlayer) {
                        player.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 200, 0));
                    }
                    shouldRegenerate = false;
                    break;
                }
                break;
            case 1:
                if (chance < ((Number) CommonConfig.SLIGHTLY_DIRTY_NAUSEA_PERCENTAGE.get()).intValue() / 100.0f && (player instanceof ServerPlayer)) {
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 100, 0));
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19612_, 600, 0));
                }
                if (chance <= ((Number) CommonConfig.SLIGHTLY_DIRTY_POISON_PERCENTAGE.get()).intValue() / 100.0f) {
                    if (player instanceof ServerPlayer) {
                        player.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 200, 0));
                    }
                    shouldRegenerate = false;
                    break;
                }
                break;
            case 2:
                if (chance < ((Number) CommonConfig.ACCEPTABLE_NAUSEA_PERCENTAGE.get()).intValue() / 100.0f && (player instanceof ServerPlayer)) {
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 100, 0));
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19612_, 600, 0));
                }
                if (chance <= ((Number) CommonConfig.ACCEPTABLE_POISON_PERCENTAGE.get()).intValue() / 100.0f) {
                    if (player instanceof ServerPlayer) {
                        player.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 200, 0));
                    }
                    shouldRegenerate = false;
                    break;
                }
                break;
            case MAX_PURITY /* 3 */:
                if (chance < ((Number) CommonConfig.PURIFIED_NAUSEA_PERCENTAGE.get()).intValue() / 100.0f && (player instanceof ServerPlayer)) {
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19604_, 100, 0));
                    player.m_7292_(new MobEffectInstance(MobEffects.f_19612_, 600, 0));
                }
                if (chance <= ((Number) CommonConfig.PURIFIED_POISON_PERCENTAGE.get()).intValue() / 100.0f) {
                    if (player instanceof ServerPlayer) {
                        player.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 200, 0));
                    }
                    shouldRegenerate = false;
                    break;
                }
                break;
        }
        return shouldRegenerate || ((Boolean) CommonConfig.QUENCH_THIRST_WHEN_DEBUFFED.get()).booleanValue();
    }

    static void registerDispenserBehaviours() {
        Method getDispenseMethod = ObfuscationReflectionHelper.findMethod(DispenserBlock.class, "m_7216_", new Class[]{ItemStack.class});
        DispenseItemBehavior bucketDefaultBehaviour = (DispenseItemBehavior) ReflectionUtil.fuckYouReflections(getDispenseMethod, Blocks.f_50061_, new ItemStack(Items.f_42446_));
        DispenseItemBehavior bottleDefaultBehaviour = (DispenseItemBehavior) ReflectionUtil.fuckYouReflections(getDispenseMethod, Blocks.f_50061_, new ItemStack(Items.f_42590_));
        Method execute = ObfuscationReflectionHelper.findMethod(DefaultDispenseItemBehavior.class, "m_7498_", new Class[]{BlockSource.class, ItemStack.class});
        DispenserBlock.m_52672_(Items.f_42446_, (block, item) -> {
            ServerLevel serverLevelM_7727_ = block.m_7727_();
            BlockPos blockpos = block.m_7961_().m_121945_(block.m_6414_().m_61143_(DispenserBlock.f_52659_));
            if (serverLevelM_7727_.m_6425_(blockpos).m_205070_(FluidTags.f_13131_) && serverLevelM_7727_.m_8055_(blockpos).m_60819_().m_76170_()) {
                ItemStack result = new ItemStack(Items.f_42447_);
                return getStack(block, item, serverLevelM_7727_, blockpos, result, true);
            }
            return (ItemStack) ReflectionUtil.fuckYouReflections(execute, bucketDefaultBehaviour, block, item);
        });
        DispenserBlock.m_52672_(Items.f_42590_, (block2, item2) -> {
            ServerLevel serverLevelM_7727_ = block2.m_7727_();
            BlockPos blockpos = block2.m_7961_().m_121945_(block2.m_6414_().m_61143_(DispenserBlock.f_52659_));
            if (serverLevelM_7727_.m_6425_(blockpos).m_205070_(FluidTags.f_13131_)) {
                ItemStack result = PotionUtils.m_43549_(new ItemStack(Items.f_42589_), Potions.f_43599_);
                return getStack(block2, item2, serverLevelM_7727_, blockpos, result, false);
            }
            return (ItemStack) ReflectionUtil.fuckYouReflections(execute, bottleDefaultBehaviour, block2, item2);
        });
    }

    @NotNull
    private static ItemStack getStack(BlockSource block, ItemStack item, Level level, BlockPos blockpos, ItemStack result, boolean pickupBlock) {
        level.m_142346_((Entity) null, GameEvent.f_157816_, blockpos);
        addPurity(result, blockpos, level);
        if (pickupBlock) {
            level.m_8055_(blockpos).m_60734_().m_142598_(level, blockpos, level.m_8055_(blockpos));
        }
        item.m_41774_(1);
        if (item.m_41619_()) {
            return result;
        }
        if (block.m_8118_().m_59237_(result) < 0) {
            new DefaultDispenseItemBehavior().m_6115_(block, result);
        }
        return item;
    }

    public static boolean matchRecipe(FluidStack stack, FluidStack other) {
        return (stack.getTag() == null || stack.getTag().m_128456_()) ? other.getTag() == null || other.getTag().m_128456_() : other.getTag() != null && stack.getTag().equals(other.getTag());
    }
}
