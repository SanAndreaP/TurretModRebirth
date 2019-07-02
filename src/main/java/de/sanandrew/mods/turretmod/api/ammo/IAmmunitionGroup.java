package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * <p>An object defining an ammunition group.</p>
 * <p>This is used as an informational object for the Turret Lexicon and also defines which turret can accept the ammo in this group.</p>
 */
public interface IAmmunitionGroup
        extends IRegistryObject
{
    /**
     * @return the stack to be used as icon for this group, cannot be <tt>null</tt>
     */
    @Nonnull
    ItemStack getIcon();

    /**
     * @return the turret delegate that can use the ammo contained in this group, cannot be <tt>null</tt>
     */
    @Nonnull
    ITurret getTurret();
}
