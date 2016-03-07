/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import de.sanandrew.mods.turretmod.client.model.ModelTurretCrossbow;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderTurretArrow;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.CommonProxy;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCrossbow.class, new RenderTurret(new ModelTurretCrossbow(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretProjectile.class, new RenderTurretArrow());
    }
}
