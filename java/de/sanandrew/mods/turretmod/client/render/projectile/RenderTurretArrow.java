/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderTurretArrow
        extends Render
{
    private static final ResourceLocation ARROW_TEXTURES = new ResourceLocation("textures/entity/arrow.png");

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partTicks) {
        this.bindEntityTexture(entity);

        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 1;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (b0 * 10) / 32.0F;
        float f5 = (5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (5 + b0 * 10) / 32.0F;
        float f9 = (10 + b0 * 10) / 32.0F;
        float f10 = 0.01625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, f2, f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, f3, f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, f3, f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, f2, f5);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ARROW_TEXTURES;
    }
}
