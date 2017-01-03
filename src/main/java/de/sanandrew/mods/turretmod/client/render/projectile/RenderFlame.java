/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileFlame;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderFlame<T extends EntityProjectileFlame>
        extends Render<T>
{
    public RenderFlame(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float yaw, float partTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        double scale = 2.0F;// * (entity.deathUpdateTicks / 20.0F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.disableLighting();

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buf = tess.getBuffer();
        float prevBrightX = OpenGlHelper.lastBrightnessX;
        float prevBrightY = OpenGlHelper.lastBrightnessY;
        
        int brightness = 0xF0;
        int brightX = brightness % 65536;
        int brightY = brightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-0.125D, -0.0625D, 0.0D).tex(0.0D, 1.0D).endVertex();
        buf.pos(0.125D, -0.0625D, 0.0D).tex(1.0D, 1.0D).endVertex();
        buf.pos(0.125D, 0.1875D, 0.0D).tex(1.0D, 0.0D).endVertex();
        buf.pos(-0.125D, 0.1875D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tess.draw();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, yaw, partTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return entity.purifying ? Resources.PROJECTILE_FLAME_BLUE.getResource() : Resources.PROJECTILE_FLAME_RED.getResource();
    }
}
