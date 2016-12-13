/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.client.render.world.RenderTurretLaser;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretPointed;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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

            RenderTurretPointed.render(mc, renderX, renderY, renderZ, partTicks);
            RenderTurretLaser.render(mc, renderX, renderY, renderZ, partTicks);
        }
    }
}
