/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.init;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.init.config.Targets;
import dev.sanandrea.mods.turretmod.init.config.TurretConfig;
import dev.sanandrea.mods.turretmod.init.config.UpgradeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;

public class TmrConfig
{
    public static final Targets         TARGETS;
    static final        ForgeConfigSpec TARGETS_SPEC;
    public static final TurretConfig    TURRETS;
    static final        ForgeConfigSpec TURRETS_SPEC;
    public static final UpgradeConfig   UPGRADES;
    static final        ForgeConfigSpec UPGRADES_SPEC;

    static {
        final Pair<Targets, ForgeConfigSpec> tgsp = new ForgeConfigSpec.Builder().configure(Targets::build);
        TARGETS_SPEC = tgsp.getRight();
        TARGETS = tgsp.getLeft();

        final Pair<TurretConfig, ForgeConfigSpec> trsp = new ForgeConfigSpec.Builder().configure(TurretConfig::build);
        TURRETS_SPEC = trsp.getRight();
        TURRETS = trsp.getLeft();

        final Pair<UpgradeConfig, ForgeConfigSpec> ugsp = new ForgeConfigSpec.Builder().configure(UpgradeConfig::build);
        UPGRADES_SPEC = ugsp.getRight();
        UPGRADES = ugsp.getLeft();
    }

    public static void register(IEventBus meb) {
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(TmrConstants.ID));
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TARGETS_SPEC, TmrConstants.ID + "/targets-common.toml");
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TURRETS_SPEC, TmrConstants.ID + "/turrets-common.toml");
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, UPGRADES_SPEC, TmrConstants.ID + "/upgrades-common.toml");

            meb.addListener((ModConfig.Loading e) -> {
                TARGETS.load();
                TURRETS.load();
                UPGRADES.load();
            });
            meb.addListener((ModConfig.Reloading e) -> {
                TARGETS.load();
                TURRETS.load();
                UPGRADES.load();
            });
        } catch( IOException ex ) {
            throw new RuntimeException(ex);
        }
    }

    private TmrConfig() { }
}
