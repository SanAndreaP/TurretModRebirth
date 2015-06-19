/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderTurretArrow
        extends RenderArrow
{
    public void doRender(EntityArrow arrow, double x, double y, double z, float yaw, float partTicks) {
        this.bindEntityTexture(arrow);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(arrow.prevRotationYaw + (arrow.rotationYaw - arrow.prevRotationYaw) * partTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(arrow.prevRotationPitch + (arrow.rotationPitch - arrow.prevRotationPitch) * partTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        float shake = arrow.arrowShake - partTicks;

        if( shake > 0.0F ) {
            float shakeVal = -MathHelper.sin(shake * 3.0F) * shake;
            GL11.glRotatef(shakeVal, 0.0F, 0.0F, 1.0F);
        }

        float scale = 0.03625F;
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(scale, 0.0F, 0.0F);

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, 0.0F, 0.15625F);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, 0.15625F, 0.15625F);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, 0.15625F, 0.3125F);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, 0.0F, 0.3125F);
        tessellator.draw();
        GL11.glNormal3f(-scale, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, 0.0F, 0.15625F);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, 0.15625F, 0.15625F);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, 0.15625F, 0.3125F);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, 0.0F, 0.3125F);
        tessellator.draw();

        for( int i = 0; i < 4; ++i ) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, scale);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, 0.0F, 0.0F);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, 0.5F, 0.0F);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, 0.5F, 0.15625F);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, 0.0F, 0.15625F);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
