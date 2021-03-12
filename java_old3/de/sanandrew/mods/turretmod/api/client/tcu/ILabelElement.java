/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * <p>An object defining a label element.</p>
 */
@OnlyIn(Dist.CLIENT)
public interface ILabelElement
{
    /**
     * <p>Indicates wether this label element should be shown.</p>
     *
     * @param turretInst The turret instance the label is applied to.
     * @return <tt>true</tt>, if the label should be shown; <tt>false</tt> otherwise.
     */
    boolean showElement(ITurretInst turretInst);

    /**
     * <p>Returns the height of this label element.</p>
     *
     * @param turretInst The turret instance the label is applied to.
     * @param fontRenderer The standard font renderer.
     * @return the height in pixels.
     */
    float getHeight(ITurretInst turretInst, FontRenderer fontRenderer);

    /**
     * <p>Returns the width of this label element.</p>
     *
     * @param turretInst The turret instance the label is applied to.
     * @param stdFontRenderer The standard font renderer.
     * @return The width in pixels.
     */
    float getWidth(ITurretInst turretInst, FontRenderer stdFontRenderer);

    /**
     * <p>Invoked when the label is rendering in quad mode.</p>
     * <p>This label element should only add vertices to the given <tt>BufferBuilder</tt>, as the label will draw that once every frame.</p>
     *
     * @param turretInst The turret instance the label is applied to.
     * @param maxWidth The maximum width of the rendered label content, in pixels.
     * @param progress The current progress on the animation.
     * @param fontRenderer The standard font renderer.
     * @param currHeight The height the label currently has, in pixels.
     * @param buffer The BufferBuilder used to draw quads.
     */
    default void renderQuads(ITurretInst turretInst, float maxWidth, float progress, FontRenderer fontRenderer, float currHeight, BufferBuilder buffer) { }

    /**
     * <p>Invoked when the label is rendering in textured mode.</p>
     *
     * @param turretInst The turret instance the label is applied to.
     * @param maxWidth The maximum width of the rendered label content, in pixels.
     * @param progress The current progress on the animation.
     * @param fontRenderer The standard font renderer.
     */
    default void renderTextured(ITurretInst turretInst, float maxWidth, float progress, FontRenderer fontRenderer) { }

    /**
     * <p>Returns the positional priority of this label element.</p>
     * <p>Higher values result in a higher location on the label.</p>
     *
     * @return the positional priority.
     */
    default int getPriority() {
        return 0;
    }
}
