/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.turret;

import net.minecraft.inventory.IInventory;

public interface IVariantHolder
{
    default IVariant getVariant(ITurretEntity turret, Object id) {
        return getVariant(id);
    }

    default boolean hasVariants() {
        return true;
    }

    IVariant[] getVariants();

    IVariant getVariant(IInventory inv);

    IVariant getVariant(Object id);

    IVariant getVariant(String id);

    void registerVariant(IVariant variant);

    boolean isDefaultVariant(IVariant variant);
}
