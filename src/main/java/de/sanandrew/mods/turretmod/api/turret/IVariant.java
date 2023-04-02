/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.util.ResourceLocation;

public interface IVariant
{
    <T> T getId();

    ResourceLocation getTexture();

    String getTranslatedName();
}
