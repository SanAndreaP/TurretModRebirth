/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.sanlib.lib.client.gui.element.DynamicText;
import de.sanandrew.mods.sanlib.lib.client.gui.element.EnergyStorageBar;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteGeneratorEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class ElectrolyteGeneratorScreen
        extends JsonGuiContainer<ElectrolyteGeneratorContainer>
        implements EnergyStorageBar.IGuiEnergyContainer, ContainerName.IContainerName, ElectrolyteBar.IElectrolyteInfo, DynamicText.IGuiDynamicText
{
    public ElectrolyteGeneratorScreen(ElectrolyteGeneratorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_ELECTROLYTE);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    public int getEnergy() {
        return this.menu.data.getEnergyStored();
    }

    @Override
    public int getMaxEnergy() {
        return ElectrolyteGeneratorEntity.MAX_FLUX_STORAGE;
    }

    @Override
    public ITextComponent getContainerName() {
        return this.getTitle();
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
    public ITextComponent getText(String key, ITextComponent originalText) {
        if( "efficiency".equalsIgnoreCase(key) ) {
            return new StringTextComponent(String.format("%.2f%%", this.menu.data.getEfficiency() / 9.0F * 100.0F));
        } else if( "powergen".equalsIgnoreCase(key) ) {
            return new StringTextComponent(String.format("%d RF/t", this.menu.data.getEnergyGenerated()));
        }

        return null;
    }
}
