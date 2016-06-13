/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetingListener;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;
import java.util.UUID;

public class UpgradeSmartTargeting
        implements TurretUpgrade
{
    private final TargetingListener targetingListener = new TargetingListener()
    {
        @Override
        public boolean isTargetApplicable(EntityTurret turret, Entity target, boolean currValue) {
            double range = turret.getTargetProcessor().getRange();
            AxisAlignedBB rangeAABB = turret.boundingBox.expand(range, range, range);
            List entities = turret.worldObj.getEntitiesWithinAABB(turret.getClass(), rangeAABB);

            for( Object eObj : entities ) {
                if( eObj instanceof EntityTurret ) {
                    if( eObj != turret && ((EntityTurret) eObj).getTargetProcessor().getTarget() == target ) {
                        return false;
                    }
                }
            }

            return currValue;
        }

        @Override
        public int getPriority() {
            return 0;
        }
    };

    private final String name;

    public UpgradeSmartTargeting() {
        this.name = "smart_tgt";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getModId() {
        return TurretModRebirth.ID;
    }

    @Override
    public String getIconTexture() {
        return TurretModRebirth.ID + ":upgrades/" + this.name;
    }

    @Override
    public TurretUpgrade getDependantOn() {
        return null;
    }

    @Override
    public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
        return true;
    }

    @Override
    public void onApply(EntityTurret turret) {
        if( !turret.worldObj.isRemote ) {
            turret.getTargetProcessor().addTargetingListener(this.targetingListener);
        }
    }

    @Override
    public void onRemove(EntityTurret turret) { }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.UPG_SMART_TGT;
    }

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) {
        if( !turret.worldObj.isRemote ) {
            turret.getTargetProcessor().addTargetingListener(this.targetingListener);
        }
    }

    @Override
    public void onSave(EntityTurret turret, NBTTagCompound nbt) {}
}
