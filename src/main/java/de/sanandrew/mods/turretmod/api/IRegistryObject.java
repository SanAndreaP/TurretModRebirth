/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * <p>A registry object.</p>
 * <p>This is a common interface and should be extended by a more specific interface denoting the type.</p>
 */
public interface IRegistryObject
{
    /**
     * @return A unique ID for this object.
     */
    @Nonnull
    ResourceLocation getId();

    /**
     * <p>Indicates wether this object is considered valid or not.</p>
     * <p>A {@link IRegistry#getDefault() default object} returns <tt>false</tt>.</p>
     *
     * @return <tt>true</tt>, if this type is valid and usable; <tt>false</tt> otherwise.
     */
    default boolean isValid() {
        return true;
    }
}
