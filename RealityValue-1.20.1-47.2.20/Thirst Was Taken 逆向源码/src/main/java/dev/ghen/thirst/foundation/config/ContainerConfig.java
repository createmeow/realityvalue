package dev.ghen.thirst.foundation.config;

import dev.ghen.thirst.Thirst;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/config/ContainerConfig.class */
public class ContainerConfig {
    private static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CONTAINERS;

    static {
        BUILDER.push("Container");
        CONTAINERS = BUILDER.comment(new String[]{"Defineds drinks will be influenced by purity", "Format: [\"examplemod:example_item_1\", \"examplemod:example_item_2\"]"}).defineList("Containers", Arrays.asList("collectorsreap:pomegranate_black_tea", "collectorsreap:lime_green_tea", "create:builders_tea"), it -> {
            return it instanceof String;
        });
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void setup() throws IOException {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path configFolder = Paths.get(configPath.toAbsolutePath().toString(), Thirst.f0ID);
        try {
            Files.createDirectory(configFolder, new FileAttribute[0]);
        } catch (Exception e) {
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "thirst/container.toml");
    }
}
