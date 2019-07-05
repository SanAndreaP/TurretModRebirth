package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;

/**
 * <p>A registry object defining a turret upgrade.</p>
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
     * <p>Initializes this upgrade upon application to or loading from the specified turret instance.</p>
     * <p>Some upgrades apply entity attributes to this turret or the like.</p>
     * <p><b>This should be called client and server side!</b></p>
     *
     * @param turretInst The turret instance this upgrade gets applied to.
     */
    default void initialize(ITurretInst turretInst) { }

    /**
     * <p>Reads additional data for this upgrade from the provided NBT tag.</p>
     * <p><b>This should be called server side!</b></p>
     *
     * @param turretInst The turret instance which holds this upgrade.
     * @param nbt The NBT tag of the upgrade item.
     */
    default void onLoad(ITurretInst turretInst, NBTTagCompound nbt) { }

    /**
     * <p>Saves additional data for this upgrade to the provided NBT tag.</p>
     * <p><b>This should be called server side!</b></p>
     *
     * @param turretInst The turret instance which holds this upgrade.
     * @param nbt The NBT tag of the upgrade item.
     */
    default void onSave(ITurretInst turretInst, NBTTagCompound nbt) { }

    /**
     * <p>Terminates this upgrade upon removal from the specified turret instance.</p>
     * <p>Some upgrades remove entity attributes from this turret or the like.</p>
     * <p><b>This should be called client and server side!</b></p>
     *
     * @param turretInst The turret instance this upgrade gets removed from.
     */
    default void terminate(ITurretInst turretInst) { }
}
