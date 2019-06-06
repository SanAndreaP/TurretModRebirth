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
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.RedstoneFluxBar;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.element.Button;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.inventory.ContainerElectrolyteGenerator;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketAssemblyToggleAutomate;
import de.sanandrew.mods.turretmod.network.PacketInitAssemblyCrafting;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
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
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiTurretAssemblyNEW
        extends GuiContainer
        implements IGui, RedstoneFluxBar.IGuiEnergyContainer, ContainerName.IContainerName, DynamicText.IGuiDynamicText
{
    private final TileEntityTurretAssembly assembly;
    private int currEnergy;
    private int maxEnergy;
    private float currPartTicks;

    private GuiDefinition guiDef;
    private boolean initializedUpdate;

    public IAssemblyRecipe hoveredRecipe;
    public static String currGroup;

    private Button cancelButton;
    private Button automateButton;
    private Button manualButton;
    private boolean shiftPressed;

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

        this.cancelButton = (Button) this.guiDef.getElementById("cancel-button").get();
        this.automateButton = (Button) this.guiDef.getElementById("automate-button").get();
        this.manualButton = (Button) this.guiDef.getElementById("manual-button").get();

        if( this.assembly.currRecipe != null ) {
            currGroup = this.assembly.currRecipe.getGroup();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveredRecipe = null;

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        if( this.hoveredRecipe != null ) {
            this.renderToolTip(this.hoveredRecipe.getRecipeOutput(), mouseX, mouseY);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        IEnergyStorage stg = this.assembly.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if( stg != null ) {
            this.currEnergy = stg.getEnergyStored();
            this.maxEnergy = stg.getMaxEnergyStored();
        }

        if( this.assembly.currRecipe != null ) {
            this.cancelButton.setEnabled(true);
            this.automateButton.setEnabled(false);
            this.manualButton.setEnabled(false);
        } else {
            this.cancelButton.setEnabled(false);
            this.automateButton.setEnabled(!this.assembly.isAutomated());
            this.manualButton.setEnabled(this.assembly.isAutomated());
        }

        this.automateButton.setVisible(ItemStackUtils.isItem(this.assembly.getInventory().getStackInSlot(1), ItemRegistry.ASSEMBLY_UPG_AUTO));
        this.manualButton.setVisible(this.automateButton.isVisible());

        this.guiDef.update(this);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        if( !this.initializedUpdate ) {
            this.guiDef.update(this);
            this.initializedUpdate = true;
        }

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
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDef.mouseClicked(this, mouseX, mouseY, mouseButton);
    }

    @Override
    public void performAction(IGuiElement element, int action) {
        switch( action ) {
            case -1:
                if( this.hoveredRecipe != null ) {
                    PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.hoveredRecipe.getId(), this.shiftPressed ? 16 : 1));
                }
                break;
            case 0:
                PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly));
                break;
            case 1:
                PacketRegistry.sendToServer(new PacketAssemblyToggleAutomate(this.assembly));
                break;
            case 2:
                switchGroup(-1);
                break;
            case 3:
                switchGroup(1);
                break;
        }
    }

    private void switchGroup(int direction) {
        if( this.assembly.currRecipe == null ) {
            List<String> g = new ArrayList<>(Arrays.asList(AssemblyManager.INSTANCE.getGroups()));
            if( direction < 0 ) {
                Collections.reverse(g);
            }

            int currInd = g.indexOf(currGroup) + 1;
            if( currInd == g.size() ) {
                currInd = 0;
            }
            currGroup = g.get(currInd);
        }
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

    @Override
    public void handleKeyboardInput() throws IOException {
        this.shiftPressed = false;

        super.handleKeyboardInput();
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        this.shiftPressed = keyCode == Keyboard.KEY_LSHIFT;

        super.keyTyped(keyChar, keyCode);
    }

    public IAssemblyRecipe getCurrRecipe() {
        return this.assembly.currRecipe;
    }
}
