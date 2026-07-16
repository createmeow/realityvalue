package dev.anye.mc.reality_value.config;

import com.google.gson.reflect.TypeToken;
import dev.anye.mc.reality_value.RealityValue;
import dev.anye.mc.reality_value.lib._File;
import dev.anye.mc.reality_value.lib._JsonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PlaceBlockList extends _JsonConfig<List<Vec3>> {
    public PlaceBlockList(String filePath) {
        super(filePath, """
                [
                ]
                """, new TypeToken<>() {
        });
    }

    @Override
    public List<Vec3> getDatas() {
        if (datas == null)
            this.datas = new ArrayList<>();
        return super.getDatas();
    }

    public void add(BlockPos blockPos) {
        getDatas().add(blockPos.getCenter());
        save();
    }

    public boolean has(BlockPos blockPos) {
        return getDatas().contains(blockPos.getCenter());
    }

    public static PlaceBlockList get(ChunkAccess chunkAccess) {
        return get(chunkAccess.getPos());
    }

    public static PlaceBlockList get(ChunkPos chunkPos) {
        return new PlaceBlockList(
                _File.getFilePath(RealityValue.CONFIG_PlaceBlock_DIR, chunkPos.x + "," + chunkPos.z + ".json"));
    }
}