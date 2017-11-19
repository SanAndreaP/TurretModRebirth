/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.guis;

import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUpgrades
        implements IGuiTCU
{
    private boolean hasUpgStgI;
    private boolean hasUpgStgII;
    private boolean hasUpgStgIII;

    @Override
    public Container getContainer(EntityPlayer player, ITurretInst turretInst) {
        return new ContainerTurretUpgrades(player.inventory, (UpgradeProcessor) turretInst.getUpgradeProcessor());
    }

    @Override
    public void initGui(IGuiTcuInst<?> gui) {

    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.hasUpgStgI = gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_I);
        this.hasUpgStgII = gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_II);
        this.hasUpgStgIII = gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_III);
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        gui.getGui().mc.getTextureManager().bindTexture(Resources.GUI_TCU_UPGRADES.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.getGui().drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getGuiWidth(), gui.getGuiHeight());
    }

    @Override
    public void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {
        gui.getFontRenderer().drawString(Lang.translate(Lang.CONTAINER_INV.get()), 8, gui.getGuiHeight() - 126 + 3, 0xFF404040);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 300.0F);
        if( !this.hasUpgStgI ) {
            for( int j = 0; j < 9; j++ ) {
                gui.drawGradient(7 + j * 18, 25 + 18, 7 + 16 + j * 18, 25 + 18 + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }

        if( !this.hasUpgStgII ) {
            for( int j = 0; j < 9; j++ ) {
                gui.drawGradient(7 + j * 18, 25 + 36, 7 + 16 + j * 18, 25 + 36 + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }

        if( !this.hasUpgStgIII ) {
            for( int j = 0; j < 9; j++ ) {
                gui.drawGradient(7 + j * 18, 25 + 54, 7 + 16 + j * 18, 25 + 54 + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }
        GlStateManager.popMatrix();
    }
}
