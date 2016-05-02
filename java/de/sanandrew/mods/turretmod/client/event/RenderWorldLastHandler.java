/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
        EntityLivingBase renderEntity = Minecraft.getMinecraft().renderViewEntity;
        Entity pointedEntity = Minecraft.getMinecraft().pointedEntity;
        ItemStack equippedItem = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();

        if( pointedEntity instanceof EntityTurret && ItemStackUtils.isValidStack(equippedItem) && equippedItem.getItem() == ItemRegistry.tcu ) {
            double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * event.partialTicks;
            double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * event.partialTicks;
            double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * event.partialTicks;

            double entityX = pointedEntity.lastTickPosX + (pointedEntity.posX - pointedEntity.lastTickPosX) * event.partialTicks;
            double entityY = pointedEntity.lastTickPosY + (pointedEntity.posY - pointedEntity.lastTickPosY) * event.partialTicks;
            double entityZ = pointedEntity.lastTickPosZ + (pointedEntity.posZ - pointedEntity.lastTickPosZ) * event.partialTicks;

            renderTurretLabel((EntityTurret) pointedEntity, entityX - renderX, entityY - renderY, entityZ - renderZ);
        }
    }

    private static void renderTurretLabel(EntityTurret turret, double x, double y, double z) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        float f1 = 0.005F;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.0F, (float)y + turret.height + 0.5F, (float)z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(-f1, -f1, f1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Tessellator tessellator = Tessellator.instance;


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex((0), (0), 0.0D);
        tessellator.addVertex((0), (64), 0.0D);
        tessellator.addVertex((128), (64), 0.0D);
        tessellator.addVertex((128), (0), 0.0D);
        //health
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
        tessellator.addVertex((1), (22), 0.0D);
        tessellator.addVertex((1), (24), 0.0D);
        tessellator.addVertex((127), (24), 0.0D);
        tessellator.addVertex((127), (22), 0.0D);
        tessellator.setColorRGBA_F(1.0F, 0.0F, 0.0F, 1.0F);
        float healthRel = turret.getHealth() / turret.getMaxHealth();
        tessellator.addVertex((1), (22), 0.0D);
        tessellator.addVertex((1), (24), 0.0D);
        tessellator.addVertex((1 + 126.0F * healthRel), (24), 0.0D);
        tessellator.addVertex((1 + 126.0F * healthRel), (22), 0.0D);
        //ammo
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
        tessellator.addVertex((1), (38), 0.0D);
        tessellator.addVertex((1), (40), 0.0D);
        tessellator.addVertex((127), (40), 0.0D);
        tessellator.addVertex((127), (38), 0.0D);
        tessellator.setColorRGBA_F(0.4F, 0.4F, 1.0F, 1.0F);
        float ammoRel = turret.getTargetProcessor().getAmmoCount() / (float)turret.getTargetProcessor().getMaxAmmoCapacity();
        tessellator.addVertex((1), (38), 0.0D);
        tessellator.addVertex((1), (40), 0.0D);
        tessellator.addVertex((1 + 126.0F * ammoRel), (40), 0.0D);
        tessellator.addVertex((1 + 126.0F * ammoRel), (38), 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        String s = "Turret";
        fontrenderer.drawString(s, 1, 1, -1);
        s = String.format("Health: %.2f/%.2f", turret.getHealth(), turret.getMaxHealth());
        fontrenderer.drawString(s, 1, 12, 0xFFFF8080);
        s = String.format("Ammo: %d/%d", turret.getTargetProcessor().getAmmoCount(), turret.getTargetProcessor().getMaxAmmoCapacity());
        fontrenderer.drawString(s, 1, 28, 0xFFA0A0FF);
        s = String.format("Target: %s", turret.getTargetProcessor().getTarget() == null ? "n/a" : turret.getTargetProcessor().getTarget().getClass().getSimpleName());
        fontrenderer.drawString(s, 1, 44, 0xFFFFFFA0);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
