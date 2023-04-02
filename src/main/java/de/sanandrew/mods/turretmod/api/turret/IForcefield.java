/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.util.math.AxisAlignedBB;

public interface IForcefield
{
    /**
     * Checks whether the shield is currently active
     * @return true, if the shield is active, false otherwise
     */
    boolean isShieldActive();

    /**
     * Returns an AxisAlignedBoundingBox representing the shield's box.<br>
     * The AABB coordinates are relative to the center of the box, whose coords are the entity's position.<br>
     * An example for an 8 x 8 x 8 box would be:<br>
     * &nbsp;&nbsp;{@code AxisAlingedBB(-4.0F, -4.0F, -4.0F, 4.0F, 4.0F, 4.0F)}<br>
     * If the entity stands on X:3, Y:64, Z:206, the absolute coordinates for the AABB would be:<br>
     * &nbsp;&nbsp;{@code AxisAlingedBB(-1.0F, 60.0F, 202.0F, 7.0F, 68.0F, 210.0F)}
     * @return the AxisAlignedBB representing the shield
     */
    AxisAlignedBB getShieldBoundingBox();

    /**
     * Returns the color of the shield. This includes the Alpha value.
     * @return the color of the shield.
     */
    int getShieldColor();

    /**
     * Returns wether or not to cull the faces of the shield.
     * @return <tt>true</tt>, if the faces should be culled, <tt>false</tt> otherwise
     */
    boolean cullShieldFaces();

    default boolean hasSmoothFadeOut() { return true; }

    default boolean renderFull() { return false; }
}
