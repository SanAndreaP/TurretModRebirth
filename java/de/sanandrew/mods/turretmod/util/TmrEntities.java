/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import cpw.mods.fml.common.registry.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;

public final class TmrEntities
{
    public static void registerEntities() {
        int entityId = 0;
        EntityRegistry.registerModEntity(EntityTurretCrossbow.class, "turretCrossbow", entityId++, TurretMod.instance, 80, 1, true);
    }
}
