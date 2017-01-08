/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.event.TargetingEvents;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKits;

@TmrPlugin
public class TmrInternalPlugin
        implements ITmrPlugin
{
    @Override
    public void registerAssemblyRecipes(ITurretAssemblyRegistry registry) {
        TurretAssemblyRecipes.initialize(registry);
    }

    @Override
    public void registerRepairKits(IRepairKitRegistry registry) {
        RepairKits.initialize(registry);
    }

    @Override
    public void postInit() {
        ITargetProcessor.TARGET_BUS.register(new TargetingEvents());
    }
}
