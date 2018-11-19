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
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class RepairKitRegeneration
        implements IRepairKit
{
    private final ResourceLocation id;
    private final float heal;
    private final int regenLvl;
    private final int regenTime;

    RepairKitRegeneration(String suffix, float heal, int level, int time) {
        this.id = new ResourceLocation(TmrConstants.ID, "repkit.regen." + suffix);
        this.heal = heal;
        this.regenLvl = level;
        this.regenTime = time;
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
    public final void onHeal(ITurretInst turret) {
        turret.get().addPotionEffect(new PotionEffect(MobEffects.REGENERATION, this.regenTime, this.regenLvl, true, false));
    }

    @Override
    public boolean isApplicable(ITurretInst turret) {
        return turret.get().getHealth() <= turret.get().getMaxHealth() - this.heal && turret.get().getActivePotionEffect(MobEffects.REGENERATION) == null;
    }
}
