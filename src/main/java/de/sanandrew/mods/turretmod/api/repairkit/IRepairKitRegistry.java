/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.repairkit;

import de.sanandrew.mods.turretmod.api.IRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;

public interface IRepairKitRegistry
        extends IRegistry<IRepairKit>
{
    void registerItems(DeferredRegister<Item> register, String modId);
}
