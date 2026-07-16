package dev.ghen.thirst.foundation.common.damagesource;

import dev.ghen.thirst.Thirst;
import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/common/damagesource/ModDamageSource.class */
public class ModDamageSource {
    public static final ResourceKey<DamageType> DIE_OF_THIRST_KEY = ResourceKey.m_135785_(Registries.f_268580_, new ResourceLocation(Thirst.f0ID, "dehydrate".toLowerCase(Locale.ROOT)));

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type) {
        return new DamageSource(level.m_9598_().m_175515_(Registries.f_268580_).m_246971_(type), (Entity) null, (Entity) null);
    }
}
