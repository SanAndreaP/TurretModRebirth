/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTcuUpgrades
        extends GuiContainer
        implements GuiTurretCtrlUnit
{
    private final ITurretInst turretInst;

    private int posY;
    private int posX;

    private boolean hasUpgStgI;
    private boolean hasUpgStgII;
    private boolean hasUpgStgIII;

    public GuiTcuUpgrades(InventoryPlayer invPlayer, ITurretInst turretInst) {
        super(new ContainerTurretUpgrades(invPlayer, ((UpgradeProcessor) turretInst.getUpgradeProcessor())));

        this.turretInst = turretInst;

        this.xSize = GuiTCUHelper.X_SIZE;
        this.ySize = GuiTCUHelper.Y_SIZE;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( !ItemStackUtils.isValid(this.mc.player.getHeldItemMainhand()) || this.mc.player.getHeldItemMainhand().getItem() != ItemRegistry.TURRET_CONTROL_UNIT ) {
            this.mc.player.closeScreen();
        }

        this.hasUpgStgI = this.turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_I);
        this.hasUpgStgII = this.turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_II);
        this.hasUpgStgIII = this.turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.UPG_STORAGE_III);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;

        this.buttonList.clear();

        GuiTCUHelper.initGui(this);

        GuiTCUHelper.pageUpgrades.enabled = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(Resources.GUI_TCU_UPGRADES.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.posX, this.posY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        this.fontRenderer.drawString(Lang.translate(Lang.CONTAINER_INV.get()), 8, this.ySize - 126 + 3, 0xFF404040);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-this.posX, -this.posY, 0.0F);
        GuiTCUHelper.drawScreen(this);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 300.0F);
        if( !this.hasUpgStgI ) {
            for( int j = 0; j < 9; j++ ) {
                this.drawGradientRect(7 + j * 18, 25 + 18, 7 + 16 + j * 18, 25 + 18 + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }

        if( !this.hasUpgStgII ) {
            for( int j = 0; j < 9; j++ ) {
                this.drawGradientRect(7 + j * 18, 25 + 36, 7 + 16 + j * 18, 25 + 36 + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }

        if( !this.hasUpgStgIII ) {
            for( int j = 0; j < 9; j++ ) {
                this.drawGradientRect(7 + j * 18, 25 + 54, 7 + 16 + j * 18, 25 + 54 + 16, 0x80FFFFFF, 0x80FFFFFF);
            }
        }
        GlStateManager.popMatrix();

        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if( !GuiTCUHelper.actionPerformed(button, this) ) {
            super.actionPerformed(button);
        }
    }

    @Override
    public int getGuiLeft() {
        return this.posX;
    }

    @Override
    public int getGuiTop() {
        return this.posY;
    }

    @Override
    public List getButtonList() {
        return this.buttonList;
    }

    @Override
    public ITurretInst getTurretInst() {
        return this.turretInst;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    @Override
    public Minecraft getMc() {
        return this.mc;
    }
}
