/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.client.model.turret.techi.ModelTurretCrossbow;
import de.sanandrew.mods.turretmod.client.model.turret.techii.ModelTurretRevolver;
import de.sanandrew.mods.turretmod.client.render.entity.RenderBullet;
import de.sanandrew.mods.turretmod.client.render.entity.RenderTurret;
import de.sanandrew.mods.turretmod.client.render.entity.RenderTurretArrow;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileArrow;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretOP;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.techii.EntityTurretRevolver;

public final class TmrEntities
{
    public static void registerEntities() {
        int entityId = 0;
        EntityRegistry.registerModEntity(EntityTurretCrossbow.class, "turretCrossbow", entityId++, TurretMod.instance, 80, 1, true);
        EntityRegistry.registerModEntity(EntityTurretRevolver.class, "turretRevolver", entityId++, TurretMod.instance, 80, 1, true);
        EntityRegistry.registerModEntity(EntityTurretOP.class, "turretOp", entityId++, TurretMod.instance, 80, 1, true);

        EntityRegistry.registerModEntity(EntityProjectileArrow.class, "turretProjArrow", entityId++, TurretMod.instance, 64, 20, true);
        EntityRegistry.registerModEntity(EntityProjectileBullet.class, "turretProjBullet", entityId++, TurretMod.instance, 64, 20, true);
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretCrossbow.class, new RenderTurret(new ModelTurretCrossbow(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretRevolver.class, new RenderTurret(new ModelTurretRevolver(0.0F)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTurretOP.class, new RenderTurret(new ModelTurretCrossbow(0.0F)));

        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileArrow.class, new RenderTurretArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntityProjectileBullet.class, new RenderBullet());
    }
}
