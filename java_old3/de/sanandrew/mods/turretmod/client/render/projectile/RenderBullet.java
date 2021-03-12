/* ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * ***************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBullet<T extends Entity>
        extends RenderCrossTextureProjectile<T>
{
    @Override
    public ResourceLocation getRenderTexture(T entity) {
        return Resources.PROJECTILE_BULLET.resource;
    }
}
