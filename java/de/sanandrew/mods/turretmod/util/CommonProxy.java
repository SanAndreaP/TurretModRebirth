/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityTurretCrossbow.class, "turret_mkI_crossbow", 0, TurretModRebirth.instance, 128, 1, false);
        EntityRegistry.registerModEntity(EntityProjectileCrossbowBolt.class, "turret_proj_arrow", 1, TurretModRebirth.instance, 128, 1, true);
    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}
