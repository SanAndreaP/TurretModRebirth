/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ClientTickHandler
{
    public static int     ticksInGame;
    public static boolean isSneaking;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if( event.phase == TickEvent.Phase.END ) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if( gui == null || !gui.doesGuiPauseGame() ) {
                ticksInGame++;
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        setSneaking();
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        setSneaking();
    }

    private static void setSneaking() {
        isSneaking = Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
    }
}
