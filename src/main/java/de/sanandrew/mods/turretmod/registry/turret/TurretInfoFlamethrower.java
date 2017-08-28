/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInfo;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class TurretInfoFlamethrower
        implements ITurretInfo
{
    @Override
    public String getName() {
        return "turret_iii_flamethrower";
    }

    @Override
    public UUID getUUID() {
        return EntityTurretFlamethrower.TIII_UUID;
    }

    @Override
    public Class<? extends EntityTurret> getTurretClass() {
        return EntityTurretFlamethrower.class;
    }

    @Override
    public float getTurretHealth() {
        return 40.0F;
    }

    @Override
    public int getBaseAmmoCapacity() {
        return 4096;
    }

    @Override
    public ResourceLocation getModel() {
        return EntityTurretFlamethrower.ITEM_MODEL;
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.TURRET_MK3_FT;
    }

    @Override
    public String getInfoRange() {
        return "8";
    }
}
