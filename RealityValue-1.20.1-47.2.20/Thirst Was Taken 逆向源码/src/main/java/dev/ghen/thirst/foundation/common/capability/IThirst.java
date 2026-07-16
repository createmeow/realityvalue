package dev.ghen.thirst.foundation.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/common/capability/IThirst.class */
public interface IThirst {
    int getThirst();

    void setThirst(int i);

    int getQuenched();

    void setQuenched(int i);

    float getExhaustion();

    void setExhaustion(float f);

    void addExhaustion(Player player, float f);

    void tick(Player player);

    void drink(Player player, int i, int i2);

    void updateThirstData(Player player);

    void setJustHealed();

    void ExhaustionRecalculate();

    void setShouldTickThirst(boolean z);

    boolean getShouldTickThirst();

    void copy(IThirst iThirst);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag compoundTag);
}
