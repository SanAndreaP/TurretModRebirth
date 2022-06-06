package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TcuUpgradesPage
        extends JsonTcuPage
{
    GuiElementInst[] upgDisabledElements = new GuiElementInst[3];

    public TcuUpgradesPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_UPGRADES);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        this.upgDisabledElements[0] = this.guiDefinition.getElementById("hideUpgrades1");
        this.upgDisabledElements[1] = this.guiDefinition.getElementById("hideUpgrades2");
        this.upgDisabledElements[2] = this.guiDefinition.getElementById("hideUpgrades3");

        this.updateDisabledSlots();
    }

    @Override
    public void tick() {
        super.tick();

        this.updateDisabledSlots();
    }

    private void updateDisabledSlots() {
        this.upgDisabledElements[0].setVisible(!this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_I));
        this.upgDisabledElements[1].setVisible(!this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_II));
        this.upgDisabledElements[2].setVisible(!this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_III));
    }
}
