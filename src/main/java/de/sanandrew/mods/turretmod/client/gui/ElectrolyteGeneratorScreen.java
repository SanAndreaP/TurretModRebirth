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
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteEnergyStorage;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class ElectrolyteGeneratorScreen
        extends JsonGuiContainer<ElectrolyteGeneratorContainer>
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
    protected void initGd() {
        this.guiDefinition.getElementById("efficiency").get(Text.class).setTextFunc(
                (g, o) -> new StringTextComponent(String.format("%.2f%%", this.menu.data.getEfficiency() / 9.0F * 100.0F))
        );
        this.guiDefinition.getElementById("powergen").get(Text.class).setTextFunc(
                (g, o) -> new StringTextComponent(String.format("%d RF/t", this.menu.data.getEnergyGenerated()))
        );

        for( int i = 0; i < ElectrolyteInventory.INPUT_SLOT_COUNT; i++ ) {
            final int slot = i;
            this.guiDefinition.getElementById(String.format("progress_%d", i + 1)).get(ProgressBar.class)
                              .setPercentFunc(p -> 1.0F - this.menu.data.getProgress(slot) / (double) this.menu.data.getMaxProgress(slot));
        }

        this.guiDefinition.getElementById("energy").get(ProgressBar.class)
                          .setPercentFunc(p -> this.menu.data.getEnergyStored() / (double) ElectrolyteEnergyStorage.MAX_FLUX_STORAGE);

        this.guiDefinition.getElementById("energy_tooltip").get(Text.class)
                          .setTextFunc((g, t) -> new StringTextComponent(String.format("%d / %d RF", this.menu.data.getEnergyStored(), ElectrolyteEnergyStorage.MAX_FLUX_STORAGE)));
    }

    @Nonnull
    @Override
    public ITextComponent getTitle() {
        return super.getTitle();
    }
}
