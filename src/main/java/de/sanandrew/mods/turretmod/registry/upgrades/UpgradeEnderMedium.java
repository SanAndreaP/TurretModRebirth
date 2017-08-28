/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class UpgradeEnderMedium
        implements ITurretUpgrade
{
    private static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "upgrades/ender_medium");

    private final String name;

    public UpgradeEnderMedium() {
        this.name = "ender_medium";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ResourceLocation getModel() {
        return ITEM_MODEL;
    }

    @Override
    public boolean isTurretApplicable(Class<? extends EntityTurret> turretCls) {
        return EntityTurretLaser.class.isAssignableFrom(turretCls);
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.UPG_ENDER_MEDIUM;
    }
}
