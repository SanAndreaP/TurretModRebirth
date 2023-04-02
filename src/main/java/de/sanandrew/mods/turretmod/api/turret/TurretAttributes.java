/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public final class TurretAttributes
{
    private static final String ATTRIB_NAME = "attribute.name." + TmrConstants.ID;

    public static final Attribute MAX_AMMO_CAPACITY = new RangedAttribute(ATTRIB_NAME + ".max_ammo_capacity", 256.0D, 0.0D, Short.MAX_VALUE).setSyncable(true);
    public static final Attribute MAX_RELOAD_TICKS  = new RangedAttribute(ATTRIB_NAME + ".max_reload_ticks", 20.0D, 0.0D, Double.MAX_VALUE).setSyncable(true);
    public static final Attribute MAX_INIT_SHOOT_TICKS = new RangedAttribute(ATTRIB_NAME + ".max_init_shoot_ticks", 20.0D, 0.0D, Short.MAX_VALUE).setSyncable(true);
}
