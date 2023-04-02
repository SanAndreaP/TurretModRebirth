/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.upgrade;

import dev.sanandrea.mods.turretmod.api.IRegistryObject;
import dev.sanandrea.mods.turretmod.api.turret.ITurret;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;

/**
 * <p>A registry object defining a turret upgrade.</p>
 *
 * @see IUpgradeRegistry
 */
public interface IUpgrade
        extends IRegistryObject
{
    /**
     * <p>Returns the upgrade object this depends on.</p>
     * <p>If this upgrade has no dependencies, this will return <tt>null</tt>.</p>
     *
     * @return the upgrade object as the dependency or <tt>null</tt>.
     */
    default IUpgrade getDependantOn() { return null; }

    /**
     * <p>Returns an array of whitelisted turrets that can use this upgrade.</p>
     * <p>If either <tt>null</tt> or an empty array is returned, this upgrade can be applied to any turret.</p>
     *
     * @return an array with whitelisted turrets or <tt>null</tt>.
     */
    @Nullable
    default ITurret[] getApplicableTurrets() {
        return null;
    }

    /**
     * <p>Returns a range (minimum and maximum; inclusive) of applicable turret tiers.</p>
     * <p>If this upgrade can be applied to one tier, this returns {@link Range#is(Comparable) Range.is(tier)}.</p>
     * <p>If this upgrade can be applied to a tier range, this returns {@link Range#between(Comparable, Comparable) Range.between(minTier, maxTier)}.</p>
     * <p>If this upgrade can be applied to any turret tier, this returns <tt>null</tt>.</p>
     *
     * @return the range of applicable turret tiers or <tt>null</tt>.
     * @see ITurret#getTier()
     */
    @Nullable
    default Range<Integer> getTierRange() {
        return null;
    }

    /**
     * <p>Initializes this upgrade; invoked upon application to or loading from the specified turret instance.</p>
     * <p>Some upgrades apply entity attributes to this turret or the like.</p>
     * <p><b>This should be invoked on the client and server side.</b></p>
     *
     * @param turretInst The turret instance this upgrade gets applied to.
     * @param stack
     */
    default void initialize(ITurretEntity turretInst, ItemStack stack) { }

    /**
     * <p>Reads additional data for this upgrade from the provided NBT tag.</p>
     * <p><b>This should be invoked on the server side.</b></p>
     *
     * @param turretInst The turret instance which holds this upgrade.
     * @param nbt The NBT tag of the upgrade item.
     */
    @Deprecated
    default void onLoad(ITurretEntity turretInst, CompoundNBT nbt) { }

    /**
     * <p>Saves additional data for this upgrade to the provided NBT tag.</p>
     * <p><b>This should be invoked on the server side.</b></p>
     *
     * @param turretInst The turret instance which holds this upgrade.
     * @param nbt The NBT tag of the upgrade item.
     */
    @Deprecated
    default void onSave(ITurretEntity turretInst, CompoundNBT nbt) { }

    default IUpgradeData<?> getData(ITurretEntity turretInst) {
        return null;
    }

    /**
     * <p>Terminates this upgrade upon removal from the specified turret instance.</p>
     * <p>Some upgrades remove entity attributes from this turret or the like.</p>
     * <p><b>This should be invoked on the client and server side.</b></p>
     *
     * @param turretInst The turret instance this upgrade gets removed from.
     * @param stack
     */
    default void terminate(ITurretEntity turretInst, ItemStack stack) { }

    default boolean isCompatibleWithCreativeUpgrade() {
        return false;
    }
}
