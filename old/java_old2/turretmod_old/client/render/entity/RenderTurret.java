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
import de.sanandrew.mods.turretmod.client.util.TurretMod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextFormatting;
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
            TurretMod.MOD_LOG.log(Level.ERROR, "Could not instanciate model class! Make sure it has a constructor with a one float parameter (scale)! Glowmap disabled.", ex);
            this.glowModel = null;
        }
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase livingBase, int pass, float partTicks) {
        if( livingBase instanceof EntityTurretBase ) {
            return this.renderGlowMap((EntityTurretBase) livingBase, pass);
        }

        return -1;
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

        return -1;
    }

    @Override
    protected void passSpecialRender(EntityLivingBase par1EntityLiving, double par2, double par4, double par6) {

    }
}
