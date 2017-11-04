/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderPebble<T extends EntityTurretProjectile>
        extends Render<T>
{
    private ItemStack gravelItem = ItemStackUtils.getEmpty();

    public RenderPebble(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float yaw, float partTicks) {
        if( !ItemStackUtils.isValid(this.gravelItem) ) {
            this.gravelItem = new ItemStack(Blocks.GRAVEL, 1);
        }

        RenderUtils.renderStackInWorld(this.gravelItem, x, y, z, 0.0F, 0.0F, 0.0F, 0.1D);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }
}
