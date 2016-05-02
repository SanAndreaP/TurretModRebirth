/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.control;

import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GuiSlimButton
        extends GuiButton
{
    private static FontRenderer slimFont;

    public GuiSlimButton(int id, int x, int y, int width, String text) {
        super(id, x, y, width, 12, text);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if( this.visible ) {
            int center = this.width / 2;
            int textColor = 0xE0E0E0;

            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);

            if( this.packedFGColour != 0 ) {
                textColor = this.packedFGColour;
            } else if( !this.enabled ) {
                textColor = 0xA0A0A0;
            } else if( this.field_146123_n ) {
                textColor = 0xFFFFA0;
            }

            mc.getTextureManager().bindTexture(Textures.GUI_BUTTONS.getResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 78 + hoverState * 12, center, this.height);
            this.drawTexturedModalRect(this.xPosition + center, this.yPosition, 158 - center, 78 + hoverState * 12, center, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawCenteredString(mc.fontRenderer, this.displayString, this.xPosition + center, this.yPosition + (this.height - 8) / 2, textColor);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
