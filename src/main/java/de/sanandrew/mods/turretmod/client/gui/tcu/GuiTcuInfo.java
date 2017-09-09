/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketTurretNaming;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTcuInfo
        extends GuiScreen
        implements GuiTurretCtrlUnit
{
    private final ITurretInst turretInst;
    private final boolean hasPermission;

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

    public GuiTcuInfo(ITurretInst turretInst, boolean hasPerm) {
        this.turretInst = turretInst;
        this.hasPermission = hasPerm;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - GuiTCUHelper.X_SIZE) / 2;
        this.guiTop = (this.height - GuiTCUHelper.Y_SIZE) / 2;

        this.buttonList.clear();

        GuiTCUHelper.initGui(this);

        this.specOwnerHead = MiscUtils.RNG.randomInt(3) == 0 ? MiscUtils.RNG.randomInt(3) : -1;

        this.frAmmoItem = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), true);

        int center = this.guiLeft + (GuiTCUHelper.X_SIZE - 150) / 2;
        if( this.hasPermission ) {
            this.buttonList.add(this.dismantle = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, Lang.translate(Lang.TCU_BTN.get("dismantle"))));
            this.buttonList.add(this.toggleActive = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, Lang.translate(Lang.TCU_BTN.get("toggleActive"))));
            this.buttonList.add(this.toggleRange = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 164, 150, Lang.translate(Lang.TCU_BTN.get("range"))));
        }

        this.turretName = new GuiTextField(this.buttonList.size(), this.fontRenderer, this.guiLeft + 20, this.guiTop + 22, 150, 10);
        this.turretName.setMaxStringLength(128);
        this.turretName.setText(this.turretInst.getEntity().hasCustomName() ? this.turretInst.getEntity().getCustomNameTag() : "");

        GuiTCUHelper.pageInfo.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( this.turretInst.getEntity().isDead ) {
            this.mc.player.closeScreen();
        }

        this.turretName.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        if( this.toggleActive != null ) {
            if( this.turretInst.isActive() ) {
                this.toggleActive.displayString = Lang.translate(Lang.TCU_BTN.get("toggleActive.disable"));
            } else {
                this.toggleActive.displayString = Lang.translate(Lang.TCU_BTN.get("toggleActive.enable"));
            }
        }

        if( this.toggleRange != null ) {
            if( this.turretInst.showRange() ) {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.disable"));
            } else {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.enable"));
            }
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

        EntityLiving turretL = this.turretInst.getEntity();
        String value;
//        String value = this.turret_placer.hasCustomName() ? this.turret_placer.getCustomNameTag() : "-n/a-";
//        this.fontRenderer.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", turretL.getHealth(), turretL.getMaxHealth());
        this.fontRenderer.drawString(value, this.guiLeft + 20, this.guiTop + 35, 0x000000);

        ITargetProcessor tgtProc = this.turretInst.getTargetProcessor();
        value = String.format("%d", tgtProc.getAmmoCount());

        if( tgtProc.hasAmmo() ) {
            RenderUtils.renderStackInGui(tgtProc.getAmmoStack(), this.guiLeft + 21, this.guiTop + 49, 1.0D, this.frAmmoItem, value, true);
        }

        value = tgtProc.hasAmmo() ? tgtProc.getAmmoStack().getDisplayName() : "-n/a-";
        this.fontRenderer.drawString(value, this.guiLeft + 42, this.guiTop + 48, 0x000000);

        value = tgtProc.hasTarget() ? Lang.translate(Lang.ENTITY_NAME.get(tgtProc.getTargetName())) : "-n/a-";
        this.fontRenderer.drawString(value, this.guiLeft + 20, this.guiTop + 71, 0x000000);

        value = this.turretInst.getOwnerName();
        this.fontRenderer.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);

        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
            String err = Lang.translate(this.infoStr);
            this.fontRenderer.drawSplitString(err, this.guiLeft + 10 + (GuiTCUHelper.X_SIZE - 20 - Math.min(GuiTCUHelper.X_SIZE - 20, this.fontRenderer.getStringWidth(err))) / 2,
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
            if( !PacketPlayerTurretAction.tryDismantle(this.mc.player, this.turretInst) ) {
                this.infoStr = Lang.TCU_DISMANTLE_ERROR.get();
                this.infoTimeShown = System.currentTimeMillis();
            } else {
                this.infoStr = null;
                this.mc.player.closeScreen();
            }
        } else if( button == this.toggleRange ) {
            this.turretInst.setShowRange(!this.turretInst.showRange());
            this.turretInst.getEntity().ignoreFrustumCheck = this.turretInst.showRange();

            if( this.turretInst.showRange() ) {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.disable"));
            } else {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.enable"));
            }
        } else if( button == this.toggleActive ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(this.turretInst, PacketPlayerTurretAction.TOGGLE_ACTIVE));
        } else if( !GuiTCUHelper.actionPerformed(button, this) ) {
            super.actionPerformed(button);
        }
    }

    @Override
    public void onGuiClosed() {
        PacketRegistry.sendToServer(new PacketTurretNaming(this.turretInst, this.turretName.getText()));

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

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public boolean hasPermision() {
        return this.hasPermission;
    }
}
