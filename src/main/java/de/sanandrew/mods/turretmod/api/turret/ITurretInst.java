/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public interface ITurretInst
{
    LivingEntity get();

    ITurret getTurret();

    ITextComponent getOwnerName();

    AxisAlignedBB getRangeBB();

    boolean isBuoy();

    SoundEvent getShootSound();

    SoundEvent getNoAmmoSound();

    boolean wasShooting();

    void setShooting();

    boolean applyRepairKit(ItemStack stack);

    ITargetProcessor getTargetProcessor();

    //TODO: reimplement upgrades
//    IUpgradeProcessor getUpgradeProcessor();

    boolean isActive();

    void setActive(boolean isActive);

    boolean showRange();

    void setShowRange(boolean showRange);

    boolean hasPlayerPermission(PlayerEntity player);

    boolean isInGui();

    <V extends ITurretRAM> V getRAM(Supplier<V> onNull);

    void updateState();

    ITurret.AttackType getAttackType();

//    TileEntityTurretCrate dismantle();

    IVariant getVariant();

    void setVariant(Object variantId);

    @OnlyIn(Dist.CLIENT)
    int getPartBrightnessForRender(double partY);
}
