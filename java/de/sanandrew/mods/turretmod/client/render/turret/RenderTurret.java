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
//        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Pre(entity, this, x, y, z))) return;
//        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
////        GL11.glDepthMask(true);
////        GL11.glDepthFunc(GL11.GL_NEVER);
//
//        double d3 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
//
//        if (d3 <= (double)(64 * 64))
//        {
//            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
//            float f = 1.6F;
//            float f1 = 0.016666668F * f;
//            GL11.glPushMatrix();
//            GL11.glTranslatef((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
//            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
//            GL11.glScalef(-f1, -f1, f1);
//            GL11.glDisable(GL11.GL_LIGHTING);
//            GL11.glDepthMask(false);
//            GL11.glDisable(GL11.GL_DEPTH_TEST);
//            GL11.glEnable(GL11.GL_BLEND);
//            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
//            Tessellator tessellator = Tessellator.instance;
//            byte b0 = 0;
//
//            String s = "test";
//
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
//            tessellator.startDrawingQuads();
//            int j = fontrenderer.getStringWidth(s) / 2;
//            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.95F);
//            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
//            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
//            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
//            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
//            tessellator.draw();
//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, b0, 553648127);
//            GL11.glEnable(GL11.GL_DEPTH_TEST);
//            GL11.glDepthMask(true);
//            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, b0, -1);
//            GL11.glEnable(GL11.GL_LIGHTING);
//            GL11.glDisable(GL11.GL_BLEND);
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            GL11.glPopMatrix();
//        }
//
////        GL11.glDepthFunc(GL11.GL_LEQUAL);
////        GL11.glDepthMask(true);
//        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Post(entity, this, x, y, z));
    }
}
