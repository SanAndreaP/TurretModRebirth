package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface TurretInfo
{
    String getName();
    UUID getUUID();
    Class<? extends EntityTurret> getTurretClass();
    float getTurretHealth();
    int getBaseAmmoCapacity();
    ResourceLocation getModel();
    UUID getRecipeId();
    String getInfoRange();
}
