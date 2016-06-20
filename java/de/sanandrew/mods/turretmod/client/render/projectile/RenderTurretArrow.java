/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderTurretArrow<T extends EntityTurretProjectile>
        extends Render<T>
{
    private static final ResourceLocation ARROW_TEXTURES = new ResourceLocation("textures/entity/arrow.png");

    public RenderTurretArrow() {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float yaw, float partTicks) {
        this.bindEntityTexture(entity);

//        GL11.glPushMatrix();
//        GL11.glTranslatef((float)x, (float)y, (float)z);
//        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partTicks - 90.0F, 0.0F, 1.0F, 0.0F);
//        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partTicks, 0.0F, 0.0F, 1.0F);
//        Tessellator tessellator = Tessellator.instance;
//        byte b0 = 1;
//        float f2 = 0.0F;
//        float f3 = 0.5F;
//        float f4 = (b0 * 10) / 32.0F;
//        float f5 = (5 + b0 * 10) / 32.0F;
//        float f6 = 0.0F;
//        float f7 = 0.15625F;
//        float f8 = (5 + b0 * 10) / 32.0F;
//        float f9 = (10 + b0 * 10) / 32.0F;
//        float f10 = 0.01625F;
//        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buf = tess.getBuffer();
        int textureIndex = 1;
        float f = 0.0F;
        float f1 = 0.5F;
        float f2 = (textureIndex * 10) / 32.0F;
        float f3 = (5 + textureIndex * 10) / 32.0F;
        float f4 = 0.0F;
        float f5 = 0.15625F;
        float f6 = (5 + textureIndex * 10) / 32.0F;
        float f7 = (10 + textureIndex * 10) / 32.0F;
        float f8 = 0.01625F;
        GlStateManager.enableRescaleNormal();
//        float f9 = (float)entity.arrowShake - partialTicks;

//        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
//        GL11.glScalef(f10, f10, f10);
//        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
//        GL11.glNormal3f(f10, 0.0F, 0.0F);
//        tess.startDrawingQuads();
//        tess.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f8);
//        tess.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f8);
//        tess.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f9);
//        tess.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f9);
//        tess.draw();
//        GL11.glNormal3f(-f10, 0.0F, 0.0F);
//        tess.startDrawingQuads();
//        tess.addVertexWithUV(-7.0D, 2.0D, -2.0D, f6, f8);
//        tess.addVertexWithUV(-7.0D, 2.0D, 2.0D, f7, f8);
//        tess.addVertexWithUV(-7.0D, -2.0D, 2.0D, f7, f9);
//        tess.addVertexWithUV(-7.0D, -2.0D, -2.0D, f6, f9);
//        tess.draw();
//
//        for (int i = 0; i < 4; ++i)
//        {
//            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
//            GL11.glNormal3f(0.0F, 0.0F, f10);
//            tess.startDrawingQuads();
//            tess.addVertexWithUV(-8.0D, -2.0D, 0.0D, f2, f4);
//            tess.addVertexWithUV(8.0D, -2.0D, 0.0D, f3, f4);
//            tess.addVertexWithUV(8.0D, 2.0D, 0.0D, f3, f5);
//            tess.addVertexWithUV(-8.0D, 2.0D, 0.0D, f2, f5);
//            tess.draw();
//        }
//
//        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//        GL11.glPopMatrix();
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f8, f8, f8);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        GlStateManager.glNormal3f(f8, 0.0F, 0.0F);
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-7.0D, -2.0D, -2.0D).tex(f4, f6).endVertex();
        buf.pos(-7.0D, -2.0D, 2.0D).tex(f5, f6).endVertex();
        buf.pos(-7.0D, 2.0D, 2.0D).tex(f5, f7).endVertex();
        buf.pos(-7.0D, 2.0D, -2.0D).tex(f4, f7).endVertex();
        tess.draw();
        GlStateManager.glNormal3f(-f8, 0.0F, 0.0F);
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-7.0D, 2.0D, -2.0D).tex(f4, f6).endVertex();
        buf.pos(-7.0D, 2.0D, 2.0D).tex(f5, f6).endVertex();
        buf.pos(-7.0D, -2.0D, 2.0D).tex(f5, f7).endVertex();
        buf.pos(-7.0D, -2.0D, -2.0D).tex(f4, f7).endVertex();
        tess.draw();

        for (int j = 0; j < 4; ++j)
        {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, f8);
            buf.begin(7, DefaultVertexFormats.POSITION_TEX);
            buf.pos(-8.0D, -2.0D, 0.0D).tex(f, f2).endVertex();
            buf.pos(8.0D, -2.0D, 0.0D).tex(f1, f2).endVertex();
            buf.pos(8.0D, 2.0D, 0.0D).tex(f1, f3).endVertex();
            buf.pos(-8.0D, 2.0D, 0.0D).tex(f, f3).endVertex();
            tess.draw();
        }

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return ARROW_TEXTURES;
    }
}
