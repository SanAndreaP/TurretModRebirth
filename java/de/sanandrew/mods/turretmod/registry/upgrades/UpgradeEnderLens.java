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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.entity.turret.TargetingListener;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class UpgradeEnderLens
        implements TurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "upgrades/ender_lens");

    private final String name;

    public UpgradeEnderLens() {
        this.name = "ender_lens";
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
        return EntityTurretLaser.class.isAssignableFrom(turretCls);
    }

    @Override
    public void onApply(EntityTurret turret) { }

    @Override
    public void onRemove(EntityTurret turret) { }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.UPG_ENDER_LENS;
    }

    @Override
    public void onLoad(EntityTurret turret, NBTTagCompound nbt) { }
}
