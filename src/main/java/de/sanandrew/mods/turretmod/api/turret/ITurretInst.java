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

import java.util.function.Supplier;

public interface ITurretInst
{
    EntityLiving get();

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

    <V extends ITurretRAM> V getRAM(Supplier<V> onNull);

    void updateState();

    ITurret.AttackType getAttackType();
}
