/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

public interface ITurretInst
{
    EntityLiving getEntity();

    ITurret getTurret();

    String getOwnerName();

    AxisAlignedBB getRangeBB();

    boolean isUpsideDown();

    SoundEvent getShootSound();

    SoundEvent getNoAmmoSound();

    boolean wasShooting();

    void setShooting();

    ITargetProcessor getTargetProcessor();

    IUpgradeProcessor getUpgradeProcessor();

    boolean isActive();

    void setActive(boolean isActive);

    boolean showRange();

    void setShowRange(boolean showRange);

    boolean hasPlayerPermission(EntityPlayer player);

    boolean isInGui();

    <V> void setField(int index, V value);

    <V> V getField(int index);
}
