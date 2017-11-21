/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketTurretNaming;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiInfo
        implements IGuiTCU
{
    private int specOwnerHead;
    private FontRenderer frAmmoItem;

    private GuiButton dismantle;
    private GuiButton toggleActive;
    private GuiButton toggleRange;

    private GuiTextField turretName;

    private String infoStr;
    private long infoTimeShown;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        this.specOwnerHead = MiscUtils.RNG.randomInt(3) == 0 ? MiscUtils.RNG.randomInt(3) : -1;

        this.frAmmoItem = new FontRenderer(gui.getGui().mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), gui.getGui().mc.getTextureManager(), true);

        int center = gui.getPosX() + (gui.getGuiWidth() - 150) / 2;
        if( gui.hasPermision() ) {
            this.dismantle = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 138, 150, Lang.translate(Lang.TCU_BTN.get("dismantle"))));
            this.toggleActive = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 151, 150, Lang.translate(Lang.TCU_BTN.get("toggleActive"))));
            this.toggleRange = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 164, 150, Lang.translate(Lang.TCU_BTN.get("range"))));
        }

        this.turretName = new GuiTextField(0, gui.getFontRenderer(), gui.getPosX() + 20, gui.getPosY() + 22, 150, 10);
        this.turretName.setMaxStringLength(128);
        this.turretName.setText(gui.getTurretInst().getEntity().hasCustomName() ? gui.getTurretInst().getEntity().getCustomNameTag() : "");
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.turretName.updateCursorCounter();
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        ITurretInst turretInst = gui.getTurretInst();
        FontRenderer fontRenderer = gui.getFontRenderer();
        GuiScreen guiScreen = gui.getGui();
        int posX = gui.getPosX();
        int posY = gui.getPosY();

        if( this.toggleActive != null ) {
            if( turretInst.isActive() ) {
                this.toggleActive.displayString = Lang.translate(Lang.TCU_BTN.get("toggleActive.disable"));
            } else {
                this.toggleActive.displayString = Lang.translate(Lang.TCU_BTN.get("toggleActive.enable"));
            }
        }

        if( this.toggleRange != null ) {
            if( turretInst.showRange() ) {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.disable"));
            } else {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.enable"));
            }
        }

//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        this.drawDefaultBackground();

        guiScreen.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        guiScreen.drawTexturedModalRect(posX, gui.getPosY(), 0, 0, gui.getGuiWidth(), gui.getGuiHeight());

        if( this.specOwnerHead >= 0 ) {
            guiScreen.drawTexturedModalRect(posX + 7, gui.getPosY() + 95, gui.getGuiWidth(), this.specOwnerHead * 8, 10, 8);
        }

        EntityLiving turretL = turretInst.getEntity();
        String value;
        //        String value = this.turret_placer.hasCustomName() ? this.turret_placer.getCustomNameTag() : "-n/a-";
        //        this.fontRenderer.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", turretL.getHealth(), turretL.getMaxHealth());
        fontRenderer.drawString(value, posX + 20, posY + 35, 0x000000);

        ITargetProcessor tgtProc = turretInst.getTargetProcessor();
        value = String.format("%d", tgtProc.getAmmoCount());

        if( tgtProc.hasAmmo() ) {
            RenderUtils.renderStackInGui(tgtProc.getAmmoStack(), posX + 21, posY + 49, 1.0D, this.frAmmoItem, value, true);
        }

        value = tgtProc.hasAmmo() ? tgtProc.getAmmoStack().getDisplayName() : "-n/a-";
        fontRenderer.drawString(value, posX + 42, posY + 48, 0x000000);

        value = tgtProc.hasTarget() ? Lang.translate(Lang.ENTITY_NAME.get(tgtProc.getTargetName())) : "-n/a-";
        fontRenderer.drawString(value, posX + 20, posY + 71, 0x000000);

        value = turretInst.getOwnerName();
        fontRenderer.drawString(value, posX + 20, posY + 95, 0x000000);

        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
            String err = Lang.translate(this.infoStr);
            fontRenderer.drawSplitString(err, posX + 10 + (gui.getGuiWidth() - 20 - Math.min(gui.getGuiWidth() - 20, fontRenderer.getStringWidth(err))) / 2,
                                         posY + 178, gui.getGuiWidth() - 25, 0xFFFF0000);
        } else {
            this.infoStr = null;
        }

        this.turretName.drawTextBox();
    }

    @Override
    public void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {
        ITurretInst turretInst = gui.getTurretInst();
        if( button == this.dismantle ) {
            if( !PacketPlayerTurretAction.tryDismantle(gui.getGui().mc.player, turretInst) ) {
                this.infoStr = Lang.TCU_DISMANTLE_ERROR.get();
                this.infoTimeShown = System.currentTimeMillis();
            } else {
                this.infoStr = null;
                gui.getGui().mc.player.closeScreen();
            }
        } else if( button == this.toggleRange ) {
            turretInst.setShowRange(!turretInst.showRange());
            turretInst.getEntity().ignoreFrustumCheck = turretInst.showRange();

            if( turretInst.showRange() ) {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.disable"));
            } else {
                this.toggleRange.displayString = Lang.translate(Lang.TCU_BTN.get("range.enable"));
            }
        } else if( button == this.toggleActive ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.TOGGLE_ACTIVE));
        }
    }

    @Override
    public void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        this.turretName.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doKeyIntercept(IGuiTcuInst<?> gui, char typedChar, int keyCode) throws IOException {
        return this.turretName.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClose(IGuiTcuInst<?> gui) {
        PacketRegistry.sendToServer(new PacketTurretNaming(gui.getTurretInst(), this.turretName.getText()));
    }
}
