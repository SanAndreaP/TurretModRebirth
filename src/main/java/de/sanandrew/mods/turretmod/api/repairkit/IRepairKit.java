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

import javax.annotation.Nonnull;
import java.util.UUID;

public interface IRepairKit
{
    @Nonnull
    ResourceLocation getId();

    float getHealAmount();

    boolean isApplicable(ITurretInst turret);

    default void onHeal(ITurretInst turret) { }

    default boolean isValid() {
        return true;
    }
}
