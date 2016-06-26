package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

@SuppressWarnings("unused")
public interface TurretUpgrade
{
    String getName();

    String getModId();

    ResourceLocation getModel();

    TurretUpgrade getDependantOn();

    UUID getRecipeId();

    boolean isTurretApplicable(Class<? extends EntityTurret> turretCls);

    /**
     * Called when this upgrade gets applied to the turret (through interaction from the player or other means).
     * This is called client and server side!
     * Note for client side: This gets also called when the upgrades are loaded from NBT!
     * @param turret The turret this upgrade gets applied
     */
    void onApply(EntityTurret turret);

    /**
     * Called when the upgrade gets loaded from the turrets NBTCompound.
     * This is called server side!
     * @param turret The turret which loads this upgrade
     * @param nbt The NBT the turret saves
     */
    void onLoad(EntityTurret turret, NBTTagCompound nbt);

    /**
     * Called when the upgrade gets loaded from the turrets NBTCompound.
     * This is called server side!
     * @param turret The turret which loads this upgrade
     */
    void onSave(EntityTurret turret, NBTTagCompound nbt);

    /**
     * Called when this upgrade gets removed from the turret.
     * This is called client and server side!
     * @param turret The turret which loads this upgrade
     */
    void onRemove(EntityTurret turret);
}
