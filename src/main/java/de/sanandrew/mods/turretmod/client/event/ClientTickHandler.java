/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class ClientTickHandler
{
    public static int     ticksInGame;
    public static boolean isSneaking;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if( event.phase == TickEvent.Phase.END ) {
            Screen gui = Minecraft.getInstance().screen;
            if( gui == null || !gui.isPauseScreen() ) {
                ticksInGame++;
            }
        }

        isSneaking = Minecraft.getInstance().options.keyShift.isDown();
    }
}
