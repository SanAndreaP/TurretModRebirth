/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.TurretAssemblyRecipes;

import java.util.UUID;

public class TurretInfoT1Crossbow
        implements TurretInfo
{
    public static final UUID TI_UUID = UUID.fromString("50e1e69c-395c-486c-bb9d-41e82c8b22e2");

    @Override
    public String getName() {
        return "turret_crossbow";
    }

    @Override
    public UUID getUUID() {
        return TI_UUID;
    }

    @Override
    public Class<? extends EntityTurret> getTurretClass() {
        return EntityTurretCrossbow.class;
    }

    @Override
    public float getHealth() {
        return 20.0F;
    }

    @Override
    public int getBaseAmmoCapacity() {
        return 256;
    }

    @Override
    public String getIcon() {
        return "turret_crossbow";
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.TURRET_MK1_CB;
    }
}
