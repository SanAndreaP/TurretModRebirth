/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TmrConstants.ID)
public class SneakKeyHandler
{
    private static boolean isSneakPressed;

    private SneakKeyHandler() { }

    @SubscribeEvent
    public static void onKeyRelease(GuiScreenEvent.KeyboardKeyReleasedEvent event) {
        if( event.getKeyCode() == Minecraft.getInstance().options.keyShift.getKey().getValue() ) {
            isSneakPressed = false;
        }
    }

    @SubscribeEvent
    public static void onKeyPress(GuiScreenEvent.KeyboardKeyPressedEvent event) {
        if( event.getKeyCode() == Minecraft.getInstance().options.keyShift.getKey().getValue() ) {
            isSneakPressed = true;
        }
    }

    public static boolean isSneakPressed() {
        return isSneakPressed || Minecraft.getInstance().options.keyShift.isDown();
    }
}
