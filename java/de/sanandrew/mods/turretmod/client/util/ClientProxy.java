/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import cpw.mods.fml.client.registry.RenderingRegistry;
import de.sanandrew.mods.turretmod.client.model.turret.techi.ModelTurretCrossbow;
import de.sanandrew.mods.turretmod.client.render.entity.RenderTurret;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.CommonProxy;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void init() {
        super.init();

        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCrossbow.class, new RenderTurret(new ModelTurretCrossbow(0.0F)));
    }
}
