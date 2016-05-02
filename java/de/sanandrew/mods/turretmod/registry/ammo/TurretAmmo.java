/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface TurretAmmo
{
    String getName();
    UUID getUUID();
    UUID getTypeUUID();
    String getItemDesc();
    String getInfoDesc();
    int getAmmoCapacity();
    Class<? extends IProjectile> getEntity();
    Class<? extends EntityTurret> getTurret();
    float getInfoDamage();
    String getIcon();
    ItemStack getStoringAmmoItem();
}
