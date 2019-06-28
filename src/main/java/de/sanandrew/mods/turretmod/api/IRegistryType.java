package de.sanandrew.mods.turretmod.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * <p>A common interface used by types registered via the Turret Mod API.</p>
 */
public interface IRegistryType
{
    /**
     * @return A unique ID for this type, cannot be <tt>null</tt>
     */
    @Nonnull
    ResourceLocation getId();

    /**
     * @return <tt>true</tt>, if this type is valid and usable, <tt>false</tt> otherwise.
     */
    default boolean isValid() {
        return true;
    }
}
