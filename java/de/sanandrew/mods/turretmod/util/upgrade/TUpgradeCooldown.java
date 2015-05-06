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
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public abstract class TUpgradeCooldown
        extends TurretUpgrade
{
    protected AttributeModifier cooldownModifier;

    protected TUpgradeCooldown(String modID, String upgName, String texture, TurretUpgrade dependUpgrade) {
        super(modID, upgName, texture, dependUpgrade);
    }

    @Override
    public void onApply(AEntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            turret.getEntityAttribute(TurretAttributes.MAX_COOLDOWN_TICKS).applyModifier(cooldownModifier);
        }
    }

    @Override
    public void onRemove(AEntityTurretBase turret) {
        turret.getEntityAttribute(TurretAttributes.MAX_COOLDOWN_TICKS).removeModifier(cooldownModifier);
    }

    public static class TUpgradeCooldownI
            extends TUpgradeCooldown
    {
        public TUpgradeCooldownI() {
            super(TurretMod.MOD_ID, "cooldownI", "upgrades/cooldown_i", null);
            this.cooldownModifier = new AttributeModifier(UUID.fromString("44CDF38C-1562-4CBE-8C47-893FCDFDE175"), "cooldown_1", -0.15D,
                                                          EnumAttrModifierOperation.ADD_PERC_VAL_TO_SUM.ordinal());
        }
    }

    public static class TUpgradeCooldownII
            extends TUpgradeCooldown
    {
        public TUpgradeCooldownII(TurretUpgrade depInst) {
            super(TurretMod.MOD_ID, "cooldownII", "upgrades/cooldown_ii", depInst);
            this.cooldownModifier = new AttributeModifier(UUID.fromString("B561CE77-79A9-4EBD-8EAE-64A263D522CE"), "cooldown_2", -0.35D,
                                                          EnumAttrModifierOperation.ADD_PERC_VAL_TO_SUM.ordinal());
        }
    }
}
