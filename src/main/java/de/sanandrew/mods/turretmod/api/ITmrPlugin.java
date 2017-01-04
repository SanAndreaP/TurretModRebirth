package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;

public interface ITmrPlugin
{
    void registerAssemblyRecipes(ITurretAssemblyRegistry registry);

}
