package dev.ghen.thirst.compat.create.ponder;

import com.simibubi.create.foundation.ponder.PonderWorldBlockEntityFix;
import dev.ghen.thirst.Thirst;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/compat/create/ponder/ThirstPonderPlugin.class */
public class ThirstPonderPlugin implements PonderPlugin {
    @NotNull
    public String getModId() {
        return Thirst.f0ID;
    }

    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ThirstPonders.registerScenes(helper);
    }

    public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        ThirstPonders.registerTags(helper);
    }

    public void onPonderLevelRestore(@NotNull PonderLevel ponderLevel) {
        PonderWorldBlockEntityFix.fixControllerBlockEntities(ponderLevel);
    }
}
