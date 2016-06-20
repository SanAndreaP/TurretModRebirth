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
    public RenderTurret(ModelBase standardModel) {
        super(Minecraft.getMinecraft().getRenderManager(), standardModel, 0.5F);

        this.addLayer(new LayerTurretGlow<>(this));
//        try {
//            this.glowModel = standardModel.getClass().getConstructor(float.class).newInstance(0.001F);
//        } catch( NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex ) {
//            TurretModRebirth.LOG.log(Level.ERROR, "Could not instanciate model class! Make sure it has a constructor with a one float parameter (scale)! Glowmap disabled.", ex);
//            this.glowModel = null;
//        }
    }

    @Override
    public void doRender(EntityTurret entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        renderTurretRange(entity, x, y, z);
    }

    @Override
    protected void renderModel(EntityTurret turret, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float scale) {
        super.renderModel(turret, limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, scale);

        if( TmrConfiguration.renderUpgrades ) {
            GL11.glPushMatrix();

                GL11.glRotated((rotYaw + 90.0D), 0.0F, 1.0F, 0.0F);
                GL11.glRotated(rotPitch, 0.0F, 0.0F, 1.0F);

                RenderTurret.renderUpgrades(turret);
                if( turret.hurtTime > 0 ) {
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glColor4f(0.4F, 0.0F, 0.0F, 1.0F);

                    RenderTurret.renderUpgrades(turret);

                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }
            }
            GL11.glPopMatrix();
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

//    private int renderGlowMap(EntityTurret turret, int pass) {
//        if( pass == 0 ) {
//            this.setRenderPassModel(this.glowModel);
//            this.bindTexture(turret.getGlowTexture());
//
//            GL11.glEnable(GL11.GL_BLEND);
//            GL11.glDisable(GL11.GL_ALPHA_TEST);
//            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
//
//            int bright = 0xF0;
//            int brightX = bright % 65536;
//            int brightY = bright / 65536;
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX / 1.0F, brightY / 1.0F);
//
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            GL11.glDepthMask(false);
//
//            return 1;
//        } else if( pass == 1 ) {
//            this.setRenderPassModel(this.mainModel);
//            GL11.glDepthMask(true);
//            GL11.glDisable(GL11.GL_BLEND);
//        }
//
//        return -1;
//    }

    private static void renderTurretRange(EntityTurret turret, double x, double y, double z) {
        if( turret.showRange ) {
//            GL11.glDisable(GL11.GL_LIGHTING);
            GlStateManager.disableLighting();
//            GL11.glEnable(GL11.GL_BLEND);
            GlStateManager.enableBlend();
//            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.disableTexture2D();

            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buf = tess.getBuffer();

            int range = MathHelper.floor_double(turret.getTargetProcessor().getRange());

            GlStateManager.glLineWidth(5.0F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            buf.pos(x, y, z).color(255, 0, 0, 64).endVertex();
            buf.pos(x, y + range, z).color(255, 0, 0, 64).endVertex();
            tess.draw();

            GlStateManager.glLineWidth(0.1F);
            for( double j = -range; j <= range; j += 1.0D ) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double xC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double yC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);


                    buf.pos(x + xC, y + yC, z + j).color(255, 0, 0, 64).endVertex();
                }
                tess.draw();

                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double zC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double yC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);

                    buf.pos(x + j, y + yC, z + zC).color(255, 0, 0, 64).endVertex();
                }
                tess.draw();

                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
//                tess.setColorRGBA(255, 0, 0, 64);
                for( int i = 0; i <= 360; i+=5 ) {
                    double neg = Math.sin(Math.acos(j / range)) * range;
                    double xC = neg * Math.sin(i * Math.PI * 2.0D / 360.0D);
                    double zC = neg * Math.cos(i * Math.PI * 2.0D / 360.0D);

                    buf.pos(x + xC, y + j, z + zC).color(255, 0, 0, 64).endVertex();
                }
                tess.draw();
            }

//            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.enableTexture2D();
//            GL11.glEnable(GL11.GL_LIGHTING);
            GlStateManager.enableLighting();
//            GL11.glDisable(GL11.GL_BLEND);
            GlStateManager.disableLighting();
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    //TODO: re-enable item rendering!
    public static void renderUpgrades(EntityTurret turret) {
        int cnt = turret.getUpgradeProcessor().getSizeInventory();
//        RenderItemFrame.renderInFrame = true;
//        for( int i = 0; i < cnt; i++ ) {
//            ItemStack slotStack = turret.getUpgradeProcessor().getStackInSlot(i);
//            if( slotStack != null ) {
//                EntityItem entityitem = new EntityItem(turret.worldObj, 0.0D, 0.0D, 0.0D, slotStack.copy());
//                entityitem.getEntityItem().stackSize = 1;
//                entityitem.hoverStart = 0.0F;
//
//                int x = i / 18;
//                int y = i % 18;
//
//                GL11.glPushMatrix();
//                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
//                GL11.glScalef(0.5F, 0.5F, 0.5F);
//                GL11.glTranslatef(-0.25F + 0.5F * x, 0.15F + 0.05F, -0.425F + 0.05F * y);
//                GL11.glEnable(GL11.GL_BLEND);
//                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
//                RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
//                GL11.glDisable(GL11.GL_BLEND);
//                GL11.glPopMatrix();
//            }
//        }
//        RenderItem.renderInFrame = false;
    }

//    private static float interpolateRotation(float rot1, float rot2, float partTicks) {
//        float rotDelta = rot2 - rot1;
//
//        while( rotDelta < -180.0F ) {
//            rotDelta += 360.0F;
//        }
//
//        while( rotDelta >= 180.0F ) {
//            rotDelta -= 360.0F;
//        }
//
//        return rot1 + partTicks * rotDelta;
//    }

}
