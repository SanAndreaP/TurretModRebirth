/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderPebble<T extends EntityTurretProjectile>
        extends Render<T>
{
    private ItemStack gravelItem;

    public RenderPebble(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float yaw, float partTicks) {
        if( this.gravelItem == null ) {
            this.gravelItem = new ItemStack(Blocks.GRAVEL, 1);
        }

        TmrClientUtils.renderStackInWorld(this.gravelItem, x, y, z, 0.0F, 0.0F, 0.0F, 0.1D);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
