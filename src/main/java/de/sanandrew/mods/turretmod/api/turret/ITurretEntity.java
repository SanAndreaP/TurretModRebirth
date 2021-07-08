/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface ITurretEntity
{
    LivingEntity get();

    ITurret getDelegate();

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

    boolean shouldShowRange();

    void setShowRange(boolean showRange);

    boolean hasPlayerPermission(PlayerEntity player);

    boolean isOwner(PlayerEntity player);

    @Nullable
    PlayerEntity getOwner();

    boolean isInGui();

    <V extends ITurretRAM> V getRAM(Supplier<V> onNull);

    void updateState();

    ITurret.TargetType getAttackType();

    IVariant getVariant();

    void setVariant(Object variantId);

    boolean canRemoteTransfer();

    @OnlyIn(Dist.CLIENT)
    int getPartBrightnessForRender(double partY);

    @OnlyIn(Dist.CLIENT)
    int getCameraQuality();

    TurretCrateEntity dismantle();
}
