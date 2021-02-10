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
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.sanlib.lib.client.gui.element.RedstoneFluxBar;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.inventory.AssemblyInventory;
import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketAssemblyToggleAutomate;
import de.sanandrew.mods.turretmod.network.PacketInitAssemblyCrafting;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiTurretAssembly
        extends GuiContainer
        implements IGui, RedstoneFluxBar.IGuiEnergyContainer, ContainerName.IContainerName
{
    public final TileEntityTurretAssembly assembly;
    private int currEnergy;
    private int maxEnergy;
    private float currPartTicks;

    private GuiDefinition guiDef;

    public IAssemblyRecipe hoveredRecipe;
    public int[] hoveredRecipeCoords;
    public int[] currRecipeCoords;
    public static String currGroup;

    private GuiElementInst cancelButton;
    private GuiElementInst automateButton;
    private GuiElementInst manualButton;
    private boolean shiftPressed;

    public GuiTurretAssembly(InventoryPlayer invPlayer, TileEntityTurretAssembly tile) {
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

        this.cancelButton = this.guiDef.getElementById("cancel-button");
        this.automateButton = this.guiDef.getElementById("automate-button");
        this.manualButton = this.guiDef.getElementById("manual-button");

        if( this.assembly.currRecipe != null ) {
            currGroup = this.assembly.currRecipe.getGroup();
        }

        this.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveredRecipe = null;
        this.hoveredRecipeCoords = null;
        this.currRecipeCoords = null;

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

        if( this.assembly.currRecipe != null ) {
            this.cancelButton.get(Button.class).setEnabled(true);
            this.automateButton.get(Button.class).setEnabled(false);
            this.manualButton.get(Button.class).setEnabled(false);
        } else {
            this.cancelButton.get(Button.class).setEnabled(false);
            this.automateButton.get(Button.class).setEnabled(!this.assembly.isAutomated());
            this.manualButton.get(Button.class).setEnabled(this.assembly.isAutomated());
        }

        this.automateButton.setVisible(ItemStackUtils.isItem(this.assembly.getInventory().getStackInSlot(AssemblyInventory.SLOT_UPGRADE_AUTO), ItemRegistry.ASSEMBLY_UPG_AUTO));
        this.manualButton.setVisible(this.automateButton.isVisible());

        this.guiDef.update(this);
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
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDef.mouseClicked(this, mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean performAction(IGuiElement element, int action) {
        switch( action ) {
            case -1:
                if( this.hoveredRecipe != null ) {
                    PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.hoveredRecipe.getId(), this.shiftPressed ? 16 : 1));
                    return true;
                }
                break;
            case 0:
                PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly));
                return true;
            case 1:
                PacketRegistry.sendToServer(new PacketAssemblyToggleAutomate(this.assembly));
                return true;
            case 2:
                switchGroup(-1);
                return true;
            case 3:
                switchGroup(1);
                return true;
        }

        return false;
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
    public void handleKeyboardInput() throws IOException {
        this.shiftPressed = false;

        super.handleKeyboardInput();
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        this.shiftPressed = keyCode == this.mc.gameSettings.keyBindSneak.getKeyCode();

        super.keyTyped(keyChar, keyCode);
    }

    public int getCraftingCount() {
        return this.assembly.currRecipe != null ? (this.assembly.isAutomated() ? Integer.MAX_VALUE : this.assembly.craftingAmount) : 0;
    }

    public int getRfPerTick(IAssemblyRecipe recipe) {
        return recipe != null ? MathHelper.ceil(recipe.getFluxPerTick() * (this.assembly.hasSpeedUpgrade() ? 4.4F : 1.0F)) : 0;
    }

    public int getProcessTime(IAssemblyRecipe recipe) {
        return recipe != null ? MathHelper.floor(recipe.getProcessTime() / (this.assembly.hasSpeedUpgrade() ? 4.0F : 1.0F)) : 0;
    }

    public boolean isShiftPressed() {
        return this.shiftPressed;
    }
}
