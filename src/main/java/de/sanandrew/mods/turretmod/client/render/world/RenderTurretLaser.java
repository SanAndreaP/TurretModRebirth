/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.render.world;

import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretLaser;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.function.Predicate;

@SideOnly(Side.CLIENT)
public final class RenderTurretLaser
{
    public static void render(Minecraft mc, double x, double y, double z, double partTicks) {
        Predicate<EntityTurret> chk = turretInst -> {
            if( turretInst != null && turretInst.getTurret() instanceof TurretLaser ) {
                ITargetProcessor tgtProc = turretInst.getTargetProcessor();
                return tgtProc.hasAmmo() && tgtProc.isShooting() && tgtProc.hasTarget();
            }
            return false;
        };
        mc.world.getEntities(EntityTurret.class, chk::test).forEach(turret -> renderLaser(turret, x, y, z, partTicks));
    }

    private static void renderLaser(ITurretInst turret, double renderX, double renderY, double renderZ, double partTicks) {
        Entity tgt = turret.getTargetProcessor().getTarget();
        EntityLiving turretL = turret.getEntity();

        double turretX = turretL.lastTickPosX + (turretL.posX - turretL.lastTickPosX) * partTicks;
        double turretY = turretL.lastTickPosY + (turretL.posY - turretL.lastTickPosY) * partTicks + turretL.getEyeHeight() - (turret.isUpsideDown() ? 0.95F : 0.13F);
        double turretZ = turretL.lastTickPosZ + (turretL.posZ - turretL.lastTickPosZ) * partTicks;

        double vecX = tgt.lastTickPosX + (tgt.posX - tgt.lastTickPosX) * partTicks - turretX;
        double vecY = tgt.lastTickPosY + (tgt.posY - tgt.lastTickPosY) * partTicks + tgt.height / 1.4F - turretY;
        double vecZ = tgt.lastTickPosZ + (tgt.posZ - tgt.lastTickPosZ) * partTicks - turretZ;

        double dist = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);

        double rotYaw = turretL.prevRotationYawHead + (turretL.rotationYawHead - turretL.prevRotationYawHead) * partTicks;
        double rotPtc = turretL.prevRotationPitch + (turretL.rotationPitch - turretL.prevRotationPitch) * partTicks;

        final double beamWidth = 0.0125D;

        ColorObj laserClr = new ColorObj(1.0F, 0.0F, 0.0F, 0.25F);
        if( turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.ENDER_MEDIUM) ) {
            laserClr = new ColorObj(0.0F, 0.5F, 1.0F, 0.25F);
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();

        GlStateManager.translate(turretX - renderX, turretY - renderY, turretZ - renderZ);
        GlStateManager.rotate(270.0F - (float) rotYaw, 0.0F, (turret.isUpsideDown() ? -1.0F : 1.0F), 0.0F);
        GlStateManager.rotate((float) -rotPtc, 0.0F, 0.0F, (turret.isUpsideDown() ? -1.0F : 1.0F));

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
