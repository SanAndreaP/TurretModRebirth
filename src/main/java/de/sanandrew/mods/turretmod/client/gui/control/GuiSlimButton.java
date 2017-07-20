/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.control;

import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSlimButton
        extends GuiButton
{
    private static FontRenderer slimFont;

    public GuiSlimButton(int id, int x, int y, int width, String text) {
        super(id, x, y, width, 12, text);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            int center = this.width / 2;
            int textColor = 0xE0E0E0;

            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered);

            if( this.packedFGColour != 0 ) {
                textColor = this.packedFGColour;
            } else if( !this.enabled ) {
                textColor = 0xA0A0A0;
            } else if( this.hovered ) {
                textColor = 0xFFFFA0;
            }

            mc.getTextureManager().bindTexture(Resources.GUI_BUTTONS.getResource());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 78 + hoverState * 12, center, this.height);
            this.drawTexturedModalRect(this.x + center, this.y, 158 - center, 78 + hoverState * 12, center, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + center, this.y + (this.height - 8) / 2, textColor);
            GlStateManager.disableBlend();
        }
    }
}
