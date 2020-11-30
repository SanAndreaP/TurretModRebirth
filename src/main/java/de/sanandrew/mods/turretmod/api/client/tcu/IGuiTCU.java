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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

/**
 * <p>An object defining a Turret Control Unit GUI page.</p>
 */
@SuppressWarnings({"unused"})
public interface IGuiTCU
{
    /**
     * <p>Initializes this page upon opening the GUI or when changing resolution.</p>
     *
     * @param gui The TCU GUI instance.
     * @param guiDefinition
     */
    void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition);

    /**
     * <p>Invoked every world tick (every 1/20th of a second) to update values for this page that aren't needed to be updated every frame.</p>
     *
     * @param gui The TCU GUI instance.
     */
    default void updateScreen(IGuiTcuInst<?> gui) {}

    ResourceLocation getGuiDefinition();

    default boolean onElementAction(IGuiTcuInst<?> gui, IGuiElement element, int action) { return false; }

    default void guiClosed(IGuiTcuInst<?> gui) { }

    default void keyTyped(IGuiTcuInst<?> gui, char typedChar, int keyCode) { }
}
