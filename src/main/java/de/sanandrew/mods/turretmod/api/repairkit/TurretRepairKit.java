/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.repairkit;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public interface TurretRepairKit
{
    String getName();

    UUID getUUID();

    float getHealAmount();

    boolean isApplicable(ITurretInst turret);

    ResourceLocation getModel();

    default void onHeal(ITurretInst turret) { }
}
