/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public interface ITurretInst
{
    EntityLiving get();

    ITurret getTurret();

    String getOwnerName();

    AxisAlignedBB getRangeBB();

    boolean isBuoy();

    SoundEvent getShootSound();

    SoundEvent getNoAmmoSound();

    boolean wasShooting();

    void setShooting();

    boolean applyRepairKit(ItemStack stack);

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

    TileEntityTurretCrate dismantle();

    int getVariant();

    void setVariant(int variant);

    @SideOnly(Side.CLIENT)
    int getPartBrightnessForRender(double partY);
}
