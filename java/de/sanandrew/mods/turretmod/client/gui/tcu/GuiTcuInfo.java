/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiTcuInfo
        extends GuiTurretControlUnit
{
    private int specOwnerHead;

    private FontRenderer frAmmoItem;

    private GuiButton dismantle;
    private GuiButton toggleActive;
    private GuiButton toggleRange;

    private String infoStr;
    private long infoTimeShown;

    public GuiTcuInfo(EntityTurret turret) {
        super(turret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.pageInfo.enabled = false;
        this.specOwnerHead = TmrUtils.RNG.nextInt(3) == 0 ? TmrUtils.RNG.nextInt(3) : -1;

        this.frAmmoItem = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), true);

        int center = this.guiLeft + (this.xSize - 150) / 2;
        this.buttonList.add(this.dismantle = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, translateBtn("dismantle")));
        this.buttonList.add(this.toggleActive = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, translateBtn("toggleActive")));
        this.buttonList.add(this.toggleRange = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 164, 150, translateBtn("range")));

        if( true ) { //TODO: check for turret activation
            this.toggleActive.displayString = translateBtn("toggleActive.disable");
        } else {
            this.toggleActive.displayString = translateBtn("toggleActive.enable");
        }

        if( this.myTurret.showRange ) {
            this.toggleRange.displayString = translateBtn("range.disable");
        } else {
            this.toggleRange.displayString = translateBtn("range.enable");
        }
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        this.mc.renderEngine.bindTexture(Textures.GUI_TCU_INFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if( this.specOwnerHead >= 0 ) {
            this.drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 95, this.xSize, this.specOwnerHead * 8, 10, 8);
        }

        String value = this.myTurret.hasCustomNameTag() ? this.myTurret.getCustomNameTag() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", this.myTurret.getHealth(), this.myTurret.getMaxHealth());
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 35, 0x000000);

        TargetProcessor tgtProc = this.myTurret.getTargetProcessor();
        value = String.format("%d", tgtProc.getAmmoCount());

        if( tgtProc.hasAmmo() ) {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            this.drawItemStack(tgtProc.getAmmoStack(), this.guiLeft + 21, this.guiTop + 49);
            GL11.glTranslated(0.0D, 0.0D, 300.0D);
            this.frAmmoItem.drawStringWithShadow(value, this.guiLeft + 38 - this.frAmmoItem.getStringWidth(value), this.guiTop + 58, 0xFFFFFFFF);
            GL11.glTranslated(0.0D, 0.0D, -300.0D);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
        }

        value = tgtProc.hasAmmo() ? tgtProc.getAmmoStack().getDisplayName() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 42, this.guiTop + 48, 0x000000);

        value = tgtProc.hasTarget() ? StatCollector.translateToLocal(String.format("entity.%s.name", tgtProc.getTargetName())) : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 71, 0x000000);

        value = this.myTurret.getOwnerName();
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);

        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
            String err = StatCollector.translateToLocal(this.infoStr);
            this.fontRendererObj.drawSplitString(err, this.guiLeft + 10 + (this.xSize - 20 - Math.min(this.xSize - 20, this.fontRendererObj.getStringWidth(err))) / 2, this.guiTop + 178, this.xSize - 25, 0xFFFF0000);
        } else {
            this.infoStr = null;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button == this.dismantle ) {
            if( !this.myTurret.tryDismantle(this.mc.thePlayer) ) {
                this.infoStr = String.format("gui.%s.tcu.page.info.button.dismantle.error", TurretModRebirth.ID);
                this.infoTimeShown = System.currentTimeMillis();
            } else {
                this.infoStr = null;
                this.closeGui();
            }
        } else if( button == this.toggleRange ) {
            this.myTurret.showRange = !this.myTurret.showRange;
            this.myTurret.ignoreFrustumCheck = this.myTurret.showRange;

            if( this.myTurret.showRange ) {
                this.toggleRange.displayString = translateBtn("range.disable");
            } else {
                this.toggleRange.displayString = translateBtn("range.enable");
            }
        } else {
            super.actionPerformed(button);
        }
    }

    private static String translateBtn(String s) {
        return StatCollector.translateToLocal(String.format("gui.%s.tcu.page.info.button.%s", TurretModRebirth.ID, s));
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        itemRender.renderItemAndEffectIntoGUI(this.frAmmoItem, this.mc.getTextureManager(), stack, x, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        GL11.glTranslatef(0.0F, 0.0F, -32.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }
}
