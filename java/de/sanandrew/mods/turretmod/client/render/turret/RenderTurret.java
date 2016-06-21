/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.InvocationTargetException;

public class RenderTurret
        extends RenderLiving<EntityTurret>
{
    public RenderTurret(RenderManager manager, ModelBase standardModel) {
        super(manager, standardModel, 0.5F);

        try {
            this.addLayer(new LayerTurretGlow<>(this, standardModel.getClass().getConstructor(float.class).newInstance(0.001F)));
        } catch( NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Could not instanciate model class! Make sure it has a constructor with a one float parameter (scale)! Glowmap disabled.", ex);
        }

        this.addLayer(new LayerTurretUpgrades<>());
    }

    @Override
    public void doRender(EntityTurret entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        renderTurretRange(entity, x, y, z);
    }

    @Override
    protected void renderModel(EntityTurret turret, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float scale) {
        super.renderModel(turret, limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale);
    }

    @Override
    protected void rotateCorpse(EntityTurret turret, float x, float y, float z) {
        super.rotateCorpse(turret, x, y, z);

        if( turret.isUpsideDown ) {
            GlStateManager.translate(0.0F, turret.height + 0.2F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTurret entity) {
        return entity.getStandardTexture();
    }

    private static void renderTurretRange(EntityTurret turret, double x, double y, double z) {
        if( turret.showRange ) {
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();

            float prevBrightX = OpenGlHelper.lastBrightnessX;
            float prevBrightY = OpenGlHelper.lastBrightnessX;
            int brightness = 0xF0;
            int brightX = brightness % 65536;
            int brightY = brightness / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buf = tess.getBuffer();

            int range = MathHelper.floor_double(turret.getTargetProcessor().getRange());

            GlStateManager.glLineWidth(5.0F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(x, y, z).color(255, 0, 0, 64).endVertex();
            buf.pos(x, y + range, z).color(255, 0, 0, 64).endVertex();
            tess.draw();

            GlStateManager.glLineWidth(0.1F);
            for( double j = -range; j <= range; j += 1.0D ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double xC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double yC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);


                    buf.pos(x + xC, y + yC, z + j).color(255, 0, 0, 64).endVertex();
                }
                tess.draw();

                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double zC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double yC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);

                    buf.pos(x + j, y + yC, z + zC).color(255, 0, 0, 64).endVertex();
                }
                tess.draw();

                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double xC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double zC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);

                    buf.pos(x + xC, y + j, z + zC).color(255, 0, 0, 64).endVertex();
                }
                tess.draw();
            }

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX,prevBrightY);

            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableLighting();
        }
    }
}
