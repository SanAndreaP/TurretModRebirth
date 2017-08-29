/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.TurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.event.TargetingEventHandler;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmunitions;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKits;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@TmrPlugin
public class TmrInternalPlugin
        implements ITmrPlugin
{
    public static ITurretAmmoRegistry ammoRegistry;

    @Override
    public void registerAssemblyRecipes(ITurretAssemblyRegistry registry) {
        TurretAssemblyRecipes.initialize(registry);
    }

    @Override
    public void registerTurrets(ITurretRegistry registry) {
        Turrets.initialize(registry);
    }

    @Override
    public void registerRepairKits(IRepairKitRegistry registry) {
        RepairKits.initialize(registry);
    }

    @Override
    public void registerAmmo(ITurretAmmoRegistry registry) {
        ammoRegistry = registry;
        TurretAmmunitions.initialize(registry);
    }

    @Override
    public void registerUpgrades(IUpgradeRegistry registry) {
        UpgradeRegistry.initialize(registry);
    }

    @Override
    public void postInit() {
        ITargetProcessor.TARGET_BUS.register(new TargetingEventHandler());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTurretInfoCategories(ITurretInfoCategoryRegistry registry) {
        TurretInfoCategoryRegistry.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTurretRenderer(ITurretRenderRegistry<?> registry) {
        RenderTurret.initialize(registry);
    }
}
