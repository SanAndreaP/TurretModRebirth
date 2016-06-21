package de.sanandrew.mods.turretmod.registry.medpack;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public interface TurretRepairKit
{
    String getName();
    UUID getUUID();
    float getHealAmount();
    void onHeal(EntityTurret turret);
    boolean isApplicable(EntityTurret turret);
    ResourceLocation getModel();
}
