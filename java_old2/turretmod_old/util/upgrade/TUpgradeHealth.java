/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util.upgrade;

import de.sanandrew.core.manpack.util.EnumAttrModifierOperation;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public abstract class TUpgradeHealth
        extends TurretUpgradeBase
{
    private final AttributeModifier modifier;
    private final int upgLevel;

    private TUpgradeHealth(String upgName, String texture, UUID attribUUID, int level) {
        this(upgName, texture, attribUUID, level, null);
    }

    private TUpgradeHealth(String upgName, String texture, UUID attribUUID, int level, TurretUpgrade dependsOn) {
        super(upgName, texture, dependsOn);
        this.modifier = new AttributeModifier(attribUUID, String.format("healthUpg_%d", level), 0.25D, EnumAttrModifierOperation.ADD_PERC_VAL_TO_SUM.ordinal());
        this.upgLevel = level;
    }

    @Override
    public final void onApply(Turret turret) {
        if( !turret.getEntity().worldObj.isRemote ) {
            turret.getEntity().getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(modifier);
            turret.getEntity().setHealth(this.incrHealth(turret.getEntity()));
        }
    }

    @Override
    public final void onRemove(Turret turret) {
        turret.getEntity().setHealth(this.decrHealth(turret.getEntity()));
        turret.getEntity().getEntityAttribute(SharedMonsterAttributes.maxHealth).removeModifier(modifier);
    }

    private float incrHealth(EntityLiving living) {
        float modifierRemover = 1.0F + (float) modifier.getAmount() * (this.upgLevel - 1);
        float modifierAdd = 1.0F + (float) modifier.getAmount() * this.upgLevel;
        return (living.getHealth() / modifierRemover) * modifierAdd;
    }

    private float decrHealth(EntityLiving living) {
        float modifierRemover = 1.0F + (float) modifier.getAmount() * (this.upgLevel - 1);
        float modifierAdd = 1.0F + (float) modifier.getAmount() * this.upgLevel;
        return (living.getHealth() / modifierAdd) * modifierRemover;
    }

    public static class TUpgradeHealthI
            extends TUpgradeHealth
    {
        public TUpgradeHealthI() {
            super("healthUpgradeI", "upgrades/health_i", UUID.fromString("84BF0C8F-A5E8-429F-A7ED-DEE503CA4505"), 1);
        }
    }

    public static class TUpgradeHealthII
            extends TUpgradeHealth
    {
        public TUpgradeHealthII(TurretUpgrade dependant) {
            super("healthUpgradeII", "upgrades/health_ii", UUID.fromString("704FA08B-F49A-4A69-86E9-FFAD639868C9"), 2, dependant);
        }
    }

    public static class TUpgradeHealthIII
            extends TUpgradeHealth
    {
        public TUpgradeHealthIII(TurretUpgrade dependant) {
            super("healthUpgradeIII", "upgrades/health_iii", UUID.fromString("E1C7BBB8-ACE8-413D-B7EA-D95C7FF5285F"), 3, dependant);
        }
    }

    public static class TUpgradeHealthIV
            extends TUpgradeHealth
    {
        public TUpgradeHealthIV(TurretUpgrade dependant) {
            super("healthUpgradeIV", "upgrades/health_iv", UUID.fromString("E4D37B7F-81DD-439E-B359-539E76F01B71"), 4, dependant);
        }
    }
}
