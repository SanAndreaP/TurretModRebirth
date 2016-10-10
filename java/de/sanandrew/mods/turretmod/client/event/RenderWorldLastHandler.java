/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.sanlib.lib.client.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;
import java.util.function.Predicate;

public class RenderWorldLastHandler
{
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderEntity = mc.getRenderViewEntity();

        if( renderEntity != null ) {
            float partTicks = event.getPartialTicks();
            double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partTicks;
            double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partTicks;
            double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partTicks;

            Entity pointedEntity = mc.pointedEntity;

            if( pointedEntity instanceof EntityTurret ) {
                if( isItemTCU(mc.thePlayer.getHeldItemMainhand()) || isItemTCU(mc.thePlayer.getHeldItemOffhand()) ) {
                    double entityX = pointedEntity.lastTickPosX + (pointedEntity.posX - pointedEntity.lastTickPosX) * partTicks;
                    double entityY = pointedEntity.lastTickPosY + (pointedEntity.posY - pointedEntity.lastTickPosY) * partTicks;
                    double entityZ = pointedEntity.lastTickPosZ + (pointedEntity.posZ - pointedEntity.lastTickPosZ) * partTicks;

                    renderTurretLabel((EntityTurret) pointedEntity, entityX - renderX, entityY - renderY, entityZ - renderZ);
                }
            }

            Function<EntityTurret, TargetProcessor> tgtProc = EntityTurret::getTargetProcessor;
            Predicate<EntityTurretLaser> chk = turret -> turret != null && tgtProc.apply(turret).hasAmmo() && tgtProc.apply(turret).isShooting() && tgtProc.apply(turret).hasTarget();

            mc.theWorld.getEntities(EntityTurretLaser.class, chk::test).forEach(turret -> renderTurretLaser(turret, renderX, renderY, renderZ, partTicks));
        }
    }

    private static boolean isItemTCU(ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.tcu);
    }

    private static void addQuad(VertexBuffer buf, double minX, double minY, double maxX, double maxY, ColorObj clr) {
        buf.pos(minX, minY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr.fRed(), clr.fGreen(), clr.fBlue(), clr.fAlpha()).endVertex();
    }

    private static void addQuad(VertexBuffer buf, double minX, double minY, double maxX, double maxY, ColorObj clr1, ColorObj clr2) {
        buf.pos(minX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
        buf.pos(minX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, maxY, 0.0D).color(clr2.fRed(), clr2.fGreen(), clr2.fBlue(), clr2.fAlpha()).endVertex();
        buf.pos(maxX, minY, 0.0D).color(clr1.fRed(), clr1.fGreen(), clr1.fBlue(), clr1.fAlpha()).endVertex();
    }

    private static void renderTurretLaser(EntityTurretLaser turret, double renderX, double renderY, double renderZ, double partTicks) {
        Entity tgt = turret.getTargetProcessor().getTarget();

        double turretX = turret.lastTickPosX + (turret.posX - turret.lastTickPosX) * partTicks;
        double turretY = turret.lastTickPosY + (turret.posY - turret.lastTickPosY) * partTicks + turret.getEyeHeight() - 0.1D;
        double turretZ = turret.lastTickPosZ + (turret.posZ - turret.lastTickPosZ) * partTicks;

        double vecX = tgt.lastTickPosX + (tgt.posX - tgt.lastTickPosX) * partTicks - turretX;
        double vecY = tgt.lastTickPosY + (tgt.posY - tgt.lastTickPosY) * partTicks + tgt.height / 1.4F - turretY;
        double vecZ = tgt.lastTickPosZ + (tgt.posZ - tgt.lastTickPosZ) * partTicks - turretZ;

        double dist = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);

        double rotYaw = turret.prevRotationYawHead + (turret.rotationYawHead - turret.prevRotationYawHead) * partTicks;
        double rotPtc = turret.prevRotationPitch + (turret.rotationPitch - turret.prevRotationPitch) * partTicks;

        final double beamWidth = 0.0125D;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();

        GlStateManager.translate(turretX - renderX, turretY - renderY, turretZ - renderZ);
        GlStateManager.rotate(270.0F - (float) rotYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) -rotPtc, 0.0F, 0.0F, 1.0F);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, -beamWidth, -beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(dist, -beamWidth, -beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(dist, beamWidth, beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(0.0D, beamWidth, beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(0.0D, -beamWidth, beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(dist, -beamWidth, beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(dist, beamWidth, -beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        buffer.pos(0.0D, beamWidth, -beamWidth).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void renderTurretLabel(EntityTurret turret, double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontrenderer = mc.fontRendererObj;
        float scale = 0.010F;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        float healthRel = turret.getHealth() / turret.getMaxHealth();
        float ammoRel = turret.getTargetProcessor().getAmmoCount() / (float)turret.getTargetProcessor().getMaxAmmoCapacity();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + (turret.isUpsideDown ? turret.height / 2.0F : turret.height), z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.translate(-64.0D, 0.0D, 0.0D);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addQuad(buffer, 0.0D, 0.0D, 128.0D, 64.0D, new ColorObj(0x80001000));

        addQuad(buffer, -1.0D, -1.0D, 129.0D, 0.0D, new ColorObj(0xCC50FF00));
        addQuad(buffer, -1.0D, 64.0D, 129.0D, 65.0D, new ColorObj(0xCC288000));
        addQuad(buffer, -1.0D, 0.0D, 0.0D, 64.0D, new ColorObj(0xCC50FF00), new ColorObj(0xCC288000));
        addQuad(buffer, 128.0D, 0.0D, 129.0D, 64.0D, new ColorObj(0xCC50FF00), new ColorObj(0xCC288000));

        addQuad(buffer, -1.0D, -2.0D, 129.0D, -1.0D, new ColorObj(0x80001000));
        addQuad(buffer, -1.0D, 65.0D, 129.0D, 66.0D, new ColorObj(0x80001000));
        addQuad(buffer, -2.0D, -1.0D, -1.0D, 65.0D, new ColorObj(0x80001000));
        addQuad(buffer, 129.0D, -1.0D, 130.0D, 65.0D, new ColorObj(0x80001000));

        //health
        addQuad(buffer, 1.0D, 22.0D, 127.0D, 24.0D, new ColorObj(0xFF000000));
        addQuad(buffer, 1.0D, 22.0D, (1 + 126.0F * healthRel), 24.0D, new ColorObj(0xFFFF0000));

        //ammo
        addQuad(buffer, 1.0D, 38.0D, 127.0D, 40.0D, new ColorObj(0xFF000000));
        addQuad(buffer, 1.0D, 38.0D, (1 + 126.0F * ammoRel), 40.0D, new ColorObj(0xFF6666FF));

        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();

        String s = turret.hasCustomName() ? turret.getCustomNameTag() : Lang.translateEntityCls(turret.getClass());
        fontrenderer.drawString(s, 1, 1, -1);
        s = String.format("Health: %.2f/%.2f", turret.getHealth(), turret.getMaxHealth());
        fontrenderer.drawString(s, 1, 12, 0xFFFF8080);
        s = String.format("Ammo: %d/%d", turret.getTargetProcessor().getAmmoCount(), turret.getTargetProcessor().getMaxAmmoCapacity());
        fontrenderer.drawString(s, 1, 28, 0xFFA0A0FF);
        s = String.format("Target: %s", turret.getTargetProcessor().getTarget() == null ? "n/a" : Lang.translateEntityCls(turret.getTargetProcessor().getTarget().getClass()));
        fontrenderer.drawString(s, 1, 44, 0xFFFFFFA0);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
