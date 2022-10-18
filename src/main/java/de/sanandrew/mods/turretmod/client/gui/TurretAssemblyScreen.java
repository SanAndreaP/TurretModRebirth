/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.inventory.container.TurretAssemblyContainer;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyEnergyStorage;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

@SuppressWarnings("java:S110")
public class TurretAssemblyScreen
        extends JsonGuiContainer<TurretAssemblyContainer>
{
    private static String lastGroup;
    private String group;

    private ButtonSL cancelButton;
    private ButtonSL automateButton;
    private ButtonSL manualButton;

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
        this.setGroup(MiscUtils.apply(this.getCurrentRecipe(), IRecipe::getGroup, Strings.isNullOrEmpty(lastGroup)
                                                                                 ? AssemblyManager.INSTANCE.getGroups(this.getLevel())[0]
                                                                                 : lastGroup));
        this.cancelButton = this.guiDefinition.getElementById("cancel-button").get(ButtonSL.class);
        this.automateButton = this.guiDefinition.getElementById("automate-button").get(ButtonSL.class);
        this.manualButton = this.guiDefinition.getElementById("manual-button").get(ButtonSL.class);
        this.updateButtons();

        this.guiDefinition.getElementById("energy").get(ProgressBar.class)
                          .setPercentFunc(p -> this.menu.data.getEnergyStored() / (double) AssemblyEnergyStorage.MAX_FLUX_STORAGE);
        this.guiDefinition.getElementById("energy_tooltip").get(Text.class)
                          .setTextFunc((g, t) -> new StringTextComponent(String.format("%d / %d RF", this.menu.data.getEnergyStored(), AssemblyEnergyStorage.MAX_FLUX_STORAGE)));
        this.guiDefinition.getElementById("group-icon").get(Item.class)
                          .setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.group));

        MiscUtils.accept(this.guiDefinition.getElementById("prev-tab").get(ButtonSL.class), btn -> {
            btn.setFunction(b -> this.setGroup(this.getPrevGroup()));
            btn.get(ButtonSL.LABEL).get(Item.class).setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.getPrevGroup()));
        });
        MiscUtils.accept(this.guiDefinition.getElementById("next-tab").get(ButtonSL.class), btn -> {
            btn.setFunction(b -> this.setGroup(this.getNextGroup()));
            btn.get(ButtonSL.LABEL).get(Item.class).setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.getNextGroup()));
        });
    }

    @Override
    public void tick() {
        super.tick();

        this.updateButtons();
    }

    private void updateButtons() {
        this.cancelButton.setActive(this.menu.hasCurrentRecipe());
        this.automateButton.setVisible(this.menu.hasAutoUpgrade());
        this.automateButton.setActive(!this.menu.isAutomated());
        this.manualButton.setVisible(this.menu.hasAutoUpgrade());
        this.manualButton.setActive(this.menu.isAutomated());
    }

    private String getPrevGroup() {
        return AssemblyManager.INSTANCE.getPreviousGroup(this.getLevel(), this.group);
    }

    private String getNextGroup() {
        return AssemblyManager.INSTANCE.getPreviousGroup(this.getLevel(), this.group);
    }

    private World getLevel() {
        return this.getMinecraft().level;
    }

    private void setGroup(String group) {
        if( !Strings.isNullOrEmpty(group) ) {
            this.group = group;
            updateLastGroup(group);
        }
    }

    private static synchronized void updateLastGroup(String group) {
        lastGroup = group;
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
