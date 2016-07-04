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
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketTurretNaming;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

public class GuiTcuInfo
        extends GuiScreen
        implements GuiTurretCtrlUnit
{
    private EntityTurret turret;

    private int guiLeft;
    private int guiTop;

    private int specOwnerHead;

    private FontRenderer frAmmoItem;

    private GuiButton dismantle;
    private GuiButton toggleActive;
    private GuiButton toggleRange;

    private GuiTextField turretName;

    private String infoStr;
    private long infoTimeShown;

    public GuiTcuInfo(EntityTurret turret) {
        this.turret = turret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - GuiTCUHelper.X_SIZE) / 2;
        this.guiTop = (this.height - GuiTCUHelper.Y_SIZE) / 2;

        this.buttonList.clear();

        GuiTCUHelper.initGui(this);

        this.specOwnerHead = TmrUtils.RNG.nextInt(3) == 0 ? TmrUtils.RNG.nextInt(3) : -1;

        this.frAmmoItem = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), true);

        int center = this.guiLeft + (GuiTCUHelper.X_SIZE - 150) / 2;
        this.buttonList.add(this.dismantle = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, Lang.translate(Lang.TCU_BTN.get("dismantle"))));
        this.buttonList.add(this.toggleActive = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, Lang.translate(Lang.TCU_BTN.get("toggleActive"))));
        this.buttonList.add(this.toggleRange = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 164, 150, Lang.translate(Lang.TCU_BTN.get("range"))));

        this.turretName = new GuiTextField(this.buttonList.size(), this.fontRendererObj, this.guiLeft + 20, this.guiTop + 22, 150, 10);
        this.turretName.setMaxStringLength(128);
        this.turretName.setText(this.turret.hasCustomName() ? this.turret.getCustomNameTag() : "");

        GuiTCUHelper.pageInfo.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( this.turret.isDead ) {
            this.mc.thePlayer.closeScreen();
        }

        this.turretName.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        if( this.turret.isActive() ) {
            this.toggleActive.displayString = Lang.translate(Lang.TCU_BTN.get("toggleActive.disable"));
        } else {
            this.toggleActive.displayString = Lang.translate(Lang.TCU_BTN.get("toggleActive.enable"));
        }

        if( this.turret.showRange ) {
            this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.disable"));
        } else {
            this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.enable"));
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GuiTCUHelper.X_SIZE, GuiTCUHelper.Y_SIZE);

        if( this.specOwnerHead >= 0 ) {
            this.drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 95, GuiTCUHelper.X_SIZE, this.specOwnerHead * 8, 10, 8);
        }

        this.turretName.drawTextBox();

        String value;
//        String value = this.turret.hasCustomName() ? this.turret.getCustomNameTag() : "-n/a-";
//        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", this.turret.getHealth(), this.turret.getMaxHealth());
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 35, 0x000000);

        TargetProcessor tgtProc = this.turret.getTargetProcessor();
        value = String.format("%d", tgtProc.getAmmoCount());

        if( tgtProc.hasAmmo() ) {
            TmrClientUtils.renderStackInGui(tgtProc.getAmmoStack(), this.guiLeft + 21, this.guiTop + 49, 1.0D, frAmmoItem, value);
        }

        value = tgtProc.hasAmmo() ? tgtProc.getAmmoStack().getDisplayName() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 42, this.guiTop + 48, 0x000000);

        value = tgtProc.hasTarget() ? Lang.translate(Lang.ENTITY_NAME.get(tgtProc.getTargetName())) : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 71, 0x000000);

        value = this.turret.getOwnerName();
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);

        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
            String err = Lang.translate(this.infoStr);
            this.fontRendererObj.drawSplitString(err, this.guiLeft + 10 + (GuiTCUHelper.X_SIZE - 20 - Math.min(GuiTCUHelper.X_SIZE - 20, this.fontRendererObj.getStringWidth(err))) / 2,
                                                 this.guiTop + 178, GuiTCUHelper.X_SIZE - 25, 0xFFFF0000);
        } else {
            this.infoStr = null;
        }

        GuiTCUHelper.drawScreen(this);

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.turretName.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if( !this.turretName.textboxKeyTyped(typedChar, keyCode) ) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if( button == this.dismantle ) {
            if( !this.turret.tryDismantle(this.mc.thePlayer) ) {
                this.infoStr = String.format("gui.%s.tcu.page.info.button.dismantle.error", TurretModRebirth.ID);
                this.infoTimeShown = System.currentTimeMillis();
            } else {
                this.infoStr = null;
                this.mc.thePlayer.closeScreen();
            }
        } else if( button == this.toggleRange ) {
            this.turret.showRange = !this.turret.showRange;
            this.turret.ignoreFrustumCheck = this.turret.showRange;

            if( this.turret.showRange ) {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.disable"));
            } else {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.enable"));
            }
        } else if( button == this.toggleActive ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(this.turret, PacketPlayerTurretAction.TOGGLE_ACTIVE));
        } else if( !GuiTCUHelper.actionPerformed(button, this) ) {
            super.actionPerformed(button);
        }
    }

    @Override
    public void onGuiClosed() {
        PacketRegistry.sendToServer(new PacketTurretNaming(this.turret, this.turretName.getText()));

        super.onGuiClosed();
    }

    @Override
    public int getGuiLeft() {
        return this.guiLeft;
    }

    @Override
    public int getGuiTop() {
        return this.guiTop;
    }

    @Override
    public List getButtonList() {
        return this.buttonList;
    }

    @Override
    public EntityTurret getTurret() {
        return this.turret;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.fontRendererObj;
    }

    @Override
    public Minecraft getMc() {
        return this.mc;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
