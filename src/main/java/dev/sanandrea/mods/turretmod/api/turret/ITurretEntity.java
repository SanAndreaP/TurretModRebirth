/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.turret;

import dev.sanandrea.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;
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

    IUpgradeProcessor getUpgradeProcessor();

    boolean isActive();

    void setActive(boolean isActive);

    boolean shouldShowRange();

    void setShowRange(boolean showRange);

    boolean hasPlayerPermission(PlayerEntity player);

    boolean isOwner(PlayerEntity player);

    @Nullable
    UUID getOwnerId();

    boolean hasOwner();

//    boolean isInGui();

    <V extends ITurretRAM> V getRAM(Supplier<V> onNull);

    void syncState(byte transferType);

    ITurret.TargetType getAttackType();

    IVariant getVariant();

    void setVariant(Object variantId);

    boolean canRemoteTransfer();

    @OnlyIn(Dist.CLIENT)
    int getPartBrightnessForRender(double partY);

    @OnlyIn(Dist.CLIENT)
    int getCameraQuality();

    TurretCrateEntity dismantle();

    ITextComponent getTurretTypeName();

    boolean hasClientForcefield(Class<? extends IForcefield> forcefieldClass);

    void addClientForcefield(IForcefield forcefield);

    void removeClientForcefield(Class<? extends IForcefield> forcefieldClass);

    void changeOwner(UUID newOwner);
}
