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
import de.sanandrew.mods.turretmod.api.ResourceLocations;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.inventory.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorTileEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class ElectrolyteGeneratorScreen
        extends ContainerScreen<ElectrolyteGeneratorContainer>
        implements IGui, EnergyStorageBar.IGuiEnergyContainer, ContainerName.IContainerName, ElectrolyteBar.IElectrolyteInfo, DynamicText.IGuiDynamicText
{
    private float currPartTicks;

    private GuiDefinition guiDef;

    public ElectrolyteGeneratorScreen(ElectrolyteGeneratorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);

        try {
            this.guiDef = GuiDefinition.getNewDefinition(ResourceLocations.MODEL_GUI_ELECTROLYTE);
            this.width = this.guiDef.width;
            this.height = this.guiDef.height;
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
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();

        this.guiDef.update(this);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.currPartTicks = partialTicks;
        ClientProxy.drawGDBackground(this.guiDef, matrixStack, this, partialTicks, x, y);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int x, int y) {
        this.guiDef.drawForeground(this, matrixStack, x, y, this.currPartTicks);
    }

    @Override
    public int getEnergy() {
        return this.menu.data.getEnergyStored();
    }

    @Override
    public int getMaxEnergy() {
        return ElectrolyteGeneratorTileEntity.MAX_FLUX_STORAGE;
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public int getScreenPosX() {
        return this.leftPos;
    }

    @Override
    public int getScreenPosY() {
        return this.topPos;
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
        return this.menu.data.getProgress(slot);
    }

    @Override
    public int getMaxProgress(int slot) {
        return this.menu.data.getMaxProgress(slot);
    }

    @Override
    public String getText(String key, String originalText) {
        if( "efficiency".equalsIgnoreCase(key) ) {
            return String.format("%.2f%%", this.menu.data.getEfficiency() / 9.0F * 100.0F);
        } else if( "powergen".equalsIgnoreCase(key) ) {
            return String.format("%d RF/t", this.menu.data.getEnergyGenerated());
        }
        return null;
    }
}
