/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.entity;

import de.sanandrew.mods.turretmod.client.util.Textures;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;

public class RenderBullet
        extends RenderTurretArrow
{
    @Override
    protected ResourceLocation getEntityTexture(EntityArrow arrowEntity) {
        return Textures.PROJECTILE_BULLET.getResource();
    }
}
