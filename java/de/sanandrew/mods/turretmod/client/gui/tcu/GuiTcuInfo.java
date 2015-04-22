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
    private int specOwnerHead;

    public GuiTcuInfo(AEntityTurretBase turret) {
        super(turret);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.pageInfo.enabled = false;
        this.specOwnerHead = SAPUtils.RNG.nextInt(3) == 0 ? SAPUtils.RNG.nextInt(3) : -1;
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_INFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if( this.specOwnerHead >= 0 ) {
            this.drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 95, this.xSize, this.specOwnerHead * 8, 10, 8);
        }

        String value = this.myTurret.hasCustomNameTag() ? this.myTurret.getCustomNameTag() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", this.myTurret.getHealth(), this.myTurret.getMaxHealth());
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 35, 0x000000);

        value = String.format("%d / %d proj.", this.myTurret.getAmmo(), this.myTurret.getMaxAmmo());
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 47, 0x000000);

        value = this.myTurret.getAmmoType() != null ? this.myTurret.getAmmoType().getTypeItem().getDisplayName() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 59, 0x000000);

        value = this.myTurret.hasTarget() ? SAPUtils.translatePreFormat("entity.%s.name", this.myTurret.getTargetName()) : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 71, 0x000000);

        value = this.myTurret.getOwnerName();
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);
    }
}
