package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;

public interface IUpgrade
{
    ResourceLocation getId();

    default IUpgrade getDependantOn() { return null; }

    /**
     * <p>This returns an array of whitelisted turrets that can use this upgrade.</p>
     * <p>If either {@code null} or an empty array is returned, this upgrade can be applied to any turret.</p>
     *
     * @return The array with whitelisted turrets or {@code null}
     */
    @Nullable
    default ITurret[] getApplicableTurrets() {
        return null;
    }

    /**
     * <p>This returns a range (minimum and maximum). The turrets' tier has to be within that range (both inclusive) in order for this upgrade to be applicable.</p>
     * <p>If {@code null} is returned, this upgrade can be applied to any turret tier.</p>
     *
     * @return The range or {@code null}
     */
    @Nullable
    default Range<Integer> getTierRange() {
        return null;
    }

    /**
     * Called when this upgrade gets applied to the turret (through interaction from the player or other means).
     * This is called client and server side!
     * Note for client side: This gets also called when the upgrades are loaded from NBT!
     * @param turretInst The turret this upgrade gets applied
     */
    default void onApply(ITurretInst turretInst) { }

    /**
     * Called when the upgrade gets loaded from the turrets NBTCompound.
     * This is called server side!
     * @param turretInst The turret which loads this upgrade
     * @param nbt The NBT the turret saves
     */
    default void onLoad(ITurretInst turretInst, NBTTagCompound nbt) { }

    /**
     * Called when the upgrade gets loaded from the turrets NBTCompound.
     * This is called server side!
     * @param turretInst The turret which loads this upgrade
     */
    default void onSave(ITurretInst turretInst, NBTTagCompound nbt) { }

    /**
     * Called when this upgrade gets removed from the turret.
     * This is called client and server side!
     * @param turretInst The turret which loads this upgrade
     */
    default void onRemove(ITurretInst turretInst) { }

    /**
     * Returns wether or not this upgrade is considered valid.
     * @return <tt>true</tt>, if this upgrade is valid and usable, <tt>false</tt> otherwise.
     */
    default boolean isValid() {
        return true;
    }
}
