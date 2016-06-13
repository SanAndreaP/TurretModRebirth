/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSnowball
        extends Render
{
    private EntityItem snowblock;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partTicks) {
        if( this.snowblock == null ) {
            this.snowblock = new EntityItem(entity.worldObj, 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.snow, 1));
            this.snowblock.hoverStart = 0.0F;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);

        RenderItem.renderInFrame = true;

        GL11.glPushMatrix();
//                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
//                GL11.glTranslatef(-0.25F + 0.5F * x, 0.15F + 0.05F, -0.425F + 0.05F * y);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        RenderManager.instance.renderEntityWithPosYaw(this.snowblock, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        RenderItem.renderInFrame = false;

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
