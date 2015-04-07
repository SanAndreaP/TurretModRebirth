/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.entity;

import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;

public class RenderBullet
        extends RenderArrow
{
    @Override
    protected ResourceLocation getEntityTexture(EntityArrow arrowEntity) {
        return EnumTextures.PROJECTILE_BULLET.getResource();
    }
}
