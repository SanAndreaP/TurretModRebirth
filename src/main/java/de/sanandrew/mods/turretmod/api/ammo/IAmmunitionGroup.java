package de.sanandrew.mods.turretmod.api.ammo;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * <p>A registry object defining an ammunition group.</p>
 * <p>This is used to group objects together onto one page for the Turret Lexicon and also defines which turret can accept the ammo in this group.</p>
 * <p>Note: although this is a registry object, this does not need to be registered explicitly, as this will be automatically with an {@link IAmmunition}
 *    object, which references this instance via {@link IAmmunition#getGroup()}.</p>
 */
public interface IAmmunitionGroup
        extends IRegistryObject
{
    /**
     * @return the stack to be used as icon for this group, cannot be <tt>null</tt>
     */
    @Nonnull
    @Deprecated
    ItemStack getIcon();

    default ResourceLocation getBookEntryId() {
        return null;
    }

    /**
     * @return the turret delegate that can use the ammo contained in this group, cannot be <tt>null</tt>
     */
    @Nonnull
    ITurret getTurret();
}
