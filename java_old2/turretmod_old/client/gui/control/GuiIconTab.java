/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.control;

import de.sanandrew.mods.turretmod.client.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiIconTab
        extends GuiButton
{
	protected IIcon renderedIcon;
    protected static RenderItem itemRenderer = new RenderItem();
    protected boolean isRight;

	public GuiIconTab(int id, int posX, int posY, IIcon renderedIcon, String hoverText, boolean onTheRight) {
		super(id, posX, posY, hoverText);
		this.width = 26;
		this.height = 26;
		this.renderedIcon = renderedIcon;
		this.isRight = onTheRight;
	}

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if( this.visible ) {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            mc.renderEngine.bindTexture(Textures.GUI_BUTTONS.getResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 26 * (isRight ? 0 : 1), hoverState * 26, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);

//            de.sanandrew.core.manpack.util.client.helpers.ItemRenderHelper.renderItemInGui(mc, this.renderedItem, this.xPosition + 5, this.yPosition + 5);
//            this.drawItemStack(this.renderedItem, this.xPosition + 5, this.yPosition + 5, var4, par1Minecraft);
            this.drawIcon(this.renderedIcon, this.xPosition + 5, this.yPosition + 5, mc);

            if( this.field_146123_n ) {
                this.drawTabHoveringText(this.displayString, this.xPosition - (this.isRight ? mc.fontRenderer.getStringWidth(this.displayString) + 5 : -5),
                                         this.yPosition + 21, mc.fontRenderer);
            }

            GL11.glDisable(GL11.GL_BLEND);
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

        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    protected void drawIcon(IIcon icon, int posX, int posY, Minecraft mc) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        ResourceLocation resourcelocation = mc.getTextureManager().getResourceLocation(1);
        mc.getTextureManager().bindTexture(resourcelocation);

        if( icon == null ) {
            icon = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        itemRenderer.renderIcon(posX, posY, icon, 16, 16);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
        GL11.glPopMatrix();
    }
}
