/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.RedstoneFluxBar;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.tileentity.electrolytegen.TileEntityElectrolyteGenerator;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiTurretAssemblyNEW
        extends GuiContainer
        implements IGui, RedstoneFluxBar.IGuiEnergyContainer, ContainerName.IContainerName, DynamicText.IGuiDynamicText
{
    private final TileEntityTurretAssembly assembly;
    private int currEnergy;
    private int maxEnergy;
    private float currPartTicks;

    private GuiDefinition guiDef;

    public IAssemblyRecipe hoveredRecipe;
    public String currGroup;

    public GuiTurretAssemblyNEW(InventoryPlayer invPlayer, TileEntityTurretAssembly tile) {
        super(new ContainerTurretAssembly(invPlayer, tile));

        this.assembly = tile;

        try {
            this.guiDef = GuiDefinition.getNewDefinition(Resources.GUI_STRUCT_ASSEMBLY.resource);
            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiHelper.initGuiDef(this.guiDef, this);
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

        IEnergyStorage stg = this.assembly.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if( stg != null ) {
            this.currEnergy = stg.getEnergyStored();
            this.maxEnergy = stg.getMaxEnergyStored();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        this.currPartTicks = partTicks;
        GuiHelper.drawGDBackground(this.guiDef, this, partTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        this.guiDef.drawForeground(this, mouseX, mouseY, this.currPartTicks);
        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiDef.handleMouseInput(this);
    }

    @Override
    public int getEnergy() {
        return this.currEnergy;
    }

    @Override
    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    @Override
    public GuiScreen get() {
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
        return "";//this.assembly.hasCustomName() ? this.assembly.getName() : LangUtils.translate(this.assembly.getName());
    }

    @Override
    public float getZLevel() {
        return this.zLevel;
    }

    @Override
    public float setZLevel(float newZ) {
        float origZ = this.zLevel;
        this.zLevel = newZ;
        return origZ;
    }

    @Override
    public String getText(String key, String originalText) {
//        if( "efficiency".equalsIgnoreCase(key) ) {
//            return String.format("%.2f%%", this.currEffective / 9.0F * 100.0F);
//        } else if( "powergen".equalsIgnoreCase(key) ) {
//            return String.format("%d RF/t", this.generatedEnergy);
//        }
        return null;
    }
}
