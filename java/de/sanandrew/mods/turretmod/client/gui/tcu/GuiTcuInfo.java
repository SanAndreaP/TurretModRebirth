/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import org.lwjgl.opengl.GL11;

public class GuiTcuInfo
        extends AGuiTurretControlUnit
{
    public GuiTcuInfo(AEntityTurretBase turret) {
        super(turret);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pageInfo.enabled = false;
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_INFO_SETTINGS.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        String title = "name:";
        String value = this.myTurret.hasCustomNameTag() ? this.myTurret.getCustomNameTag() : "-n/a-";
        this.fontRendererObj.drawString(title, this.guiLeft + 10, this.guiTop + 10, 0x003300);
        this.fontRendererObj.drawString(value, this.guiLeft + 14 + this.fontRendererObj.getStringWidth(title), this.guiTop + 10, 0x000000);

        title = "health:";
        value = String.format("%.1f / %.1f HP", this.myTurret.getHealth(), this.myTurret.getMaxHealth());
        this.fontRendererObj.drawString(title, this.guiLeft + 10, this.guiTop + 20, 0x660000);
        this.fontRendererObj.drawString(value, this.guiLeft + 14 + this.fontRendererObj.getStringWidth(title), this.guiTop + 20, 0x330000);

        title = "ammo:";
        value = String.format("%d / %d proj.", this.myTurret.getAmmo(), this.myTurret.getMaxAmmo());
        this.fontRendererObj.drawString(title, this.guiLeft + 10, this.guiTop + 30, 0x000066);
        this.fontRendererObj.drawString(value, this.guiLeft + 14 + this.fontRendererObj.getStringWidth(title), this.guiTop + 30, 0x000033);

        title = "curr. target:";
        value = this.myTurret.hasTarget() ? SAPUtils.translatePreFormat("entity.%s.name", this.myTurret.getTargetName()) : "-n/a-";
        this.fontRendererObj.drawString(title, this.guiLeft + 10, this.guiTop + 40, 0x666600);
        this.fontRendererObj.drawString(value, this.guiLeft + 14 + this.fontRendererObj.getStringWidth(title), this.guiTop + 40, 0x333300);
    }
}
