package dev.ghen.thirst.content.purity;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/content/purity/ContainerWithPurity.class */
public class ContainerWithPurity {
    private ItemStack filledItem;
    private ItemStack emptyItem;
    private final boolean isDrinkable;
    private final boolean isStatic;
    private Predicate<ItemStack> equalsFilled;
    private Predicate<ItemStack> equalsEmpty;
    private boolean canHarvestRunningWater;

    public ContainerWithPurity(ItemStack emptyItem, ItemStack filledItem) {
        this.emptyItem = emptyItem;
        this.filledItem = filledItem;
        this.isDrinkable = true;
        this.isStatic = false;
        this.canHarvestRunningWater = true;
        fillPredicates();
    }

    public ContainerWithPurity(ItemStack emptyItem, ItemStack filledItem, boolean isDrinkable) {
        this.emptyItem = emptyItem;
        this.filledItem = filledItem;
        this.isDrinkable = isDrinkable;
        this.isStatic = false;
        this.canHarvestRunningWater = true;
        fillPredicates();
    }

    public ContainerWithPurity(ItemStack filledItem) {
        this.emptyItem = null;
        this.filledItem = filledItem;
        this.isDrinkable = true;
        this.isStatic = true;
        this.canHarvestRunningWater = false;
        fillPredicates();
    }

    public ContainerWithPurity canHarvestRunningWater(boolean canHarvestRunningWater) {
        this.canHarvestRunningWater = canHarvestRunningWater;
        return this;
    }

    public boolean canHarvestRunningWater() {
        return this.canHarvestRunningWater;
    }

    void fillPredicates() {
        this.equalsFilled = itemStack -> {
            return itemStack.m_41720_() == this.filledItem.m_41720_();
        };
        this.equalsEmpty = itemStack2 -> {
            return itemStack2.m_41720_() == this.emptyItem.m_41720_();
        };
    }

    public ContainerWithPurity setEqualsEmpty(Predicate<ItemStack> predicate) {
        this.equalsEmpty = predicate;
        return this;
    }

    public ContainerWithPurity setEqualsFilled(Predicate<ItemStack> predicate) {
        this.equalsFilled = predicate;
        return this;
    }

    public boolean equalsEmpty(ItemStack item) {
        return !this.isStatic && this.equalsEmpty.test(item);
    }

    public boolean equalsFilled(ItemStack item) {
        return this.equalsFilled.test(item);
    }

    public boolean isDrinkable() {
        return this.isDrinkable;
    }

    public ItemStack getFilledItem() {
        return this.filledItem;
    }

    public void setFilledItem(ItemStack filledItem) {
        this.filledItem = filledItem;
    }

    public ItemStack getEmptyItem() {
        return this.emptyItem;
    }

    public void setEmptyItem(ItemStack emptyItem) {
        this.emptyItem = emptyItem;
    }
}
