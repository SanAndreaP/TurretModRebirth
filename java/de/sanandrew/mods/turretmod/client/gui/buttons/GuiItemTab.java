/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.buttons;

import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiItemTab extends GuiButton {

	protected ItemStack renderedItem;

    protected static RenderItem itemRenderer = new RenderItem();

    protected boolean isRight;

	public GuiItemTab(int id, int posX, int posY, ItemStack renderedStack, String hoverText, boolean onTheRight) {
		super(id, posX, posY, hoverText);
		this.width = 26;
		this.height = 26;
		this.renderedItem = renderedStack;
		this.isRight = onTheRight;
	}

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if( this.visible ) {
        	FontRenderer var4 = par1Minecraft.fontRenderer;
            par1Minecraft.renderEngine.bindTexture(EnumTextures.GUI_BUTTONS.getResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 26 * (isRight ? 0 : 1), hoverState * 26, this.width, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);

            this.drawItemStack(this.renderedItem, this.xPosition + 5, this.yPosition + 5, var4, par1Minecraft);

            if( this.field_146123_n ) {
                this.drawTabHoveringText(this.displayString, this.xPosition - (this.isRight ? var4.getStringWidth(this.displayString) + 5 : -5), this.yPosition + 21,
                                         var4);
            }

            RenderHelper.disableStandardItemLighting();
        }
    }

    protected void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int textWidth = fontRenderer.getStringWidth(text);
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 8;
        this.zLevel = 300.0F;
        itemRenderer.zLevel = 300.0F;
        int bkgColor = 0xF0100010;
        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + height + 3, bkgColor, bkgColor);
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;
        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg, darkBg);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        fontRenderer.drawStringWithShadow(text, xPos, yPos, -1);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    private void drawItemStack(ItemStack stack, int posX, int posY, FontRenderer fontRenderer, Minecraft mc) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, stack, posX, posY);
        itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, stack, posX, posY);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
    }
}
