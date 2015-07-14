/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public interface Turret
{
    EntityLiving getEntity();

    void setOwner(EntityPlayer player);

    int getAmmo();

    int getMaxAmmo();

    float getHealth();

    float getMaxHealth();

    TurretInfo<? extends Turret> getInfo();

    void depleteAmmo(int amount);

    AxisAlignedBB getRangeBB();

    ItemStack getAmmoTypeItem();

    int addAmmo(ItemStack stack);

    String getTurretName();

    UpgradeHandler<? extends Turret> getUpgradeHandler();

    TargetHandler<? extends Turret> getTargetHandler();
}
