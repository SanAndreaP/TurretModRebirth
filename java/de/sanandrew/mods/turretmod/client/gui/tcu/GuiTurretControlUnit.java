/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.turretmod.client.gui.control.GuiIconTab;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public abstract class GuiTurretControlUnit
        extends GuiScreen
{
    protected GuiIconTab pageInfo;
    protected GuiIconTab pageTargets;
    protected GuiIconTab pageUpgrades;

    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;

    protected final EntityTurret myTurret;

    public GuiTurretControlUnit(EntityTurret turret) {
        this.myTurret = turret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.xSize = 176;
        this.ySize = 222;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.buttonList.add(this.pageInfo = new GuiIconTab(this.buttonList.size(), this.guiLeft - 23, this.guiTop + 5,
                Items.sign.getIconFromDamage(0), translateTab("info"), false));
        this.buttonList.add(this.pageTargets = new GuiIconTab(this.buttonList.size(), this.guiLeft - 23, this.guiTop + 33,
                Items.diamond_sword.getIconFromDamage(0), translateTab("targets"), false));
        this.buttonList.add(this.pageUpgrades = new GuiIconTab(this.buttonList.size(), this.guiLeft - 23, this.guiTop + 61,
                Items.cake.getIconFromDamage(0), translateTab("upgrades"), false));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( this.myTurret.isDead ) {
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.drawScreenPostBkg(mouseX, mouseY, partTicks);

        String pageName = "";
        if( !this.pageInfo.enabled ) {
            pageName = "info";
        } else if( !this.pageTargets.enabled ) {
            pageName = "targets";
        } else if( !this.pageUpgrades.enabled ) {
            pageName = "upgrades";
        }
        pageName = StatCollector.translateToLocal(String.format("gui.%s.tcu.page.%s.title", TurretModRebirth.ID, pageName));
        this.fontRendererObj.drawString(pageName, this.guiLeft + 8, this.guiTop + 6, 0x404040);

//        String turretName = StatCollector.translateToLocal(String.format("entity.%s.%s.name", TurretModRebirth.ID, TurretRegistry.getTurretInfo(this.myTurret.getClass()).getName());
//        this.fontRendererObj.drawString(turretName, this.guiLeft + (this.xSize - this.fontRendererObj.getStringWidth(turretName)) / 2, this.guiTop + this.ySize - 15,
//                0x00FF00, false
//        );

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
//        if( button == this.pageInfo ) {
//            TurretMod.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TCU_INFO, this.myTurret.getEntityId(), 0, 0);
//        } else if( button == this.pageTargets ) {
//            TurretMod.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TCU_TARGETS, this.myTurret.getEntityId(), 0, 0);
//        } else if( button == this.pageUpgrades ) {
//            TurretMod.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TCU_UPGRADES, this.myTurret.getEntityId(), 0, 0);
//        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public abstract void drawScreenPostBkg(int mouseX, int mouseY, float partTicks);

    private static String translateTab(String s) {
        return StatCollector.translateToLocal(String.format("gui.%s.tcu.page.%s.tab", TurretModRebirth.ID, s));
    }
}
