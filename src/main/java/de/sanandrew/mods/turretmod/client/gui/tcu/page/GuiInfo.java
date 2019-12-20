/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketTurretNaming;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiInfo
        implements IGuiTCU
{
    private static final int ACTION_DISMANTLE  = 0;
    private static final int ACTION_ACTIVATE   = 1;
    private static final int ACTION_DEACTIVATE = 2;
    private static final int ACTION_RANGE_SHOW = 3;
    private static final int ACTION_RANGE_HIDE = 4;

//    private int          specOwnerHead;
    private FontRenderer frAmmoItem;

    private Button dismantle;
    private Button setActive;
    private Button setDeactive;
    private Button showRange;
    private Button hideRange;

    private TextField turretName;

    private String infoStr;
    private long   infoTimeShown;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.dismantle = guiDefinition.getElementById("dismantle").get(Button.class);
        this.setActive = guiDefinition.getElementById("activate").get(Button.class);
        this.setDeactive = guiDefinition.getElementById("deactivate").get(Button.class);
        this.showRange = guiDefinition.getElementById("showRange").get(Button.class);
        this.hideRange = guiDefinition.getElementById("hideRange").get(Button.class);

        this.turretName = guiDefinition.getElementById("turretNameInput").get(TextField.class);
//        this.specOwnerHead = MiscUtils.RNG.randomInt(3) == 0 ? MiscUtils.RNG.randomInt(5) : 0;
//
//        this.frAmmoItem = new FontRenderer(gui.getGui().mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), gui.getGui().mc.getTextureManager(), true);

        this.setActive.setVisible(false);
        this.hideRange.setVisible(false);

        this.turretName.setMaxStringLength(128);
        this.turretName.setText(gui.getTurretInst().get().hasCustomName() ? gui.getTurretInst().get().getCustomNameTag() : "");

        if( !gui.hasPermision() ) {
            this.dismantle.setEnabled(false);
            this.setActive.setEnabled(false);
            this.setDeactive.setEnabled(false);
            this.showRange.setEnabled(false);
            this.hideRange.setEnabled(false);
            this.turretName.setEnabled(false);
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        ITurretInst turretInst = gui.getTurretInst();
        this.setDeactive.setVisible(turretInst.isActive());
        this.setActive.setVisible(!this.setDeactive.isVisible());
        this.hideRange.setVisible(turretInst.showRange());
        this.showRange.setVisible(!this.hideRange.isVisible());
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_INFO.resource;
    }

    //    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        ITurretInst turretInst = gui.getTurretInst();
        FontRenderer fontRenderer = gui.getFontRenderer();
        GuiScreen guiScreen = gui.getGui();
        int posX = gui.getPosX();
        int posY = gui.getPosY();

        guiScreen.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.resource);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
//        guiScreen.drawTexturedModalRect(posX + 7, posY + 128, 234, this.specOwnerHead * 8, 11, 8);
        guiScreen.drawTexturedModalRect(posX + 7, posY + 140, 245, 24, 11, 8);

        EntityLiving turretL = turretInst.get();
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

        value = tgtProc.hasTarget() ? LangUtils.translate(LangUtils.ENTITY_NAME.get(tgtProc.getTargetName())) : "-n/a-";
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

        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
            String err = LangUtils.translate(this.infoStr);
            fontRenderer.drawSplitString(err, posX + 10 + (gui.getWidth() - 20 - Math.min(gui.getWidth() - 20, fontRenderer.getStringWidth(err))) / 2,
                                         posY + 160, gui.getWidth() - 25, 0xFFFF0000);
        } else {
            this.infoStr = null;
        }
    }

    @Override
    public boolean onElementAction(IGuiTcuInst<?> gui, IGuiElement element, int action) {
        ITurretInst turretInst = gui.getTurretInst();
        switch( action ) {
            case ACTION_DISMANTLE:
                if( !PacketPlayerTurretAction.tryDismantle(gui.getGui().mc.player, turretInst) ) {
                    this.infoStr = Lang.TCU_DISMANTLE_ERROR.get();
                    this.infoTimeShown = System.currentTimeMillis();
                } else {
                    this.infoStr = null;
                    gui.getGui().mc.player.closeScreen();
                }
                return true;
            case ACTION_RANGE_SHOW:
                turretInst.setShowRange(true);
                turretInst.get().ignoreFrustumCheck = true;
                return true;
            case ACTION_RANGE_HIDE:
                turretInst.setShowRange(false);
                turretInst.get().ignoreFrustumCheck = false;
                return true;
            case ACTION_ACTIVATE:
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.SET_ACTIVE));
                return true;
            case ACTION_DEACTIVATE:
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.SET_DEACTIVE));
                return true;
        }

        return false;
    }

    @Override
    public void guiClosed(IGuiTcuInst<?> gui) {
        PacketRegistry.sendToServer(new PacketTurretNaming(gui.getTurretInst(), this.turretName.getText()));
    }
}
