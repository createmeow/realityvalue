package dev.ghen.thirst.compat.create;

import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.ghen.thirst.Thirst;
import net.minecraft.world.level.block.Block;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/compat/create/CreateRegistry.class */
public class CreateRegistry {
    public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> {
        return Registrate.create(Thirst.f0ID);
    });
    public static final BlockEntry<SandFilterBlock> SAND_FILTER_BLOCK = ((Registrate) REGISTRATE.get()).block("sand_filter", SandFilterBlock::new).initialProperties(SharedProperties::copperMetal).blockstate((ctx, prov) -> {
        prov.simpleBlock((Block) ctx.getEntry(), AssetLookup.partialBaseModel(ctx, prov, new String[0]));
    }).item((v1, v2) -> {
        return new AssemblyOperatorBlockItem(v1, v2);
    }).transform(ModelGen.customItemModel()).register();
    public static final BlockEntityEntry<SandFilterTileEntity> SAND_FILTER_TE = ((Registrate) REGISTRATE.get()).blockEntity("sand_filter", SandFilterTileEntity::new).validBlocks(new NonNullSupplier[]{SAND_FILTER_BLOCK}).register();

    public static void register() {
    }
}
