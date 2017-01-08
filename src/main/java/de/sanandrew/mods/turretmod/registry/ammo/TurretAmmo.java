/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface TurretAmmo<T extends Entity & IProjectile>
{
    String getName();
    UUID getId();
    UUID getTypeId();
    UUID getGroupId();
    String getInfoName();
    float getInfoDamage();
    UUID getRecipeId();
    int getAmmoCapacity();
    Class<T> getEntityClass();
    T getEntity(EntityTurret turret);
    Class<? extends EntityTurret> getTurret();
    ResourceLocation getModel();
    ItemStack getStoringAmmoItem();
}
