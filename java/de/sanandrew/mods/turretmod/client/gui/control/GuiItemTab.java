/**
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
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiItemTab
        extends GuiButton
{
	protected ItemStack renderedItem;
    protected static RenderItem itemRenderer = new RenderItem();
    protected boolean isRight;

	public GuiItemTab(int id, int posX, int posY, ItemStack renderedItem, String hoverText, boolean onTheRight) {
		super(id, posX, posY, hoverText);
		this.width = 26;
		this.height = 26;
		this.renderedItem = renderedItem;
		this.isRight = onTheRight;
	}

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if( this.visible ) {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            mc.renderEngine.bindTexture(Resources.GUI_BUTTONS.getResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 26 * (isRight ? 0 : 1), hoverState * 26, this.width, this.height);
            GL11.glDisable(GL11.GL_BLEND);

            drawItemStack(this.renderedItem, this.xPosition + 5, this.yPosition + 5, mc);

            if( this.field_146123_n ) {
                this.drawTabHoveringText(this.displayString, this.xPosition - (this.isRight ? mc.fontRenderer.getStringWidth(this.displayString) + 5 : -5),
                                         this.yPosition + 21, mc.fontRenderer);
            }
        }
    }

    protected void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 64.0F);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

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

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        fontRenderer.drawStringWithShadow(text, xPos, yPos, -1);

        GL11.glPopMatrix();
    }

    private static void drawItemStack(ItemStack stack, int x, int y, Minecraft mc) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.zLevel = -50.0F;
        itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
        itemRenderer.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslatef(0.0F, 0.0F, -32.0F);
    }
}
