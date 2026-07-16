package dev.ghen.thirst.compat.create.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.compat.create.CreateRegistry;
import dev.ghen.thirst.compat.create.ponder.scene.SandFilterScene;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/compat/create/ponder/ThirstPonders.class */
public class ThirstPonders {
    public static final ResourceLocation PURIFICATION = Thirst.asResource("purification");

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction((v0) -> {
            return v0.getId();
        });
        HELPER.registerTag(PURIFICATION).addToIndex().item(CreateRegistry.SAND_FILTER_BLOCK, true, false).title("Purification").description("Components which purifying water").register();
    }

    public static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction((v0) -> {
            return v0.getId();
        });
        HELPER.addStoryBoard(CreateRegistry.SAND_FILTER_BLOCK, "sand_filter", SandFilterScene::filtering, new ResourceLocation[]{AllCreatePonderTags.FLUIDS, PURIFICATION});
    }
}
