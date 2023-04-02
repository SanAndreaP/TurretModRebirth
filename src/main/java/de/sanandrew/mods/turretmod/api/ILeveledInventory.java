/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface ILeveledInventory
        extends IInventory
{
    World getLevel();
}
