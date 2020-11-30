/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

/**
 * A Turret Control Unit GUI instance.
 * @param <T> The type of GUI
 */
@SuppressWarnings("JavadocReference")
public interface IGuiTcuInst<T extends GuiScreen>
        extends IGui
{
    /**
     * @return the {@link GuiScreen} instance of this GUI.
     */
    T getGui();

    /**
     * @return the turret instance associated with this GUI.
     */
    ITurretInst getTurretInst();

    /**
     * @return the X position of this GUI.
     */
    int getPosX();

    /**
     * @return the Y position of this GUI.
     */
    int getPosY();

    /**
     * @return the width of this GUI.
     */
    int getWidth();

    /**
     * @return the height of this GUI.
     */
    int getHeight();

    /**
     * <p>Returns wether the player opening this GUI has permission to do so.</p>
     *
     * @return <tt>true</tt>, if the player has the appropriate permission; <tt>false</tt> otherwise.
     */
    default boolean hasPermision() {
        Minecraft mc = this.get().mc;
        return (ItemStackUtils.isItem(mc.player.getHeldItemMainhand(), ItemRegistry.TURRET_CONTROL_UNIT)
                    || ItemStackUtils.isItem(mc.player.getHeldItemOffhand(), ItemRegistry.TURRET_CONTROL_UNIT))
               && this.getTurretInst().hasPlayerPermission(mc.player);
    }

    /**
     * @return the font renderer associated with this GUI.
     */
    FontRenderer getFontRenderer();

    /**
     * <p>Draws a rectangle with a vertical gradient between the specified colors (ARGB format)</p>
     *
     * @param left The horizontal position of the left edge.
     * @param top The vertical position of the top edge.
     * @param right The horizontal position of the right edge.
     * @param bottom The vertical position of the bottom edge.
     * @param startColor The color at the beginning of the gradient.
     * @param endColor The color at the end of the gradient.
     * @see net.minecraft.client.gui.Gui#drawGradientRect(int, int, int, int, int, int)
     */
    void drawGradient(int left, int top, int right, int bottom, int startColor, int endColor);

    /**
     * @return the key of the current TCU GUI page.
     */
    ResourceLocation getCurrentPageKey();

    boolean isRemote();

    boolean canRemoteTransfer();
}
