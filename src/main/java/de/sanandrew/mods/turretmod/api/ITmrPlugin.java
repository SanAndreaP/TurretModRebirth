package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;

public interface ITmrPlugin
{
    default void preInit(ITmrUtils utils) { }

    default void registerAssemblyRecipes(ITurretAssemblyRegistry registry) { }

    default void registerRepairKits(IRepairKitRegistry registry) { }

    default void registerAmmo(ITurretAmmoRegistry registry) { }

    default void postInit() { }
}
