/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.projectile;

import de.sanandrew.mods.turretmod.api.client.render.IRender;
import de.sanandrew.mods.turretmod.api.client.render.IRenderInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBullet<T extends EntityTurretProjectile>
        implements IRender<T>
{
    @Override
    public void doRender(IRenderInst<T> render, T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        render.bindRenderEntityTexture(entity);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        int textureIndex = 0;
        float uMinMain = 0.0F;
        float uMaxMain = 0.5F;
        float vMinMain = (textureIndex * 10) / 32.0F;
        float vMaxMain = (5 + textureIndex * 10) / 32.0F;
        float uMinBack = 0.0F;
        float uMaxBack = 0.15625F;
        float vMinBack = (5 + textureIndex * 10) / 32.0F;
        float vMaxBack = (10 + textureIndex * 10) / 32.0F;
        float scale = 0.01625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        if( render.renderOutlines() ) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(render.getRenderTeamColor(entity));
        }

        GlStateManager.glNormal3f(scale, 0.0F, 0.0F);
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-7.0D, -2.0D, -2.0D).tex(uMinBack, vMinBack).endVertex();
        buf.pos(-7.0D, -2.0D, 2.0D).tex(uMaxBack, vMinBack).endVertex();
        buf.pos(-7.0D, 2.0D, 2.0D).tex(uMaxBack, vMaxBack).endVertex();
        buf.pos(-7.0D, 2.0D, -2.0D).tex(uMinBack, vMaxBack).endVertex();
        tess.draw();
        GlStateManager.glNormal3f(-scale, 0.0F, 0.0F);
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        buf.pos(-7.0D, 2.0D, -2.0D).tex(uMinBack, vMinBack).endVertex();
        buf.pos(-7.0D, 2.0D, 2.0D).tex(uMaxBack, vMinBack).endVertex();
        buf.pos(-7.0D, -2.0D, 2.0D).tex(uMaxBack, vMaxBack).endVertex();
        buf.pos(-7.0D, -2.0D, -2.0D).tex(uMinBack, vMaxBack).endVertex();
        tess.draw();

        for( int i = 0; i < 4; i++ ) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, scale);
            buf.begin(7, DefaultVertexFormats.POSITION_TEX);
            buf.pos(-8.0D, -2.0D, 0.0D).tex(uMinMain, vMinMain).endVertex();
            buf.pos(8.0D, -2.0D, 0.0D).tex(uMaxMain, vMinMain).endVertex();
            buf.pos(8.0D, 2.0D, 0.0D).tex(uMaxMain, vMaxMain).endVertex();
            buf.pos(-8.0D, 2.0D, 0.0D).tex(uMinMain, vMaxMain).endVertex();
            tess.draw();
        }

        if( render.renderOutlines() ) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public ResourceLocation getRenderTexture(T entity) {
        return Resources.PROJECTILE_BULLET.resource;
    }
}
