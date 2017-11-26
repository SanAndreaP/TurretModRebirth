/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public interface IGuiTCU
{
    /**
     * Returns the container on GUI construction to be used.
     * <br>If a container instance is returned, the TCU GUI will be a container GUI.
     * <br>If {@code null} is returned, the TCU GUI will be a "regular" GUI.
     * @return A container to be used or {@code null}, if this is not a container GUI.
     */
    default Container getContainer(EntityPlayer player, ITurretInst turretInst) { return null; }

    void initGui(IGuiTcuInst<?> gui);

    default void updateScreen(IGuiTcuInst<?> gui) {}

    void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY);

    default void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {}

    default void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {}

    default void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) throws IOException {}

    default boolean doKeyIntercept(IGuiTcuInst<?> gui, char typedChar, int keyCode) throws IOException { return false; }

    default void onKeyType(IGuiTcuInst<?> gui, char typedChar, int keyCode) throws IOException {}

    default void onGuiClose(IGuiTcuInst<?> gui) {}

    default void onMouseInput(IGuiTcuInst<?> gui) throws IOException {}
}
