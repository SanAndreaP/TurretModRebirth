/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistry;

/**
 * A registry specialized to handling objects of the type {@link IProjectile}
 *
 * @see de.sanandrew.mods.turretmod.api.ITmrPlugin#registerProjectiles(IProjectileRegistry) ITmrPlugin.registerProjectiles(IProjectileRegistry)
 */
@SuppressWarnings("unused")
public interface IProjectileRegistry
        extends IRegistry<IProjectile>
{
}
