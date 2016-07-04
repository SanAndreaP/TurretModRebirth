/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class RenderWorldLastHandler
{
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity renderEntity = mc.getRenderViewEntity();
        Entity pointedEntity = mc.pointedEntity;

        if( pointedEntity instanceof EntityTurret && renderEntity != null ) {
            if( isItemTCU(mc.thePlayer.getHeldItemMainhand()) || isItemTCU(mc.thePlayer.getHeldItemOffhand()) ) {
                float partTicks = event.getPartialTicks();
                double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partTicks;
                double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partTicks;
                double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partTicks;

                double entityX = pointedEntity.lastTickPosX + (pointedEntity.posX - pointedEntity.lastTickPosX) * partTicks;
                double entityY = pointedEntity.lastTickPosY + (pointedEntity.posY - pointedEntity.lastTickPosY) * partTicks;
                double entityZ = pointedEntity.lastTickPosZ + (pointedEntity.posZ - pointedEntity.lastTickPosZ) * partTicks;

                renderTurretLabel((EntityTurret) pointedEntity, entityX - renderX, entityY - renderY, entityZ - renderZ);
            }
        }
    }

    private static boolean isItemTCU(ItemStack stack) {
        return ItemStackUtils.isValidStack(stack) && stack.getItem() == ItemRegistry.tcu;
    }

    private static void renderTurretLabel(EntityTurret turret, double x, double y, double z) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontrenderer = mc.fontRendererObj;
        float scale = 0.010F;

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

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(0.0D, 64.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(128.0D, 64.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(128.0D, 0.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();

        buffer.pos(-1.0D, -1.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(-1.0D, 0.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(129.0D, 0.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(129.0D, -1.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(-1.0D, 64.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(-1.0D, 65.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(129.0D, 65.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(129.0D, 64.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(-1.0D, 0.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(-1.0D, 64.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(0.0D, 64.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(128.0D, 0.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();
        buffer.pos(128.0D, 64.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(129.0D, 64.0D, 0.0D).color(0.15625F, 0.5F, 0.0F, 0.8F).endVertex();
        buffer.pos(129.0D, 0.0D, 0.0D).color(0.3125F, 1.0F, 0.0F, 0.8F).endVertex();

        buffer.pos(-1.0D, -2.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-1.0D, -1.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(129.0D, -1.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(129.0D, -2.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-1.0D, 65.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-1.0D, 66.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(129.0D, 66.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(129.0D, 65.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-2.0D, -1.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-2.0D, 65.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-1.0D, 65.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(-1.0D, -1.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(129.0D, -1.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(129.0D, 65.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(130.0D, 65.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();
        buffer.pos(130.0D, -1.0D, 0.0D).color(0.0F, 0.0625F, 0.0F, 0.5F).endVertex();

        //health
        buffer.pos(1.0D, 22.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(1.0D, 24.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(127.0D, 24.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(127.0D, 22.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        float healthRel = turret.getHealth() / turret.getMaxHealth();
        buffer.pos(1.0D, 22.0D, 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(1.0D, 24.0D, 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * healthRel), 24.0D, 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * healthRel), 22.0D, 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();

        //ammo
        buffer.pos(1.0D, 38.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(1.0D, 40.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(127.0D, 40.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(127.0D, 38.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        float ammoRel = turret.getTargetProcessor().getAmmoCount() / (float)turret.getTargetProcessor().getMaxAmmoCapacity();
        buffer.pos(1.0D, 38.0D, 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        buffer.pos(1.0D, 40.0D, 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * ammoRel), 40.0D, 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * ammoRel), 38.0D, 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
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
