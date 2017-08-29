/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ITurret
{
    String getName();

    @Nonnull
    UUID getId();

    float getInfoHealth();

    int getInfoBaseAmmoCapacity();

    String getInfoRange();

    ResourceLocation getItemModel();

    UUID getRecipeId();

    default void entityInit(ITurretInst turretInst) { }

    ResourceLocation getStandardTexture(ITurretInst turretInst);

    ResourceLocation getGlowTexture(ITurretInst turretInst);

    SoundEvent getShootSound(ITurretInst turretInst);

    AxisAlignedBB getRangeBB(ITurretInst turretInst);

    default void onUpdate(ITurretInst turretInst) { }

    default void applyEntityAttributes(ITurretInst turretInst) { }

    default SoundEvent getHurtSound(ITurretInst turretInst) {
        return null;
    }

    default SoundEvent getDeathSound(ITurretInst turretInst) {
        return null;
    }

    default SoundEvent getNoAmmoSound(ITurretInst turretInst) {
        return null;
    }

}
