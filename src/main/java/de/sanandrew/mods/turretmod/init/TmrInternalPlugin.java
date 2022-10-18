/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuClientRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.tcu.ITcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.client.init.TcuClientRegistry;
import de.sanandrew.mods.turretmod.client.renderer.turret.LabelRegistry;
import de.sanandrew.mods.turretmod.entity.projectile.Projectiles;
import de.sanandrew.mods.turretmod.entity.turret.Turrets;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.item.repairkits.RepairKits;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
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
