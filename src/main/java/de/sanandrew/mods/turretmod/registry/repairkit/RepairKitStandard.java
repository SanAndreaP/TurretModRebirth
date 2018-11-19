/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.repairkit;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.util.ResourceLocation;

public class RepairKitStandard
        implements IRepairKit
{
    private final ResourceLocation id;
    private final float heal;

    RepairKitStandard(String suffix, float heal) {
        this.id = new ResourceLocation(TmrConstants.ID, "repkit.standard." + suffix);
        this.heal = heal;
    }

    @Override
    public final ResourceLocation getId() {
        return this.id;
    }

    @Override
    public final float getHealAmount() {
        return this.heal;
    }

    @Override
    public boolean isApplicable(ITurretInst turret) {
        return turret.get().getHealth() <= turret.get().getMaxHealth() - this.heal;
    }
}
