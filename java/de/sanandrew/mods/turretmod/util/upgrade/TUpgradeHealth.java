/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.entity.SharedMonsterAttributes;

public class TUpgradeHealth
        extends TurretUpgrade
{
    public TUpgradeHealth(String upgName, String texture) {
        super(TurretMod.MOD_ID, upgName, texture);
    }

    @Override
    public void onApply(AEntityTurretBase turret) {
        turret.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(turret.getMaxHealth() * 1.5D);
        turret.setHealth(turret.getHealth() * 1.5F);
    }

    @Override
    public void onRemove(AEntityTurretBase turret) {
        turret.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(turret.getMaxHealth() / 1.5D);
        turret.setHealth(turret.getHealth() / 1.5F);
    }
}
