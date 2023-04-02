/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.event;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class ClientTickHandler
{
    public static int     ticksInGame;
    public static boolean isSneaking;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if( event.phase == TickEvent.Phase.END ) {
            Screen gui = Minecraft.getInstance().screen;
            if( gui == null || !gui.isPauseScreen() ) {
                ticksInGame++;
            }
        }

        isSneaking = Minecraft.getInstance().options.keyShift.isDown();
    }
}
