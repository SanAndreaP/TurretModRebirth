/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.client.util.ShaderHelper;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class GuiButtonTcuTab
        extends GuiButton
{
	@Nonnull
	private final ItemStack renderedItem;
	private final int textureOffset;
	private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(TextureMap.LOCATION_BLOCKS_TEXTURE);

	public GuiButtonTcuTab(int id, int posX, int posY, @Nonnull ItemStack renderedItem, String hoverText) {
		super(id, posX, posY, hoverText);
		this.width = 18;
		this.height = 18;
		this.renderedItem = renderedItem;
		this.textureOffset = 0;
	}

    public GuiButtonTcuTab(int id, int posX, int posY, int textureOffset, String hoverText) {
        super(id, posX, posY, hoverText);
        this.width = 18;
        this.height = 18;
        this.renderedItem = ItemStackUtils.getEmpty();
        this.textureOffset = textureOffset;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
        if( this.visible ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(Resources.GUI_TCU_BUTTONS.getResource());
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered);
            if( ItemStackUtils.isValid(renderedItem) ) {
                if( hoverState != 1 ) {
                    this.drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
                }
            } else {
                this.drawTexturedModalRect(this.x, this.y, this.textureOffset, hoverState * 18, this.width, this.height);
            }
            GlStateManager.disableBlend();

            if( ItemStackUtils.isValid(renderedItem) ) {
                GlStateManager.enableDepth();
                if( hoverState != 0 ) {
                    SHADER_GRAYSCALE.render(() -> RenderUtils.renderStackInGui(this.renderedItem, this.x + 1, this.y + 1, 1.0F), 1.0F);
                } else {
                    RenderUtils.renderStackInGui(this.renderedItem, this.x + 1, this.y + 1, 1.0F);
                }
            }

            if( this.hovered ) {
                this.drawTabHoveringText(this.displayString, mouseX, mouseY, mc.fontRenderer);
            }
        }
    }

    private void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 400.0F);
        GlStateManager.disableRescaleNormal();
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

        GlStateManager.enableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
