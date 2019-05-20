/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
class GuiAssemblyCategoryTab
        extends GuiButton
{
	@Nonnull
	private final ItemStack renderedItem;

	GuiAssemblyCategoryTab(int id, int posX, int posY, @Nonnull ItemStack renderedItem, String hoverText) {
		super(id, posX, posY, hoverText);
		this.width = 20;
		this.height = 14;
		this.renderedItem = renderedItem;
	}

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if( this.visible ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            mc.renderEngine.bindTexture(Resources.GUI_ASSEMBLY_CRF.resource);

            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered);

            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x, this.y, 50 + 20 * hoverState, 222, this.width, this.height);

            this.mouseDragged(mc, mouseX, mouseY);

            GlStateManager.enableRescaleNormal();
            RenderHelper.enableGUIStandardItemLighting();

            GlStateManager.enableDepth();
            RenderUtils.renderStackInGui(this.renderedItem, this.x + 9, this.y + 3, 0.5F);
            GlStateManager.disableDepth();

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();

            if( this.hovered ) {
                this.drawTabHoveringText(this.displayString, this.x + 5, this.y + 15, mc.fontRenderer);
            }

            GlStateManager.disableBlend();
            RenderHelper.enableGUIStandardItemLighting();
        }
    }

    private void drawTabHoveringText(String text, int mouseX, int mouseY, FontRenderer fontRenderer) {
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();

        int textWidth = fontRenderer.getStringWidth(text);
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 8;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, 300.0D);
        GuiHelper.drawTooltipBg(xPos, yPos, textWidth, height);
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();

        fontRenderer.drawStringWithShadow(text, xPos, yPos, -1);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }
}
