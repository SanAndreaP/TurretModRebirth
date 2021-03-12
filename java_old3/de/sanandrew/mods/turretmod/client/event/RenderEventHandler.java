/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.client.render.world.RenderTurretCam;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretLaser;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretPointed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderEventHandler
{
    public static Entity renderEntity;
    private static Entity backupEntity;
    public static boolean renderPlayer;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
//        for(int i = 0; i < 10000000; i++) System.nanoTime(); // lag-induced testing

        Minecraft mc = Minecraft.getMinecraft();
        Entity renderEntity = mc.getRenderViewEntity();

        if( renderEntity != null ) {
            float partTicks = event.getPartialTicks();
            double renderX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partTicks;
            double renderY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partTicks;
            double renderZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partTicks;

            RenderTurretPointed.INSTANCE.render(mc, renderX, renderY, renderZ, partTicks);
            RenderTurretLaser.render(mc, renderX, renderY, renderZ, partTicks);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if( event.phase != TickEvent.Phase.END ) {
            RenderTurretCam.render(Minecraft.getMinecraft(), event.renderTickTime);
        }
    }

    @SubscribeEvent
    public void onPrePlayerRender(RenderPlayerEvent.Pre event) {
        if( renderPlayer && event.getEntityPlayer() == renderEntity ) {
            backupEntity = Minecraft.getMinecraft().getRenderManager().renderViewEntity;
            Minecraft.getMinecraft().getRenderManager().renderViewEntity = renderEntity;
        }
    }

    @SubscribeEvent
    public void onPostPlayerRender(RenderPlayerEvent.Post event) {
        if( renderPlayer && event.getEntityPlayer() == renderEntity ) {
            Minecraft.getMinecraft().getRenderManager().renderViewEntity = backupEntity;
            renderEntity = null;
        }
    }

    @SubscribeEvent
    public void onClientWorldUnload(WorldEvent.Unload event) {
        if( event.getWorld() instanceof WorldClient ) {
            RenderTurretCam.cleanupRenderers(true);
            RenderTurretPointed.INSTANCE.cleanupRenderers(true);
        }
    }
}
