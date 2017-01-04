/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.world;

import de.sanandrew.mods.sanlib.lib.client.ColorObj;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.function.Predicate;

@SideOnly(Side.CLIENT)
public final class RenderTurretLaser
{
    public static void render(Minecraft mc, double x, double y, double z, double partTicks) {
        Predicate<EntityTurretLaser> chk = turret -> {
            if( turret != null ) {
                TargetProcessor tgtProc = turret.getTargetProcessor();
                return tgtProc.hasAmmo() && tgtProc.isShooting() && tgtProc.hasTarget();
            }
            return false;
        };
        mc.world.getEntities(EntityTurretLaser.class, chk::test).forEach(turret -> renderLaser(turret, x, y, z, partTicks));
    }

    private static void renderLaser(EntityTurretLaser turret, double renderX, double renderY, double renderZ, double partTicks) {
        Entity tgt = turret.getTargetProcessor().getTarget();

        double turretX = turret.lastTickPosX + (turret.posX - turret.lastTickPosX) * partTicks;
        double turretY = turret.lastTickPosY + (turret.posY - turret.lastTickPosY) * partTicks + turret.getEyeHeight() - (turret.isUpsideDown ? 0.95F : 0.13F);
        double turretZ = turret.lastTickPosZ + (turret.posZ - turret.lastTickPosZ) * partTicks;

        double vecX = tgt.lastTickPosX + (tgt.posX - tgt.lastTickPosX) * partTicks - turretX;
        double vecY = tgt.lastTickPosY + (tgt.posY - tgt.lastTickPosY) * partTicks + tgt.height / 1.4F - turretY;
        double vecZ = tgt.lastTickPosZ + (tgt.posZ - tgt.lastTickPosZ) * partTicks - turretZ;

        double dist = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);

        double rotYaw = turret.prevRotationYawHead + (turret.rotationYawHead - turret.prevRotationYawHead) * partTicks;
        double rotPtc = turret.prevRotationPitch + (turret.rotationPitch - turret.prevRotationPitch) * partTicks;

        final double beamWidth = 0.0125D;

        ColorObj laserClr = new ColorObj(1.0F, 0.0F, 0.0F, 0.25F);
        if( turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.UPG_ENDER_MEDIUM) ) {
            laserClr = new ColorObj(0.0F, 0.5F, 1.0F, 0.25F);
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();

        GlStateManager.translate(turretX - renderX, turretY - renderY, turretZ - renderZ);
        GlStateManager.rotate(270.0F - (float) rotYaw, 0.0F, (turret.isUpsideDown ? -1.0F : 1.0F), 0.0F);
        GlStateManager.rotate((float) -rotPtc, 0.0F, 0.0F, (turret.isUpsideDown ? -1.0F : 1.0F));

        GlStateManager.depthMask(false);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, -beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, -beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(0.0D, beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();

        buffer.pos(0.0D, -beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, -beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(0.0D, beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();

        buffer.pos(0.0D, -beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, -beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, -beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(0.0D, -beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();

        buffer.pos(0.0D, beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(0.0D, beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();

        buffer.pos(0.0D, -beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, -beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(0.0D, beamWidth, -beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();

        buffer.pos(0.0D, -beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, -beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(dist, beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        buffer.pos(0.0D, beamWidth, beamWidth).color(laserClr.fRed(), laserClr.fGreen(), laserClr.fBlue(), laserClr.fAlpha()).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}