/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.entity;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.util.TurretMod;
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
            TurretMod.MOD_LOG.log(Level.ERROR, "Could not instanciate model class! Make sure it has a constructor with a one float parameter (scale)!" +
                                          " Glowmap disabled.", ex);
            this.glowModel = null;
        }
    }

    @Override
    public void doRender(EntityLiving living, double x, double y, double z, float yaw, float partTicks) {
        if( living instanceof EntityTurretBase ) {
            EntityTurretBase turret = (EntityTurretBase) living;
            if( turret.renderPass == 0 ) {
                super.doRender(living, x, y, z, yaw, partTicks);
            } else {
                float prevBrightX = OpenGlHelper.lastBrightnessX;
                float prevBrightY = OpenGlHelper.lastBrightnessY;

                int bright = 0xF0;
                int brightX = bright % 65536;
                int brightY = bright / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
                this.renderStats((EntityTurretBase) living, x, y, z);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBrightX, prevBrightY);
            }
        }
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase livingBase, int pass, float partTicks) {
        if( livingBase instanceof EntityTurretBase ) {
            return this.renderGlowMap((EntityTurretBase) livingBase, pass);
        }

        return 0;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if( entity instanceof EntityTurretBase ) {
            return ((EntityTurretBase) entity).getStandardTexture();
        }

        return null;
    }

    private int renderGlowMap(EntityTurretBase turret, int pass) {
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

        return 0;
    }

    private void renderName(EntityTurretBase turret, Tessellator tess) {
        FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
        String turretName = turret.getTurretName() + String.format(" (Freq: %s)", turret.getFrequency());
        String playerName = turret.getOwnerName();

        tess.startDrawingQuads();

        int stringCenter = fontRenderer.getStringWidth(turretName) / 2;
        tess.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tess.addVertex(-stringCenter - 1, -1.0D, 0.0D);
        tess.addVertex(-stringCenter - 1, 8.0D, 0.0D);
        tess.addVertex(stringCenter + 1, 8.0D, 0.0D);
        tess.addVertex(stringCenter + 1, -1.0D, 0.0D);

        if( playerName != null ) {
            stringCenter = fontRenderer.getStringWidth(playerName) / 2;
            tess.addVertex(-stringCenter - 1, 17.0D, 0.0D);
            tess.addVertex(-stringCenter - 1, 26.0D, 0.0D);
            tess.addVertex(stringCenter + 1, 26.0D, 0.0D);
            tess.addVertex(stringCenter + 1, 17.0D, 0.0D);
        }

        tess.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        fontRenderer.drawString(turretName, -fontRenderer.getStringWidth(turretName) / 2, 0, 0xFFFFFF);

        if( playerName != null ) {
            fontRenderer.drawString(playerName, -fontRenderer.getStringWidth(playerName) / 2, 18, 0xBBBBBB);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    @Override
    protected void passSpecialRender(EntityLivingBase par1EntityLiving, double par2, double par4, double par6) { }

    protected void renderStats(EntityTurretBase turret, double x, double y, double z) {
        if( MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Pre(turret, this, x, y, z)) ) {
            return;
        }

        if( this.func_110813_b(turret) && turret == this.renderManager.field_147941_i ) { // can show label
            float scaleMulti = 1.6F;
            float scale = 0.016666668F * scaleMulti;

            if( turret.getDistanceSqToEntity(this.renderManager.livingPlayer) < (NAME_TAG_RANGE * NAME_TAG_RANGE) ) {
                String s = turret.getTurretName() + String.format(" (%s%d%s)", EnumChatFormatting.GREEN, turret.getFrequency(), EnumChatFormatting.RESET);

                FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.0D, y + turret.height + 0.8D, z);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-scale, -scale, scale);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glTranslatef(0.0F, 0.25F / scale, 0.0F);
                Tessellator tessellator = Tessellator.instance;
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tessellator.startDrawingQuads();
                int i = fontrenderer.getStringWidth(s) / 2;
                tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                tessellator.addVertex((-i - 1), -1.0D, 0.0D);
                tessellator.addVertex((-i - 1), 8.0D, 0.0D);
                tessellator.addVertex((i + 1), 8.0D, 0.0D);
                tessellator.addVertex((i + 1), -1.0D, 0.0D);

                tessellator.setColorRGBA_F(0.15F, 0.0F, 0.0F, 0.85F);
                tessellator.addVertex(-19, 8, 0);
                tessellator.addVertex(-19, 10, 0);
                tessellator.addVertex(19, 10, 0);
                tessellator.addVertex(19, 8, 0);
                tessellator.setColorRGBA_F(0.0F, 0.0F, 0.15F, 0.85F);
                tessellator.addVertex(-19, 10, 0);
                tessellator.addVertex(-19, 12, 0);
                tessellator.addVertex(19, 12, 0);
                tessellator.addVertex(19, 10, 0);

                tessellator.setColorRGBA_F(1.0F, 0.0F, 0.0F, 1.0F);
                tessellator.addVertex(-19.0F, 8.0F, 0.0F);
                tessellator.addVertex(-19.0F, 10.0F, 0.0F);
                tessellator.addVertex((turret.getHealth() / turret.getMaxHealth()) * 38.0F - 19.0F, 10.0F, 0.0F);
                tessellator.addVertex((turret.getHealth() / turret.getMaxHealth()) * 38.0F - 19.0F, 8.0F, 0.0F);

                tessellator.setColorRGBA_F(0.25F, 0.25F, 1.0F, 1.0F);
                tessellator.addVertex(-19.0F, 10.0F, 0.0F);
                tessellator.addVertex(-19.0F, 12.0F, 0.0F);
                tessellator.addVertex((turret.getAmmo() / (float) turret.getMaxAmmo()) * 38.0F - 19.0F, 12.0F, 0.0F);
                tessellator.addVertex((turret.getAmmo() / (float) turret.getMaxAmmo()) * 38.0F - 19.0F, 10.0F, 0.0F);
                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 0xFFFFFF);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }

        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Post(turret, this, x, y, z));
    }
}
