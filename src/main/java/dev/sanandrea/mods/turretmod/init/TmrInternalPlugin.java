/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.init;

import dev.sanandrea.mods.turretmod.api.ITmrPlugin;
import dev.sanandrea.mods.turretmod.api.TmrPlugin;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunitionRegistry;
import dev.sanandrea.mods.turretmod.api.ammo.IProjectileRegistry;
import dev.sanandrea.mods.turretmod.api.assembly.IAssemblyManager;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuClientRegistry;
import dev.sanandrea.mods.turretmod.api.repairkit.IRepairKitRegistry;
import dev.sanandrea.mods.turretmod.api.tcu.ITcuRegistry;
import dev.sanandrea.mods.turretmod.api.turret.ITurretRegistry;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeRegistry;
import dev.sanandrea.mods.turretmod.client.init.TcuClientRegistry;
import dev.sanandrea.mods.turretmod.client.renderer.turret.LabelRegistry;
import dev.sanandrea.mods.turretmod.entity.projectile.Projectiles;
import dev.sanandrea.mods.turretmod.entity.turret.Turrets;
import dev.sanandrea.mods.turretmod.item.TurretControlUnit;
import dev.sanandrea.mods.turretmod.item.ammo.Ammunitions;
import dev.sanandrea.mods.turretmod.item.repairkits.RepairKits;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import dev.sanandrea.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO: reimplement
@TmrPlugin
public class TmrInternalPlugin
        implements ITmrPlugin
{
    @Override
    public void registerTurrets(ITurretRegistry registry) {
        Turrets.register(registry);
    }

    @Override
    public void registerRepairKits(IRepairKitRegistry registry) {
        RepairKits.register(registry);
    }

    @Override
    public void registerAmmo(IAmmunitionRegistry registry) {
        Ammunitions.register(registry);
    }

    @Override
    public void registerUpgrades(IUpgradeRegistry registry) {
        Upgrades.register(registry);
    }

    @Override
    public void setup() {
//        ITargetProcessor.TARGET_BUS.register(new TargetingEventHandler());
    }

    @Override
    public void registerTcuPages(ITcuRegistry registry) {
        TurretControlUnit.register(registry);
    }

    @Override
    public void registerProjectiles(IProjectileRegistry registry) {
        Projectiles.register(registry);
    }

    @Override
    public void manageAssembly(IAssemblyManager manager) {
        AssemblyManager.registerGroupValues(manager);
    }

    //    @Override
//    public void registerTurretRenderLayers(ITurretRenderRegistry<?> registry) {
//        TurretRenderer.initializeLayers(registry);
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerTcuClient(ITcuClientRegistry registry) {
        LabelRegistry.register(registry.getLabelRegistry());
        TcuClientRegistry.registerTcuClient(registry);
    }

    public static int getSortId(ITmrPlugin p1, ITmrPlugin p2) {
        if( p1 instanceof TmrInternalPlugin ) {
            return -1;
        }

        return p2 instanceof TmrInternalPlugin ? 1 : 0;
    }
}
