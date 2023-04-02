package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistry;
import de.sanandrew.mods.turretmod.api.client.render.IRenderRegistry;

/**
 * A registry specialized to handling objects of the type {@link IProjectile}
 *
 * @see de.sanandrew.mods.turretmod.api.ITmrPlugin#registerProjectiles(IProjectileRegistry) ITmrPlugin.registerProjectiles(IProjectileRegistry)
 * @see de.sanandrew.mods.turretmod.api.ITmrPlugin#registerProjectileRenderer(IRenderRegistry) ITmrPlugin.registerProjectileRenderer(IProjectileRegistry)
 */
@SuppressWarnings("unused")
public interface IProjectileRegistry
        extends IRegistry<IProjectile>
{
}
