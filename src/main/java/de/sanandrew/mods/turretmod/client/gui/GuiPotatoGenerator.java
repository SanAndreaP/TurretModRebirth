/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteProcess;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPotatoGenerator
        extends GuiContainer
{
    private TileEntityElectrolyteGenerator generator;
    private int currEnergy;
    private int maxEnergy;
    private float currEffective;
    private int generatedEnergy;

    public GuiPotatoGenerator(InventoryPlayer invPlayer, TileEntityElectrolyteGenerator tile) {
        super(new ContainerElectrolyteGenerator(invPlayer, tile));

        this.generator = tile;
        this.xSize = 176;
        this.ySize = 222;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        IEnergyStorage stg = this.generator.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if( stg != null ) {
            this.currEnergy = stg.getEnergyStored();
            this.maxEnergy = stg.getMaxEnergyStored();
        }

        this.currEffective = this.generator.effectiveness;
        this.generatedEnergy = this.generator.getGeneratedFlux();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Resources.GUI_POTATOGEN.getResource());

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(0, 0, 0, 0, this.xSize, this.ySize);

        for( int i = 0, max = this.generator.processes.length; i < max; i++ ) {
            ElectrolyteProcess proc = this.generator.processes[i];
            if( proc != null ) {
                this.drawTexturedModalRect(8 + i * 18, 61, 176, 59, 16 - (int) Math.round(proc.getProgress() / (float) proc.maxProgress * 16.0D), 3);
            }
        }

        double energyPerc = this.currEnergy / (double) this.maxEnergy;
        int energyBarY = Math.max(0, Math.min(59, MathHelper.ceil((1.0D - energyPerc) * 59.0D)));

        this.drawTexturedModalRect(156, 75 + energyBarY, 176, energyBarY, 12, 59 - energyBarY);

        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();

        String eff = String.format("%.2f%%", this.currEffective / 9.0F * 100.0F);
        this.fontRenderer.drawString(Lang.translate(Lang.ELECTROGEN_EFFECTIVE.get()), 8, 100, 0xFF606060, false);
        this.fontRenderer.drawString(eff, 150 - this.fontRenderer.getStringWidth(eff), 100, 0xFF606060, false);
        String rft = String.format("%d RF/t", this.generatedEnergy);
        this.fontRenderer.drawString(Lang.translate(Lang.ELECTROGEN_POWERGEN.get()), 8, 110, 0xFF606060, false);
        this.fontRenderer.drawString(rft, 150 - this.fontRenderer.getStringWidth(rft), 110, 0xFF606060, false);

        String s = this.generator.hasCustomName() ? this.generator.getName() : Lang.translate(this.generator.getName());
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 0x404040);
        this.fontRenderer.drawString(Lang.translate(Lang.CONTAINER_INV.get()), 8, this.ySize - 96 + 2, 0x404040);

        if( mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 168 && mouseY >= this.guiTop + 75 && mouseY < this.guiTop + 134 ) {
            this.drawRFluxLabel(mouseX - this.guiLeft, mouseY - guiTop);
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void drawRFluxLabel(int mouseX, int mouseY) {
        IEnergyStorage stg = this.generator.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        String amount = String.format("%d / %d RF", stg == null ? 0 : stg.getEnergyStored(), stg == null ? 0 : stg.getMaxEnergyStored());

        int textWidth = this.fontRenderer.getStringWidth(amount);
        int xPos = mouseX - 12 - textWidth;
        int yPos = mouseY - 12;
        byte height = 7;

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        this.zLevel = 400.0F;
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0.0F);

        GlStateManager.disableDepth();
        GlStateManager.translate(0.5F, 0.5F, 0.0F);
        this.fontRenderer.drawString(amount, 0, 0, 0xFF3F3F3F);
        GlStateManager.translate(-0.5F, -0.5F, -0.0F);
        this.fontRenderer.drawString(amount, 0, 0, 0xFFFFFFFF);
        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }
}
