/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.ammo;

import dev.sanandrea.mods.turretmod.api.IRegistry;
import dev.sanandrea.mods.turretmod.api.ITmrPlugin;

/**
 * A registry specialized to handling objects of the type {@link IProjectile}
 *
 * @see ITmrPlugin#registerProjectiles(IProjectileRegistry) ITmrPlugin.registerProjectiles(IProjectileRegistry)
 */
@SuppressWarnings("unused")
public interface IProjectileRegistry
        extends IRegistry<IProjectile>
{
}
