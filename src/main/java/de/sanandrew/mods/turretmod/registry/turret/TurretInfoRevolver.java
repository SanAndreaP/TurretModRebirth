/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInfo;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class TurretInfoRevolver
        implements ITurretInfo
{
    @Override
    public String getName() {
        return "turret_ii_revolver";
    }

    @Override
    public UUID getUUID() {
        return EntityTurretRevolver.TII_UUID;
    }

    @Override
    public Class<? extends EntityTurret> getTurretClass() {
        return EntityTurretRevolver.class;
    }

    @Override
    public float getTurretHealth() {
        return 30.0F;
    }

    @Override
    public int getBaseAmmoCapacity() {
        return 256;
    }

    @Override
    public ResourceLocation getModel() {
        return EntityTurretRevolver.ITEM_MODEL;
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.TURRET_MK2_RV;
    }

    @Override
    public String getInfoRange() {
        return "20";
    }
}
