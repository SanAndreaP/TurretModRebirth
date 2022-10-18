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
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.inventory.container.TurretAssemblyContainer;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyEnergyStorage;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteEnergyStorage;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteInventory;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class TurretAssemblyScreen
        extends JsonGuiContainer<TurretAssemblyContainer>
{
    private static String lastGroup;
    private String group;

    public TurretAssemblyScreen(TurretAssemblyContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_ASSEMBLY);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        this.group = MiscUtils.apply(this.getCurrentRecipe(), IRecipe::getGroup, Strings.isNullOrEmpty(lastGroup)
                                                                                 ? AssemblyManager.INSTANCE.getGroups(this.getMinecraft().level)[0]
                                                                                 : lastGroup);
        lastGroup = this.group;

        this.guiDefinition.getElementById("energy").get(ProgressBar.class)
                          .setPercentFunc(p -> this.menu.data.getEnergyStored() / (double) AssemblyEnergyStorage.MAX_FLUX_STORAGE);

        this.guiDefinition.getElementById("energy_tooltip").get(Text.class)
                          .setTextFunc((g, t) -> new StringTextComponent(String.format("%d / %d RF", this.menu.data.getEnergyStored(), AssemblyEnergyStorage.MAX_FLUX_STORAGE)));
        this.guiDefinition.getElementById("group-icon").get(Item.class)
                          .setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.group));
    }

    public String getGroup() {
        return this.group;
    }

    @Nullable
    public IAssemblyRecipe getCurrentRecipe() {
        if( this.menu.hasCurrentRecipe() ) {
            return AssemblyManager.INSTANCE.getRecipe(this.getMinecraft().level, this.menu.getCurrentRecipeId());
        }

        return null;
    }

    @Nonnull
    @Override
    public ITextComponent getTitle() {
        return super.getTitle();
    }
}
