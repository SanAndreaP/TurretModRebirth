/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.core.manpack.util.client.helpers.GuiUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTcuInfo
        extends AGuiTurretControlUnit
{
    private int specOwnerHead;

    private FontRenderer frAmmoItem;

    private GuiButton dismantle;
    private GuiButton toggleActive;

    public GuiTcuInfo(AEntityTurretBase turret) {
        super(turret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.pageInfo.enabled = false;
        this.specOwnerHead = SAPUtils.RNG.nextInt(3) == 0 ? SAPUtils.RNG.nextInt(3) : -1;

        this.frAmmoItem = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), true);

        int center = this.guiLeft + (this.xSize - 150) / 2;
        this.buttonList.add(this.dismantle = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, translateBtn("dismantle")));
        this.buttonList.add(this.toggleActive = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, translateBtn("toggleActive")));
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

        value = String.format("%d", this.myTurret.getAmmo());

        if( this.myTurret.getAmmoType() != null ) {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GuiUtils.drawGuiIcon(this.myTurret.getAmmoType().getTypeItem().getIconIndex(), this.guiLeft + 21, this.guiTop + 49);
            GL11.glTranslated(0.0D, 0.0D, 300.0D);
            this.frAmmoItem.drawStringWithShadow(value, this.guiLeft + 38 - this.frAmmoItem.getStringWidth(value), this.guiTop + 58, 0xFFFFFFFF);
            GL11.glTranslated(0.0D, 0.0D, -300.0D);
        }
        this.zLevel = 0.0F;
        value = this.myTurret.getAmmoType() != null ? this.myTurret.getAmmoType().getTypeItem().getDisplayName() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 42, this.guiTop + 48, 0x000000);
//
        value = this.myTurret.hasTarget() ? SAPUtils.translatePreFormat("entity.%s.name", this.myTurret.getTargetName()) : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 71, 0x000000);

        value = this.myTurret.getOwnerName();
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
    }

    private static String translateBtn(String s) {
        return SAPUtils.translatePreFormat("gui.%s.tcu.page.info.button.%s", TurretMod.MOD_ID, s);
    }
}
