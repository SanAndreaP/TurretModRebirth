/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.tcu.TcuContainer;
import dev.sanandrea.mods.turretmod.item.upgrades.Upgrades;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TcuUpgradesPage
        extends JsonTcuPage
{
    GuiElementInst[] upgDisabledElements = new GuiElementInst[4];
    private final boolean isRemote;

    public TcuUpgradesPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
        this.isRemote = ((TcuScreen) tcuScreen).isRemote;
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
        this.upgDisabledElements[0] = this.guiDefinition.getElementById("hideUpgrades0");
        this.upgDisabledElements[1] = this.guiDefinition.getElementById("hideUpgrades1");
        this.upgDisabledElements[2] = this.guiDefinition.getElementById("hideUpgrades2");
        this.upgDisabledElements[3] = this.guiDefinition.getElementById("hideUpgrades3");

        this.updateDisabledSlots();
    }

    @Override
    public void tick() {
        super.tick();

        this.updateDisabledSlots();
    }

    private void updateDisabledSlots() {
        this.upgDisabledElements[0].setVisible(this.isRemote && !this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.REMOTE_ACCESS));
        this.upgDisabledElements[1].setVisible(!this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_I));
        this.upgDisabledElements[2].setVisible(!this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_II));
        this.upgDisabledElements[3].setVisible(!this.turret.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_III));
    }
}
