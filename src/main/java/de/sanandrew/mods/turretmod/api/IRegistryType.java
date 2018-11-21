package de.sanandrew.mods.turretmod.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface IRegistryType
{
    /**
     * <p>Returns the ID for this type. It needs to be unique from all other types registered.</p>
     * <p>Cannot be <tt>null</tt>!</p>
     *
     * @return A unique ID for this item
     */
    @Nonnull
    ResourceLocation getId();

    /**
     * Returns wether or not this type is considered valid.
     * @return <tt>true</tt>, if this type is valid and usable, <tt>false</tt> otherwise.
     */
    default boolean isValid() {
        return true;
    }
}
