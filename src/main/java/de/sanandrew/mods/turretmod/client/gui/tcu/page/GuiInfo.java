/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.GuiCameras;
import de.sanandrew.mods.turretmod.client.gui.control.GuiButtonIcon;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretCam;
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
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiInfo
        implements IGuiTCU
{
    private int specOwnerHead;
    private FontRenderer frAmmoItem;

    private GuiButton dismantle;
    private GuiButton setActive;
    private GuiButton setDeactive;
    private GuiButton showRange;
    private GuiButton hideRange;

    private GuiTextField turretName;

    private String infoStr;
    private long infoTimeShown;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        this.specOwnerHead = MiscUtils.RNG.randomInt(3) == 0 ? MiscUtils.RNG.randomInt(5) : 0;

        this.frAmmoItem = new FontRenderer(gui.getGui().mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), gui.getGui().mc.getTextureManager(), true);

        int center = gui.getPosX() + (gui.getWidth() - 56) / 2;
        int btnY = gui.getPosY() + 190;
        if( gui.hasPermision() ) {
            this.dismantle = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), center, btnY, 176, 50, Resources.GUI_TCU_INFO.getResource(),
                                                                Lang.translate(Lang.TCU_BTN.get("dismantle"))));
            this.setActive = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), center + 19, btnY, 194, 50, Resources.GUI_TCU_INFO.getResource(),
                                                                Lang.translate(Lang.TCU_BTN.get("toggleActive.enable"))));
            this.setDeactive = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), center + 19, btnY, 212, 50, Resources.GUI_TCU_INFO.getResource(),
                                                                  Lang.translate(Lang.TCU_BTN.get("toggleActive.disable"))));
            this.showRange = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), center + 38, btnY, 176, 86, Resources.GUI_TCU_INFO.getResource(),
                                                                Lang.translate(Lang.TCU_BTN.get("range.enable"))));
            this.hideRange = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), center + 38, btnY, 194, 86, Resources.GUI_TCU_INFO.getResource(),
                                                                Lang.translate(Lang.TCU_BTN.get("range.disable"))));
        }

        this.setActive.visible = false;
        this.hideRange.visible = false;

        this.turretName = new GuiTextField(0, gui.getFontRenderer(), gui.getPosX() + 20, gui.getPosY() + 91, 148, 10);
        this.turretName.setMaxStringLength(128);
        this.turretName.setText(gui.getTurretInst().getEntity().hasCustomName() ? gui.getTurretInst().getEntity().getCustomNameTag() : "");
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.turretName.updateCursorCounter();

        ITurretInst turretInst = gui.getTurretInst();
        this.setDeactive.visible = turretInst.isActive();
        this.setActive.visible = !this.setDeactive.visible;
        this.hideRange.visible = turretInst.showRange();
        this.showRange.visible = !this.hideRange.visible;
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        ITurretInst turretInst = gui.getTurretInst();
        FontRenderer fontRenderer = gui.getFontRenderer();
        GuiScreen guiScreen = gui.getGui();
        int posX = gui.getPosX();
        int posY = gui.getPosY();

        guiScreen.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        guiScreen.drawTexturedModalRect(posX, posY, 0, 0, gui.getWidth(), gui.getHeight());
        guiScreen.drawTexturedModalRect(posX + (gui.getWidth() - 50) / 2, posY + 39, 176, 0, 50, 50);

        gui.drawGradient(posX + 7, posY + 92, posX + 169, posY + 100, 0x10000000, 0x10000000);
        gui.drawGradient(posX + 7, posY + 104, posX + 169, posY + 112, 0x10000000, 0x10000000);
        gui.drawGradient(posX + 7, posY + 116, posX + 169, posY + 124, 0x10000000, 0x10000000);
        gui.drawGradient(posX + 7, posY + 128, posX + 169, posY + 136, 0x10000000, 0x10000000);
        gui.drawGradient(posX + 7, posY + 140, posX + 169, posY + 148, 0x10000000, 0x10000000);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        guiScreen.drawTexturedModalRect(posX + 7, posY + 92, 245, 0, 11, 8);
        guiScreen.drawTexturedModalRect(posX + 7, posY + 104, 245, 8, 11, 8);
        guiScreen.drawTexturedModalRect(posX + 7, posY + 116, 245, 16, 11, 8);
        guiScreen.drawTexturedModalRect(posX + 7, posY + 128, 234, this.specOwnerHead * 8, 11, 8);
        guiScreen.drawTexturedModalRect(posX + 7, posY + 140, 245, 24, 11, 8);

        EntityLiving turretL = turretInst.getEntity();
        ITargetProcessor tgtProc = turretInst.getTargetProcessor();
        String value;

        value = String.format("%.1f / %.1f HP", turretL.getHealth(), turretL.getMaxHealth());
        fontRenderer.drawString(value, posX + 20, posY + 104, 0xFF000000);

        if( tgtProc.hasAmmo() ) {
            value = String.format("%dx %s", tgtProc.getAmmoCount(), tgtProc.getAmmoStack().getDisplayName());
            if( fontRenderer.getStringWidth(value) > 139 ) {
                int rgt = 139 - fontRenderer.getStringWidth("...");
                fontRenderer.drawString("...", posX + 30 + rgt, posY + 116, 0xFF000000);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GuiUtils.glScissor(posX + 20, posY + 116, 9 + rgt, 8);
                fontRenderer.drawString(value, posX + 30, posY + 116, 0xFF000000);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                fontRenderer.drawString(value, posX + 30, posY + 116, 0xFF000000);
            }
            RenderUtils.renderStackInGui(tgtProc.getAmmoStack(), posX + 20, posY + 116, 0.5D, this.frAmmoItem, "", true);
        } else {
            fontRenderer.drawString("-n/a-", posX + 20, posY + 116, 0xFF000000);
        }

        value = turretInst.getOwnerName();
        fontRenderer.drawString(value, posX + 20, posY + 128, 0xFF000000);

        value = tgtProc.hasTarget() ? Lang.translate(Lang.ENTITY_NAME.get(tgtProc.getTargetName())) : "-n/a-";
        if( fontRenderer.getStringWidth(value) > 149 ) {
            int rgt = 148 - fontRenderer.getStringWidth("...");
            fontRenderer.drawString("...", posX + 21 + rgt, posY + 140, 0xFF000000);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GuiUtils.glScissor(posX + 20, posY + 140, rgt, 8);
            fontRenderer.drawString(value, posX + 20, posY + 140, 0xFF000000);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            fontRenderer.drawString(value, posX + 20, posY + 140, 0xFF000000);
        }

//        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
//            String err = Lang.translate(this.infoStr);
//            fontRenderer.drawSplitString(err, posX + 10 + (gui.getWidth() - 20 - Math.min(gui.getWidth() - 20, fontRenderer.getStringWidth(err))) / 2,
//                                         posY + 178, gui.getWidth() - 25, 0xFFFF0000);
//        } else {
//            this.infoStr = null;
//        }

        this.turretName.drawTextBox();
    }

    @Override
    public void drawForeground(IGuiTcuInst<?> gui, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderTurretCam.drawTurretCam(gui.getTurretInst(), 48, (gui.getWidth() - 48) / 2, 40, 48, 48);
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
        } else if( button == this.showRange ) {
            turretInst.setShowRange(true);
            turretInst.getEntity().ignoreFrustumCheck = true;
        } else if( button == this.hideRange ) {
            turretInst.setShowRange(false);
            turretInst.getEntity().ignoreFrustumCheck = false;
        } else if( button == this.setActive ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.SET_ACTIVE));
        } else if( button == this.setDeactive ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.SET_DEACTIVE));
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
