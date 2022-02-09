package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.config.Targets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class TmrConfig
{
    public static final Targets         TARGETS;
    static final        ForgeConfigSpec TARGETS_SPEC;

    static {
        final Pair<Targets, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Targets::buildConfig);
        TARGETS_SPEC = specPair.getRight();
        TARGETS = specPair.getLeft();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TmrConfig.TARGETS_SPEC, TmrConstants.ID + "-common-targets.toml");
    }
}
