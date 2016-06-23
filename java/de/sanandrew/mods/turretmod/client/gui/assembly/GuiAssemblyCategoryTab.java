/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiAssemblyCategoryTab
        extends GuiButton
{
	protected ItemStack renderedItem;

	public GuiAssemblyCategoryTab(int id, int posX, int posY, ItemStack renderedItem, String hoverText) {
		super(id, posX, posY, hoverText);
		this.width = 20;
		this.height = 14;
		this.renderedItem = renderedItem;
	}

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if( this.visible ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            mc.renderEngine.bindTexture(Resources.GUI_ASSEMBLY_CRF.getResource());

            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.hovered);

            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 50 + 20 * hoverState, 222, this.width, this.height);

            this.mouseDragged(mc, mouseX, mouseY);

            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();

            GlStateManager.enableDepth();
            TmrClientUtils.renderStackInGui(this.renderedItem, this.xPosition + 9, this.yPosition + 3, 0.5F);
            GlStateManager.disableDepth();

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();

            if( this.hovered ) {
                this.drawTabHoveringText(this.displayString, this.xPosition + 5, this.yPosition + 15, mc.fontRendererObj);
            }

            GlStateManager.disableBlend();
            RenderHelper.enableGUIStandardItemLighting();
        }
    }

    protected void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();

        int textWidth = fontRenderer.getStringWidth(text);
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 8;

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        this.zLevel = 300.0F;
        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + height + 3, bkgColor, bkgColor);

        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg, darkBg);
        this.zLevel = 0.0F;

        GlStateManager.disableDepth();

        fontRenderer.drawStringWithShadow(text, xPos, yPos, -1);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }
}
