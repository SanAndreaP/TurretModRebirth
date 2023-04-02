/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A registry specialized to handling objects of the type {@link IAmmunition}
 */
public interface IAmmunitionRegistry
        extends IRegistry<IAmmunition>
{
    void registerItems(DeferredRegister<Item> register, String modId);

    /**
     * <p>Returns an unmodifiable view of the objects registered in this registry, compatible with the given turret.</p>
     *
     * @param turret The turret delegate which should be filtered
     * @return an unmodifiable view of registered objects compatible with this turret.
     */
    @Nonnull
    Collection<IAmmunition> getAll(ITurret turret);

    String getSubtype(ItemStack stack);

    ItemStack setSubtype(ItemStack stack, String type);

    @Nonnull
    ItemStack getItem(ResourceLocation id, String subtype, int count);

    @Nonnull
    default ItemStack getItem(ResourceLocation id, String subtype) {
        return this.getItem(id, subtype, 1);
    }

    @Nonnull
    default ItemStack getItem(IAmmunition obj, String subtype) {
        return this.getItem(obj.getId(), subtype, 1);
    }

    @Nonnull
    default ItemStack getItem(IAmmunition obj, String subtype, int count) {
        return this.getItem(obj.getId(), subtype, count);
    }
}
