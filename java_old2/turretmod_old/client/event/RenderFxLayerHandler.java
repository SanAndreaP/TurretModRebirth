/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.core.manpack.util.client.event.SAPFxLayerRenderEvent;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import org.lwjgl.opengl.GL11;

public class RenderFxLayerHandler
{
    @SubscribeEvent
    public void onRenderFxPre(SAPFxLayerRenderEvent.Pre event) {
        if( event.layerId == ClientProxy.particleFxLayer1 ) {
            GL11.glDepthMask(false);
        }
    }

    @SubscribeEvent
    public void onRenderFxPost(SAPFxLayerRenderEvent.Post event) {
        if( event.layerId == ClientProxy.particleFxLayer1 ) {
            GL11.glDepthMask(true);
        }
    }
}
