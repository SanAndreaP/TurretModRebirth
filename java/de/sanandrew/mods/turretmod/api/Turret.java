/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.util.TurretInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

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
}
