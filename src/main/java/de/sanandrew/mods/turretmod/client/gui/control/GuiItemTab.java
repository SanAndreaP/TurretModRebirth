/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.control;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class GuiItemTab
        extends GuiButton
{
	@Nonnull
	private ItemStack renderedItem;
    private boolean isRight;

	public GuiItemTab(int id, int posX, int posY, @Nonnull ItemStack renderedItem, String hoverText, boolean onTheRight) {
		super(id, posX, posY, hoverText);
		this.width = 26;
		this.height = 26;
		this.renderedItem = renderedItem;
		this.isRight = onTheRight;
	}

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(Resources.GUI_BUTTONS.getResource());
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered);
            this.drawTexturedModalRect(this.x, this.y, 26 * (isRight ? 0 : 1), hoverState * 26, this.width, this.height);
            GlStateManager.disableBlend();

            GlStateManager.enableDepth();
            RenderUtils.renderStackInGui(this.renderedItem, this.x + 5, this.y + 5, 1.0F);

            if( this.hovered ) {
                this.drawTabHoveringText(this.displayString, this.x - (this.isRight ? mc.fontRenderer.getStringWidth(this.displayString) + 5 : -5), this.y + 21, mc.fontRenderer);
            }
        }
    }

    private void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 400.0F);

        GlStateManager.enableDepth();

        int textWidth = fontRenderer.getStringWidth(text);
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 8;

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + height + 3, bkgColor, bkgColor);

        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg, darkBg);

        GlStateManager.disableDepth();
        fontRenderer.drawStringWithShadow(text, xPos, yPos, -1);
        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }
}
