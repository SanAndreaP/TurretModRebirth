package de.sanandrew.mods.turretmod.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * <p>A common interface used by objects registered via the Turret Mod API.</p>
 */
public interface IRegistryObject
{
    /**
     * @return A unique ID for this object, This cannot be <tt>null</tt>.
     */
    @Nonnull
    ResourceLocation getId();

    /**
     * <p>Indicates wether this object is considered valid or not. A {@link IRegistry#getDefaultObject() default object} should return <tt>false</tt>.</p>
     *
     * @return <tt>true</tt>, if this type is valid and usable, <tt>false</tt> otherwise.
     */
    default boolean isValid() {
        return true;
    }
}
