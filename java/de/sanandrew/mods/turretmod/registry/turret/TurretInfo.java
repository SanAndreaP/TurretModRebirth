package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface TurretInfo
{
    String getName();
    UUID getUUID();
    Class<? extends EntityTurret> getTurretClass();
    float getHealth();
    int getBaseAmmoCapacity();
    String getIcon();
}
