/*
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
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class UpgradeSmartTargeting
        implements TurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "upgrades/smart_tgt");

    private final TargetingListener targetingListener = new TargetingListener()
    {
        @Override
        public boolean isTargetApplicable(EntityTurret turret, Entity target, boolean currValue) {
            List entities = turret.world.getEntitiesWithinAABB(turret.getClass(), turret.getTargetProcessor().getRangeBB());

            for( Object eObj : entities ) {
                if( eObj instanceof EntityTurret ) {
                    EntityTurret otherTurret = (EntityTurret) eObj;
                    if( eObj != turret && otherTurret.getTargetProcessor().getTarget() == target && otherTurret.getTargetProcessor().hasAmmo() ) {
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
    public ResourceLocation getModel() {
        return ITEM_MODEL;
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
        if( !turret.world.isRemote ) {
            turret.getTargetProcessor().addTargetingListener(this.targetingListener);
        }
    }

    @Override
    public void onRemove(EntityTurret turret) {
        turret.getTargetProcessor().removeTargetingListener(this.targetingListener);
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.UPG_SMART_TGT;
    }

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) {
        if( !turret.world.isRemote ) {
            turret.getTargetProcessor().addTargetingListener(this.targetingListener);
        }
    }
}