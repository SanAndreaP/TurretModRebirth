/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api;

import dev.sanandrea.mods.turretmod.api.ammo.IAmmunitionRegistry;
import dev.sanandrea.mods.turretmod.api.ammo.IProjectileRegistry;
import dev.sanandrea.mods.turretmod.api.assembly.IAssemblyManager;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuClientRegistry;
import dev.sanandrea.mods.turretmod.api.repairkit.IRepairKitRegistry;
import dev.sanandrea.mods.turretmod.api.tcu.ITcuRegistry;
import dev.sanandrea.mods.turretmod.api.turret.ITurretRegistry;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITmrPlugin
{
    default void registerTurrets(ITurretRegistry registry) { }

    default void registerRepairKits(IRepairKitRegistry registry) { }

    default void registerAmmo(IAmmunitionRegistry registry) { }

    default void registerUpgrades(IUpgradeRegistry registry) { }

    default void setup() { }

    default void registerTcuPages(ITcuRegistry registry) { }

    default void registerProjectiles(IProjectileRegistry registry) { }

//    @Override
//    public void registerTurretRenderLayers(ITurretRenderRegistry<?> registry) {
//        TurretRenderer.initializeLayers(registry);
//    }

    @OnlyIn(Dist.CLIENT)
    default void registerTcuClient(ITcuClientRegistry registry) { }

    default void manageAssembly(IAssemblyManager manager) { }
}
