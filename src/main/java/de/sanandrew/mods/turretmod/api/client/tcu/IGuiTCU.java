/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * <p>An object defining a Turret Control Unit GUI page.</p>
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public interface IGuiTCU
{
    /**
     * <p>Returns the container on GUI construction to be used.</p>
     * <p>If a container instance is returned, the TCU GUI will be a container GUI.</p>
     * <p>If <tt>null</tt> is returned, the TCU GUI will be a "regular" GUI.</p>
     *
     * @return A container to be used or <tt>null</tt>, if this is not a container GUI.
     */
    default Container getContainer(EntityPlayer player, ITurretInst turretInst) { return null; }

    /**
     * <p>Initializes this page upon opening the GUI or when changing resolution.</p>
     *
     * @param gui The TCU GUI instance.
     */
    void initialize(IGuiTcuInst<?> gui);

    /**
     * <p>Invoked every world tick (every 1/20th of a second) to update values for this page that aren't needed to be updated every frame.</p>
     *
     * @param gui The TCU GUI instance.
     */
    default void updateScreen(IGuiTcuInst<?> gui) {}

    /**
     * <p>Draws the background elements of this page.</p>
     *
     * @param gui The TCU GUI instance.
     * @param partialTicks The partial render tick amount.
     * @param mouseX The X position of the mouse cursor.
     * @param mouseY The Y position of the mouse cursor.
     */
    @Deprecated
    void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY);

    /**
     * <p>Draws the foreground elements of this page.</p>
     *
     * @param gui The TCU GUI instance.
     * @param mouseX The X position of the mouse cursor.
     * @param mouseY The Y position of the mouse cursor.
     */
    @Deprecated
    default void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {}

    /**
     * <p>Invoked when a {@link GuiButton} on this page is clicked.</p>
     *
     * @param gui The TCU GUI instance.
     * @param button The button clicked.
     * @throws IOException if the operation tries opening an url and fails to do so.
     */
    @Deprecated
    default void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {}

    /**
     * <p>Invoked once a mouse button is pressed.</p>
     *
     * @param gui The TCU GUI instance.
     * @param mouseX The X position of the mouse cursor.
     * @param mouseY The Y position of the mouse cursor.
     * @param mouseButton The mouse button pressed.
     */
    @Deprecated
    default void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) {}

    /**
     * <p>Indicates wether this page can intercept a keystroke and prevents further keybind operations.</p>
     *
     * @param gui The TCU GUI instance.
     * @param typedChar The character representing the key typed.
     * @param keyCode The keycode of the key typed.
     * @return <tt>true</tt>, if the keystroke is intercepted, <tt>false</tt> otherwise.
     */
    default boolean doKeyIntercept(IGuiTcuInst<?> gui, char typedChar, int keyCode) { return false; }

    /**
     * <p>Invoked when a key is typed and is not intercepted.</p>
     *
     * @param gui The TCU GUI instance.
     * @param typedChar The character representing the key typed.
     * @param keyCode The keycode of the key typed.
     */
    default void onKeyType(IGuiTcuInst<?> gui, char typedChar, int keyCode) {}

    /**
     * <p>Invoked once the TCU GUI is closed.</p>
     * <p><b>This is not invoked when the page changes.</b></p>
     * @param gui The TCU GUI instance.
     */
    default void onGuiClose(IGuiTcuInst<?> gui) {}

    /**
     * <p>Invoked when there's input occurring from the mouse, like mouse click, mouse move, mouse release, etc.</p>
     * @param gui The TCU GUI instance.
     */
    @Deprecated
    default void onMouseInput(IGuiTcuInst<?> gui) {}

    /**
     * <p>Invoked when the mouse is/has been clicked and is moved.</p>
     *
     * @param gui The TCU GUI instance.
     * @param mouseX The X position of the mouse cursor.
     * @param mouseY The Y position of the mouse cursor.
     * @param mouseButton The mouse button pressed.
     * @param timeSinceLastClick The amount of milliseconds passed since the last click.
     */
    @Deprecated
    default void onMouseClickMove(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {}

    ResourceLocation getGuiDefinition();

    default void onElementAction(IGuiElement element, int action) {}
}
