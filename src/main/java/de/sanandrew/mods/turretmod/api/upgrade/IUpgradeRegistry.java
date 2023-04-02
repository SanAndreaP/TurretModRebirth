/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.upgrade;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nonnull;

/**
 * A registry specialized to handling objects of the type {@link IUpgrade}
 *
 * @see de.sanandrew.mods.turretmod.api.ITmrPlugin#registerUpgrades(IUpgradeRegistry) ITmrPlugin.registerUpgrades(IUpgradeRegistry)
 */
@SuppressWarnings("unused")
public interface IUpgradeRegistry
        extends IRegistry<IUpgrade>
{
    /**
     * <p>Indicates wether the ItemStack has the specified upgrade ID.</p>
     *
     * @param stack The ItemStack that should be checked.
     * @param id The upgrade ID that should be searched for.
     * @return <tt>true</tt>, if the ItemStack has the upgrade ID; <tt>false</tt> otherwise.
     */
    boolean isType(@Nonnull ItemStack stack, ResourceLocation id);

    /**
     * <p>Indicates wether the ItemStack has the specified upgrade object.</p>
     *
     * @param stack The ItemStack that should be checked.
     * @param obj The upgrade object that should be searched for.
     * @return <tt>true</tt>, if the ItemStack has the upgrade object; <tt>false</tt> otherwise.
     */
    boolean isType(@Nonnull ItemStack stack, IUpgrade obj);

//    /**
//     * <p>Synchronizes the upgrade with the specified ID from the given turret instance with the server.</p>
//     *
//     * @param turretInst The turret instance whose upgrade should by synchronized.
//     * @param id The ID of the upgrade that should be synchronized.
//     */
//    void syncWithServer(ITurretEntity turretInst, ResourceLocation id);
//
//    /**
//     * <p>Synchronizes the upgrade with the specified ID from the given turret instance with all nearby clients.</p>
//     *
//     * @param turretInst The turret instance whose upgrade should by synchronized.
//     * @param id The ID of the upgrade that should be synchronized.
//     */
//    void syncWithClients(ITurretEntity turretInst, ResourceLocation id);

    /**
     * <p>Returns the upgrade object representing an empty upgrade.</p>
     * <p>This is NOT to be confused with {@link IRegistry#getDefault()}, as that returns an unregistered, invalid upgrade object!</p>
     *
     * @return the upgrade object of an empty upgrade.
     */
    IUpgrade getEmptyUpgrade();

    boolean isApplicable(IUpgrade upgrade, ITurret turret);

    boolean isApplicable(IUpgrade upgrade, ITurret turret, boolean isSpecialized);

    void registerItems(DeferredRegister<Item> register, String modId);
}
