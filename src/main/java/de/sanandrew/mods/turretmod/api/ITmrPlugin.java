package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;

public interface ITmrPlugin
{
    void registerAssemblyRecipes(ITurretAssemblyRegistry registry);

    void registerRepairKits(IRepairKitRegistry registry);

    void postInit();
}
