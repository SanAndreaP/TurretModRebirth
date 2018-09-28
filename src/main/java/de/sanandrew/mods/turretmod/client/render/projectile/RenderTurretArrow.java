/* ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * ***************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderTurretArrow<T extends Entity>
        extends RenderCrossTextureProjectile<T>
{
    private static final ResourceLocation ARROW_TEXTURES = new ResourceLocation("textures/entity/arrow.png");

    @Override
    public ResourceLocation getRenderTexture(T entity) {
        return ARROW_TEXTURES;
    }

    @Override
    protected int getTextureIndex() {
        return 1;
    }
}
