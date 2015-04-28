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
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class TUpgradeHealth
        extends TurretUpgrade
{
    private final AttributeModifier healthModifier;

    public TUpgradeHealth(String upgName, String texture, UUID attribUUID, int level) {
        this(upgName, texture, attribUUID, level, null);
    }

    public TUpgradeHealth(String upgName, String texture, UUID attribUUID, int level, TurretUpgrade dependsOn) {
        super(TurretMod.MOD_ID, upgName, texture, dependsOn);
        this.healthModifier = new AttributeModifier(attribUUID, String.format("healthUpg_%d", level), 0.25D, 1 /*ADD_PERC_BASE_VALUE*/);
    }

    @Override
    public void onApply(AEntityTurretBase turret) {
        if( !turret.worldObj.isRemote ) {
            turret.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(healthModifier);
            turret.setHealth(turret.getHealth() * (1.0F + (float) healthModifier.getAmount()));
        }
    }

    @Override
    public void onRemove(AEntityTurretBase turret) {
        turret.getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(healthModifier);
        turret.setHealth(turret.getHealth() / (1.0F + (float) healthModifier.getAmount()));
    }
}
