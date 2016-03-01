/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.core.manpack.util.EnumAttrModifierOperation;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public abstract class TUpgradeReloadTime
        extends TurretUpgradeBase
{
    protected AttributeModifier modifier;

    protected TUpgradeReloadTime(String upgName, String texture, TurretUpgrade dependUpgrade) {
        super(upgName, texture, dependUpgrade);
    }

    @Override
    public void onApply(Turret turret) {
        if( !turret.getEntity().worldObj.isRemote ) {
            turret.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).applyModifier(modifier);
        }
    }

    @Override
    public void onRemove(Turret turret) {
        turret.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).removeModifier(modifier);
    }

    public static class TUpgradeReloadTimeI
            extends TUpgradeReloadTime
    {
        public TUpgradeReloadTimeI() {
            super("reloadTimeI", "upgrades/reload_i", null);
            this.modifier = new AttributeModifier(UUID.fromString("44CDF38C-1562-4CBE-8C47-893FCDFDE175"), "reloadTime_1", -0.15D,
                                                          EnumAttrModifierOperation.ADD_PERC_VAL_TO_SUM.ordinal());
        }
    }

    public static class TUpgradeReloadTimeII
            extends TUpgradeReloadTime
    {
        public TUpgradeReloadTimeII(TurretUpgrade depInst) {
            super("reloadTimeII", "upgrades/reload_ii", depInst);
            this.modifier = new AttributeModifier(UUID.fromString("B561CE77-79A9-4EBD-8EAE-64A263D522CE"), "reloadTime_2", -0.35D,
                                                          EnumAttrModifierOperation.ADD_PERC_VAL_TO_SUM.ordinal());
        }
    }
}
