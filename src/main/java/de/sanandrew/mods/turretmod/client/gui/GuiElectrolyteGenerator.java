/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.EnergyStorageBar;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.init.Resources;
import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiElectrolyteGenerator
        extends ContainerScreen<ContainerElectrolyteGenerator>
        implements IGui, EnergyStorageBar.IGuiEnergyContainer, ContainerName.IContainerName, ElectrolyteBar.IGuiElectrolyte, DynamicText.IGuiDynamicText
{
//    private int currEnergy;
//    private int maxEnergy;
//    private float currEffective;
//    private int generatedEnergy;
    private float currPartTicks;

    private GuiDefinition guiDef;

    public GuiElectrolyteGenerator(ContainerElectrolyteGenerator container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);

        try {
            this.guiDef = GuiDefinition.getNewDefinition(Resources.GUI_STRUCT_ELECTROLYTE.resource);
            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }
    }


    @Override
    protected void init() {
        super.init();

        ClientProxy.initGuiDef(this.guiDef, this);

        this.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();

//        IEnergyStorage stg = this.generator.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
//        if( stg != null ) {
//            this.currEnergy = stg.getEnergyStored();
//            this.maxEnergy = stg.getMaxEnergyStored();
//        }
//
//        this.currEffective = this.generator.efficiency;
//        this.generatedEnergy = this.generator.getGeneratedFlux();

        this.guiDef.update(this);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.currPartTicks = partialTicks;
        ClientProxy.drawGDBackground(this.guiDef, matrixStack, this, partialTicks, x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
//        RenderHelper.disableStandardItemLighting();
        this.guiDef.drawForeground(this, matrixStack, x, y, this.currPartTicks);
//        RenderHelper.enableStandardItemLighting();
    }

//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        RenderHelper.disableStandardItemLighting();
//        this.guiDef.drawForeground(this, mouseX, mouseY, this.currPartTicks);
//        RenderHelper.enableGUIStandardItemLighting();
//    }

    @Override
    public int getEnergy() {
        return this.container.data.getEnergyStored();
    }

    @Override
    public int getMaxEnergy() {
        return TileEntityElectrolyteGenerator.MAX_FLUX_STORAGE;
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public int getScreenPosX() {
        return this.guiLeft;
    }

    @Override
    public int getScreenPosY() {
        return this.guiTop;
    }

    @Override
    public String getContainerName() {
        return this.getTitle().getString();
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getProgress(int slot) {
        return this.container.data.getProgress(slot);
    }

    @Override
    public int getMaxProgress(int slot) {
        return this.container.data.getMaxProgress(slot);
    }

    @Override
    public String getText(String key, String originalText) {
        if( "efficiency".equalsIgnoreCase(key) ) {
            return String.format("%.2f%%", this.container.data.getEfficiency() / 9.0F * 100.0F);
        } else if( "powergen".equalsIgnoreCase(key) ) {
            return String.format("%d RF/t", this.container.data.getEnergyGenerated());
        }
        return null;
    }
}
