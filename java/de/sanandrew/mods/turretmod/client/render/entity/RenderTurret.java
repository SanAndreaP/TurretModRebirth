/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.render.entity;

import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.util.ITurretInfo;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
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
    protected int shouldRenderPass(EntityLivingBase livingBase, int pass, float partTicks) {
        if( livingBase instanceof ITurretInfo ) {
            return this.renderGlowMap((ITurretInfo) livingBase, pass);
        }

        return 0;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if( entity instanceof ITurretInfo ) {
            return ((ITurretInfo) entity).getStandardTexture();
        }

        return null;
    }

    private int renderGlowMap(ITurretInfo info, int pass) {
        if( pass == 0 ) {
            this.setRenderPassModel(this.glowModel);
            this.bindTexture(info.getGlowTexture());

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
        }

        return 0;
    }

    private void renderName(AEntityTurretBase turret, Tessellator tess) {
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
    protected void passSpecialRender(EntityLivingBase par1EntityLiving, double par2, double par4, double par6)
    {
        this.renderStats((AEntityTurretBase)par1EntityLiving, par2, par4, par6);
    }

    protected void renderStats(AEntityTurretBase turret, double x, double y, double z) {
        if( MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Pre(turret, this, x, y, z)) ) {
            return;
        }

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

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
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
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
                GL11.glDepthMask(true);
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 0xFFFFFF);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }

        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Post(turret, this, x, y, z));
