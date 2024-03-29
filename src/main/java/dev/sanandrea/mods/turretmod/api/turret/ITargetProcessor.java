/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.turret;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ITargetProcessor
{
    boolean addAmmo(@Nonnull ItemStack stack);

    boolean addAmmo(@Nonnull ItemStack stack, ICapabilityProvider excessInv);

    int getAmmoCount();

    @Nonnull
    ItemStack getAmmoStack();

    boolean hasAmmo();

    void dropExcessAmmo();

    void decrAmmo();

    void putAmmoInInventory(ICapabilityProvider inventory);

    boolean isAmmoApplicable(@Nonnull ItemStack stack);

    ApplyType getAmmoApplyType(@Nonnull ItemStack stack);

    int getMaxAmmoCapacity();

    int getMaxShootTicks();

    boolean isShooting();

    boolean canShoot();

    void setShot(boolean success);

    void decrInitShootTicks();

    void resetInitShootTicks();

    Entity getProjectile();

    double getRangeVal();

    AxisAlignedBB getAdjustedRange(boolean doOffset);

    boolean shootProjectile();

    void playSound(SoundEvent sound, float volume);

    boolean isEntityValidTarget(Entity entity);

    List<Entity> getValidTargetList();

    boolean isEntityTargeted(Entity entity);

    boolean isPlayerTargeted(UUID id);

    boolean isEntityTargeted(ResourceLocation id);

    ITurretEntity getTurret();

    boolean hasTarget();

    Entity getTarget();

    void save(CompoundNBT nbt);

    void load(CompoundNBT nbt);

    ResourceLocation[] getEnabledEntityTargets();

    UUID[] getEnabledPlayerTargets();

    Map<ResourceLocation, Boolean> getEntityTargets();

    Map<UUID, Boolean> getPlayerTargets();

    void updateEntityTarget(ResourceLocation res, boolean active, boolean forClient);

    void updateEntityTargets(EntityClassification res, boolean active, boolean forClient);

    void updatePlayerTarget(UUID uid, boolean active, boolean forClient);

    void updateAllEntityTargets(boolean active, boolean forClient);

    void updateAllPlayerTargets(boolean active, boolean forClient);

    void syncTargets();

    ITextComponent getTargetName();

    void onTick();

    void onTickClient();

    boolean isEntityDenyList();

    boolean isPlayerDenyList();

    void setEntityDenyList(boolean isDenyList);

    void setPlayerDenyList(boolean isDenyList);

    enum ApplyType {
        ADD,
        REPLACE,
        NOT_COMPATIBLE
    }
}
