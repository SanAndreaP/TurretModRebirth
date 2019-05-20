/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.control;

import com.google.common.base.Strings;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public final class GuiButtonIcon
        extends GuiButton
{
    private final ResourceLocation texture;
    private final int textureX;
    private final int textureY;

    public GuiButtonIcon(int buttonId, int x, int y, int texX, int texY, ResourceLocation texture, String buttonText) {
        super(buttonId, x, y, 18, 18, buttonText);
        this.textureX = texX;
        this.textureY = texY;
        this.texture = texture;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered) - 1;

            mc.getTextureManager().bindTexture(this.texture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY + hoverState * this.height, this.width, this.height);

            if( this.hovered && !Strings.isNullOrEmpty(this.displayString) ) {
                drawTabHoveringText(this.displayString, mouseX, mouseY, mc.fontRenderer);
            }
        }
    }

    private static void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 400.0F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableDepth();

        String[] lines = text.split("\\\\n");
        int textWidth = Arrays.stream(lines).reduce(0, (i, s) -> Math.max(fontRenderer.getStringWidth(s), i), Math::max);
        int textHeight = lines.length * fontRenderer.FONT_HEIGHT + (2 * (lines.length - 1)) - lines.length;
        int xPos = mouseX + 12;
        int yPos = mouseY - 4 - textHeight;

        GuiHelper.drawTooltipBg(xPos, yPos, textWidth, textHeight);
//        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
//        this.drawGradientRect(xPos - 3, yPos + textHeight + 3, xPos + textWidth + 3, yPos + textHeight + 4, bkgColor, bkgColor);
//        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + textHeight + 3, bkgColor, bkgColor);
//        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + textHeight + 3, bkgColor, bkgColor);
//        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + textHeight + 3, bkgColor, bkgColor);
//
//        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + textHeight + 3 - 1, lightBg, darkBg);
//        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + textHeight + 3 - 1, lightBg, darkBg);
//        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
//        this.drawGradientRect(xPos - 3, yPos + textHeight + 2, xPos + textWidth + 3, yPos + textHeight + 3, darkBg, darkBg);

        GlStateManager.disableDepth();
        for( int i = 0; i < lines.length; i++ ) {
            fontRenderer.drawStringWithShadow(lines[i], xPos, yPos + i * (fontRenderer.FONT_HEIGHT + 1), -1);
        }
        GlStateManager.enableDepth();

        GlStateManager.enableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
