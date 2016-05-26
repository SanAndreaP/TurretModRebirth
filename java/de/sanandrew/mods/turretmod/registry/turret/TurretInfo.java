package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;

import java.util.UUID;

public interface TurretInfo
{
    String getName();
    UUID getUUID();
    Class<? extends EntityTurret> getTurretClass();
    float getTurretHealth();
    int getBaseAmmoCapacity();
    String getIcon();
    UUID getRecipeId();
}
