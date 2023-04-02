/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.util.ResourceLocation;

public class GuiUpgrades
        implements IGuiTCU
{
    private GuiElementInst hideUpgrades1;
    private GuiElementInst hideUpgrades2;
    private GuiElementInst hideUpgrades3;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.hideUpgrades1 = gui.getDefinition().getElementById("hideUpgrades1");
        this.hideUpgrades2 = gui.getDefinition().getElementById("hideUpgrades2");
        this.hideUpgrades3 = gui.getDefinition().getElementById("hideUpgrades3");
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_UPGRADES.resource;
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        IUpgradeProcessor upgProc = gui.getTurretInst().getUpgradeProcessor();

        this.hideUpgrades1.setVisible(!upgProc.hasUpgrade(Upgrades.UPG_STORAGE_I));
        this.hideUpgrades2.setVisible(!upgProc.hasUpgrade(Upgrades.UPG_STORAGE_II));
        this.hideUpgrades3.setVisible(!upgProc.hasUpgrade(Upgrades.UPG_STORAGE_III));
    }
}
