/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api;

import net.minecraft.util.AxisAlignedBB;

public interface ShieldedTurret
{
    /**
     * Checks whether the shield is currently active
     * @return true, if the shield is active, false otherwise
     */
    boolean hasShieldActive();

    /**
     * Returns an AxisAlignedBoundingBox representing the shield's box.<br>
     * The AABB coordinates are relative to the center of the box, whose coords are the entity's position.<br>
     * An example for an 8 x 8 x 8 box would be:<br>
     * &nbsp;&nbsp;{@code AxisAlingedBB(-4.0F, -4.0F, -4.0F, 4.0F, 4.0F, 4.0F)}<br>
     * If the entity stands on X:3, Y:64, Z:206, the absolute coordinates for the AABB would be:<br>
     * &nbsp;&nbsp;{@code AxisAlingedBB(-1.0F, 60.0F, 202.0F, 7.0F, 68.0F, 210.0F)}<br>
     * @return the AxisAlignedBB representing the shield
     */
    AxisAlignedBB getShieldBoundingBox();

    /**
     * Returns the color of the shield. This includes the Alpha value.<br>
     * The format of the Integer returned as hex value is as follows:<br>
     * {@code 0xAARRGGBB}, where {@code AA} = Alpha, {@code RR} = red, {@code GG} = green, {@code BB} = blue
     * @return the color of the shield.
     */
    int getShieldColor();
}
