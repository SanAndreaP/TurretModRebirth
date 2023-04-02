/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.client.util.TurretMod;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public final class TurretAttributes
{
    public static final IAttribute MAX_AMMO_CAPACITY = new RangedAttribute(TurretMod.MOD_ID + ".maxAmmoCapacity", 256.0D, 0.0D, 576.0D).setShouldWatch(true);
    public static final IAttribute MAX_RELOAD_TICKS = new RangedAttribute(TurretMod.MOD_ID + ".maxReloadTicks", 20.0D, 0.0D, Double.MAX_VALUE).setShouldWatch(true);
    public static final IAttribute MAX_UPGRADE_SLOTS = new RangedAttribute(TurretMod.MOD_ID + ".maxUpgradeSlots", 9.0D, 0.0D, 36.0F).setShouldWatch(true);
}
