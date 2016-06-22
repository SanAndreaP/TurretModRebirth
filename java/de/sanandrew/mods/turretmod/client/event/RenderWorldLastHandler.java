/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

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

        if( pointedEntity instanceof EntityTurret ) {
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
        float f1 = 0.005F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x + 0.0F, (float)y + turret.height + 0.5F, (float)z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0, (0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        buffer.pos(0, (64), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        buffer.pos(128, (64), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        buffer.pos(128, (0), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        //health
        buffer.pos((1), (22), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1), (24), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((127), (24), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((127), (22), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        float healthRel = turret.getHealth() / turret.getMaxHealth();
        buffer.pos((1), (22), 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1), (24), 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * healthRel), (24), 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * healthRel), (22), 0.0D).color(1.0F, 0.0F, 0.0F, 1.0F).endVertex();
        //ammo
        buffer.pos((1), (38), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((1), (40), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((127), (40), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos((127), (38), 0.0D).color(0.0F, 0.0F, 0.0F, 1.0F).endVertex();
        float ammoRel = turret.getTargetProcessor().getAmmoCount() / (float)turret.getTargetProcessor().getMaxAmmoCapacity();
        buffer.pos((1), (38), 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        buffer.pos((1), (40), 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * ammoRel), (40), 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        buffer.pos((1 + 126.0F * ammoRel), (38), 0.0D).color(0.4F, 0.4F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        String s = "Turret";
        fontrenderer.drawString(s, 1, 1, -1);
        s = String.format("Health: %.2f/%.2f", turret.getHealth(), turret.getMaxHealth());
        fontrenderer.drawString(s, 1, 12, 0xFFFF8080);
        s = String.format("Ammo: %d/%d", turret.getTargetProcessor().getAmmoCount(), turret.getTargetProcessor().getMaxAmmoCapacity());
        fontrenderer.drawString(s, 1, 28, 0xFFA0A0FF);
        s = String.format("Target: %s", turret.getTargetProcessor().getTarget() == null ? "n/a" : turret.getTargetProcessor().getTarget().getClass().getSimpleName());
        fontrenderer.drawString(s, 1, 44, 0xFFFFFFA0);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}