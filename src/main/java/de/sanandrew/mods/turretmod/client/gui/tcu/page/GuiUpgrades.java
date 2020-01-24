/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiUpgrades
        implements IGuiTCU
{
    private IGuiElement hideUpgrades1;
    private IGuiElement hideUpgrades2;
    private IGuiElement hideUpgrades3;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.hideUpgrades1 = gui.getDefinition().getElementById("hideUpgrades1").get();
        this.hideUpgrades2 = gui.getDefinition().getElementById("hideUpgrades2").get();
        this.hideUpgrades3 = gui.getDefinition().getElementById("hideUpgrades3").get();
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_UPGRADES.resource;
    }

    @Override
    public Container getContainer(EntityPlayer player, ITurretInst turretInst) {
        return new ContainerTurretUpgrades(player.inventory, (UpgradeProcessor) turretInst.getUpgradeProcessor());
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        IUpgradeProcessor upgProc = gui.getTurretInst().getUpgradeProcessor();

        this.hideUpgrades1.setVisible(!upgProc.hasUpgrade(Upgrades.UPG_STORAGE_I));
        this.hideUpgrades2.setVisible(!upgProc.hasUpgrade(Upgrades.UPG_STORAGE_II));
        this.hideUpgrades3.setVisible(!upgProc.hasUpgrade(Upgrades.UPG_STORAGE_III));
    }
}
