/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class GuiUpgrades
//        implements IGuiTCU
{
//    private boolean hasUpgStgI;
//    private boolean hasUpgStgII;
//    private boolean hasUpgStgIII;
//
//    @Override
//    public Container getContainer(EntityPlayer player, ITurretInst turretInst) {
//        return new ContainerTurretUpgrades(player.inventory, (UpgradeProcessor) turretInst.getUpgradeProcessor());
//    }
//
//    @Override
//    public void initialize(IGuiTcuInst<?> gui) {
//
//    }
//
//    @Override
//    public void updateScreen(IGuiTcuInst<?> gui) {
//        this.hasUpgStgI = gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_I);
//        this.hasUpgStgII = gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_II);
//        this.hasUpgStgIII = gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_III);
//    }
//
//    @Override
//    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
//        gui.getGui().mc.getTextureManager().bindTexture(Resources.GUI_TCU_UPGRADES.resource);
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        gui.getGui().drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getWidth(), gui.getHeight());
//    }
//
//    @Override
//    public void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {
//        gui.getFontRenderer().drawString(LangUtils.translate(Lang.CONTAINER_INV.get()), 8, gui.getHeight() - 126 + 3, 0xFF404040);
//
//        GlStateManager.pushMatrix();
//        GlStateManager.translate(0.0F, 0.0F, 300.0F);
//        if( !this.hasUpgStgI ) {
//            for( int j = 0; j < 9; j++ ) {
//                gui.drawGradient(8 + j * 18, 40 + 18, 8 + 16 + j * 18, 40 + 18 + 16, 0x80FFFFFF, 0x80FFFFFF);
//            }
//        }
//
//        if( !this.hasUpgStgII ) {
//            for( int j = 0; j < 9; j++ ) {
//                gui.drawGradient(8 + j * 18, 40 + 36, 8 + 16 + j * 18, 40 + 36 + 16, 0x80FFFFFF, 0x80FFFFFF);
//            }
//        }
//
//        if( !this.hasUpgStgIII ) {
//            for( int j = 0; j < 9; j++ ) {
//                gui.drawGradient(8 + j * 18, 40 + 54, 8 + 16 + j * 18, 40 + 54 + 16, 0x80FFFFFF, 0x80FFFFFF);
//            }
//        }
//        GlStateManager.popMatrix();
//    }
}
