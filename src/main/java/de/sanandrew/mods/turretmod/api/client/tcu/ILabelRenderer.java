/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.client.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.IRegistryObject;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.renderer.WorldRenderer;

public interface ILabelRenderer
        extends IRegistryObject
{
    boolean isVisible(ITurretEntity turret);

    int getMinWidth(ILabelRegistry registry, ITurretEntity turret);

    int getHeight(ILabelRegistry registry, ITurretEntity turret);

    default int getSortOrder() {
        return 0;
    }

    default void render(ILabelRegistry registry, ITurretEntity turret, WorldRenderer context, MatrixStack mat, float totalWidth, float totalHeight, float partialTicks, float opacity) { }
}
