/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.client.renderer.turret.ForcefieldRender;
import dev.sanandrea.mods.turretmod.client.renderer.turret.LabelRegistry;
import dev.sanandrea.mods.turretmod.client.renderer.turret.TurretCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class RenderEventHandler
{
    public static  Entity renderEntity;
    private static Entity backupEntity;
    public static boolean renderPlayer;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft        mc        = Minecraft.getInstance();
        ActiveRenderInfo camera    = mc.gameRenderer.getMainCamera();
        float            partTicks = event.getPartialTicks();

        MatrixStack mStack = event.getMatrixStack();

        ForcefieldRender.INSTANCE.render(mc, event.getMatrixStack(), partTicks, camera);
        LabelRegistry.INSTANCE.render(mc, event.getContext(), mStack, partTicks, camera);
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if( event.phase == TickEvent.Phase.START ) {
            TurretCamera.render(Minecraft.getInstance(), new MatrixStack(), event.renderTickTime);
        }
    }

    @SubscribeEvent
    public static void onPrePlayerRender(RenderPlayerEvent.Pre event) {
        if( renderPlayer && event.getPlayer() == renderEntity ) {
            backupEntity = Minecraft.getInstance().cameraEntity;
            Minecraft.getInstance().cameraEntity = renderEntity;
        }
    }

    @SubscribeEvent
    public static void onPostPlayerRender(RenderPlayerEvent.Post event) {
        if( renderPlayer && event.getPlayer() == renderEntity ) {
            Minecraft.getInstance().cameraEntity = backupEntity;
            renderEntity = null;
        }
    }

    @SubscribeEvent
    public static void onClientWorldUnload(WorldEvent.Unload event) {
        if( event.getWorld() instanceof ClientWorld ) {
            TurretCamera.cleanupRenderers(true);
            LabelRegistry.INSTANCE.cleanupRenderers();
        }
    }
}