//        if( Minecraft.isGuiEnabled() /*&& TM3ModRegistry.proxy.getPlayerTM3Data(Minecraft.getMinecraft().thePlayer).getBoolean("renderLabels") && !par1Turret.isInGui()*/) {
//            float scale = 0.016666668F;
//            double renderEntityDistSq = turret.getDistanceSqToEntity(this.renderManager.livingPlayer);
//            float maxRenderDist = 16.0F;
//
//            if( renderEntityDistSq < maxRenderDist * maxRenderDist ) {
//
//                GL11.glPushMatrix();
//                GL11.glTranslatef((float) x + 0.0F, (float)y + 2.8F, (float) z);
//                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
//                GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//                GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
//                GL11.glScalef(-scale, -scale, scale);
//                GL11.glDisable(GL11.GL_LIGHTING);
//                GL11.glTranslatef(0.0F, 0.25F / scale, 0.0F);
//                GL11.glDepthMask(false);
//                GL11.glEnable(GL11.GL_BLEND);
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                Tessellator var15 = Tessellator.instance;
//                GL11.glDisable(GL11.GL_TEXTURE_2D);
//
//                this.renderName(turret, var15);
//
//                double health = (turret.getHealth() / turret.getMaxHealth()) * 50.0D - 25.0D;
//                double ammo = ((double)turret.getAmmo() / (double)turret.getMaxAmmo()) * 50.0D - 25.0D;
////                double exp = ((double)turret.getExperience() / (double)turret.getExpCap()) * 50.0D - 25.0D;
////                boolean hasXP = TurretUpgrades.hasUpgrade(TUpgExperience.class, turret.upgrades) && turret.hasPlayerAccess(Minecraft.getMinecraft().thePlayer);
//
////                if (TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, turret.upgrades) && turret.getAmmo() > 0) {
////                    ammo = 25.0D;
////                }
//
//                //bars bkg
//                var15.startDrawingQuads();
//                var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
//                var15.addVertex(health, 9.0D, 0.0D);
//                var15.addVertex(health, 11.0D, 0.0D);
//                var15.addVertex(25.0D, 11.0D, 0.0D);
//                var15.addVertex(25.0D, 9.0D, 0.0D);
//
//                var15.addVertex(ammo, 11.5D, 0.0D);
//                var15.addVertex(ammo, 13.5D, 0.0D);
//                var15.addVertex(25.0D, 13.5D, 0.0D);
//                var15.addVertex(25.0D, 11.5D, 0.0D);
//
//                var15.addVertex(-25.5D, 8.5D, 0.0D);
//                var15.addVertex(-25.5D, 9.0D, 0.0D);
//                var15.addVertex(25.5D, 9.0D, 0.0D);
//                var15.addVertex(25.5D, 8.5D, 0.0D);
//
//                var15.addVertex(-25.5D, 8.5D, 0.0D);
//                var15.addVertex(-25.5D, 11.5D, 0.0D);
//                var15.addVertex(-25.0D, 11.5D, 0.0D);
//                var15.addVertex(-25.0D, 8.5D, 0.0D);
//                var15.addVertex(25.0D, 8.5D, 0.0D);
//                var15.addVertex(25.0D, 11.5D, 0.0D);
//                var15.addVertex(25.5D, 11.5D, 0.0D);
//                var15.addVertex(25.5D, 8.5D, 0.0D);
//                var15.addVertex(-25.0D, 11.0D, 0.0D);
//                var15.addVertex(-25.0D, 11.5D, 0.0D);
//                var15.addVertex(25.0D, 11.5D, 0.0D);
//                var15.addVertex(25.0D, 11.0D, 0.0D);
//
//                var15.addVertex(-25.5D, 11.0D, 0.0D);
//                var15.addVertex(-25.5D, 14.0D, 0.0D);
//                var15.addVertex(-25.0D, 14.0D, 0.0D);
//                var15.addVertex(-25.0D, 11.0D, 0.0D);
//                var15.addVertex(25.0D, 11.0D, 0.0D);
//                var15.addVertex(25.0D, 14.0D, 0.0D);
//                var15.addVertex(25.5D, 14.0D, 0.0D);
//                var15.addVertex(25.5D, 11.0D, 0.0D);
//                var15.addVertex(-25.0D, 13.5D, 0.0D);
//                var15.addVertex(-25.0D, 14.0D, 0.0D);
//                var15.addVertex(25.0D, 14.0D, 0.0D);
//                var15.addVertex(25.0D, 13.5D, 0.0D);
//
////                if (hasXP) {
////                    var15.addVertex(exp, 14.0D, 0.0D);
////                    var15.addVertex(exp, 16.0D, 0.0D);
////                    var15.addVertex(25.0D, 16.0D, 0.0D);
////                    var15.addVertex(25.0D, 14.0D, 0.0D);
////
////                    var15.addVertex(-25.5D, 13.5D, 0.0D);
////                    var15.addVertex(-25.5D, 16.5D, 0.0D);
////                    var15.addVertex(-25.0D, 16.5D, 0.0D);
////                    var15.addVertex(-25.0D, 13.5D, 0.0D);
////                    var15.addVertex(25.0D, 13.5D, 0.0D);
////                    var15.addVertex(25.0D, 16.5D, 0.0D);
////                    var15.addVertex(25.5D, 16.5D, 0.0D);
////                    var15.addVertex(25.5D, 13.5D, 0.0D);
////                    var15.addVertex(-25.0D, 16.0D, 0.0D);
////                    var15.addVertex(-25.0D, 16.5D, 0.0D);
////                    var15.addVertex(25.0D, 16.5D, 0.0D);
////                    var15.addVertex(25.0D, 16.0D, 0.0D);
////                }
//
//                //health bar
//                var15.setColorRGBA_F(1.0F, 0.0F, 0.0F, 1.0F);
//                var15.addVertex(-25.0D, 9.0D, 0.0D);
//                var15.addVertex(-25.0D, 11.0D, 0.0D);
//                var15.addVertex(health, 11.0D, 0.0D);
//                var15.addVertex(health, 9.0D, 0.0D);
//
//                //ammo bar
//                var15.setColorRGBA_F(0.0F, 0.5F, 1.0F, 1.0F);
//                var15.addVertex(-25.0D, 11.5D, 0.0D);
//                var15.addVertex(-25.0D, 13.5D, 0.0D);
//                var15.addVertex(ammo, 13.5D, 0.0D);
//                var15.addVertex(ammo, 11.5D, 0.0D);
////
////                //exp bar
////                if (hasXP) {
////                    var15.setColorRGBA_F(0.0F, 1.0F, 0.5F, 1.0F);
////                    var15.addVertex(-25.0D, 14.0D, 0.0D);
////                    var15.addVertex(-25.0D, 16.0D, 0.0D);
////                    var15.addVertex(exp, 16.0D, 0.0D);
////                    var15.addVertex(exp, 14.0D, 0.0D);
////                }
//
//                var15.draw();
//                GL11.glEnable(GL11.GL_TEXTURE_2D);
//                GL11.glEnable(GL11.GL_LIGHTING);
//                GL11.glDisable(GL11.GL_BLEND);
//                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//                GL11.glPopMatrix();
//            }
//        }
    }
}
