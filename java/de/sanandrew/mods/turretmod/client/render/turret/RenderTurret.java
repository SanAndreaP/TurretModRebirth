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
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.InvocationTargetException;

public class RenderTurret
        extends RenderLiving
{
    private ModelBase glowModel;

    public RenderTurret(ModelBase standardModel) {
        super(standardModel, 0.5F);
        try {
            this.glowModel = standardModel.getClass().getConstructor(float.class).newInstance(0.001F);
        } catch( NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Could not instanciate model class! Make sure it has a constructor with a one float parameter (scale)! Glowmap disabled.", ex);
            this.glowModel = null;
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partTicks) {
        super.doRender(entity, x, y, z, yaw, partTicks);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase livingBase, int pass, float partTicks) {
        if( livingBase instanceof EntityTurret ) {
            return this.renderGlowMap((EntityTurret) livingBase, pass);
        }

        return -1;
    }

    @Override
    protected void rotateCorpse(EntityLivingBase living, float x, float y, float z) {
        super.rotateCorpse(living, x, y, z);

        if( living instanceof EntityTurret && ((EntityTurret) living).isUpsideDown ) {
            GL11.glTranslatef(0.0F, living.height + 0.2F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if( entity instanceof EntityTurret ) {
            return ((EntityTurret) entity).getStandardTexture();
        }

        return null;
    }

    private int renderGlowMap(EntityTurret turret, int pass) {
        if( pass == 0 ) {
            this.setRenderPassModel(this.glowModel);
            this.bindTexture(turret.getGlowTexture());

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            int bright = 0xF0;
            int brightX = bright % 65536;
            int brightY = bright / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(false);

            return 1;
        } else if( pass == 1 ) {
            this.setRenderPassModel(this.mainModel);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
        }

        return -1;
    }

    @Override
    protected void passSpecialRender(EntityLivingBase entity, double x, double y, double z) {
        renderTurretRange((EntityTurret) entity, x, y, z);
    }

    private static void renderTurretRange(EntityTurret turret, double x, double y, double z) {
        if( turret.showRange ) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            Tessellator tess = Tessellator.instance;

            int range = MathHelper.floor_double(turret.getTargetProcessor().getRange());

            GL11.glLineWidth(5.0F);
            tess.startDrawing(GL11.GL_LINE_LOOP);
            tess.setColorRGBA(255, 0, 0, 64);
            tess.addVertex(x, y, z);
            tess.addVertex(x, y + range, z);
            tess.draw();

            GL11.glLineWidth(0.1F);
            for( double j = -range; j <= range; j += 1.0D ) {
                tess.startDrawing(GL11.GL_LINE_LOOP);
                tess.setColorRGBA(255, 0, 0, 64);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double xC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double yC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);


                    tess.addVertex(x + xC, y + yC, z + j);
                }
                tess.draw();

                tess.startDrawing(GL11.GL_LINE_LOOP);
                tess.setColorRGBA(255, 0, 0, 64);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double zC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double yC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);

                    tess.addVertex(x + j, y + yC, z + zC);
                }
                tess.draw();

                tess.startDrawing(GL11.GL_LINE_LOOP);
                tess.setColorRGBA(255, 0, 0, 64);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double xC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double zC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);

                    tess.addVertex(x + xC, y + j, z + zC);
                }
                tess.draw();
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
