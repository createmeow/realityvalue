package dev.anye.mc.reality_value.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerExProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerExCap> PlayerExCap = CapabilityManager.get(new CapabilityToken<>() {
    });
    private PlayerExCap playerExCap = null;
    private final LazyOptional<PlayerExCap> optional = LazyOptional.of(this::create);
    private PlayerExCap create() {
        if (playerExCap == null){
            playerExCap = new PlayerExCap();
        }
        return playerExCap;
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PlayerExCap){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        create().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        create().loadNBTData(nbt);
    }
}
