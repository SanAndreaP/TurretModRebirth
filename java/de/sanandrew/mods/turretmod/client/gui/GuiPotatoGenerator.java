/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.turretmod.inventory.ContainerPotatoGenerator;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiPotatoGenerator
        extends GuiContainer
{
    TileEntityElectrolyteGenerator generator;

    public GuiPotatoGenerator(InventoryPlayer invPlayer, TileEntityElectrolyteGenerator tile) {
        super(new ContainerPotatoGenerator(invPlayer, tile));

        this.generator = tile;
        this.xSize = 176;
        this.ySize = 222;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.guiLeft, this.guiTop, 0.0F);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Resources.GUI_POTATOGEN.getResource());
        this.drawTexturedModalRect(0, 0, 0, 0, this.xSize, this.ySize);

        for( int i = 0; i < this.generator.progress.length; i++ ) {
            this.drawTexturedModalRect(8 + i*18, 61, 176, 59, (int) StrictMath.round(this.generator.progress[i] / (float)this.generator.maxProgress[i] * 16.0D), 3);
        }


        int energy = this.generator.getEnergyStored(ForgeDirection.DOWN);
        int maxEnergy = TileEntityElectrolyteGenerator.MAX_FLUX_STORAGE;

        double energyPerc = energy / (double) maxEnergy;
        int energyBarY = Math.max(0, Math.min(59, MathHelper.ceiling_double_int((1.0D - energyPerc) * 59.0D)));

        this.drawTexturedModalRect(156, 75 + energyBarY, 176, energyBarY, 12, 59 - energyBarY);

        String eff = String.format("%.2f%%", this.generator.effectiveness / 9.0F * 100.0F);
        this.fontRendererObj.drawString(Lang.translate(Lang.ELECTROGEN_EFFECTIVE), 8, 100, 0xFF606060, false);
        this.fontRendererObj.drawString(eff, 150 - this.fontRendererObj.getStringWidth(eff), 100, 0xFF606060, false);
        String rft = String.format("%d RF/t", this.generator.getGeneratedFlux());
        this.fontRendererObj.drawString(Lang.translate(Lang.ELECTROGEN_POWERGEN), 8, 110, 0xFF606060, false);
        this.fontRendererObj.drawString(rft, 150 - this.fontRendererObj.getStringWidth(rft), 110, 0xFF606060, false);

        GL11.glPopMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String s = this.generator.hasCustomInventoryName() ? this.generator.getInventoryName() : Lang.translate(this.generator.getInventoryName());
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(Lang.translate(Lang.CONTAINER_INV), 8, this.ySize - 96 + 2, 0x404040);

        if( mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 168 && mouseY >= this.guiTop + 75 && mouseY < this.guiTop + 134 ) {
            this.drawRFluxLabel(mouseX - this.guiLeft, mouseY - guiTop);
        }
    }

    private void drawRFluxLabel(int mouseX, int mouseY) {
        String amount = String.format("%d / %d RF", this.generator.getEnergyStored(ForgeDirection.DOWN), this.generator.getMaxEnergyStored(ForgeDirection.DOWN));

        int textWidth = this.fontRendererObj.getStringWidth(amount);
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 7;

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.zLevel = 400.0F;
        itemRender.zLevel = 300.0F;
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

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;


        GL11.glPushMatrix();
        GL11.glTranslatef(xPos, yPos, 0.0F);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslatef(0.5F, 0.5F, 0.0F);
        this.fontRendererObj.drawString(amount, 0, 0, 0xFF3F3F3F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.0F);
        this.fontRendererObj.drawString(amount, 0, 0, 0xFFFFFFFF);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }
}
