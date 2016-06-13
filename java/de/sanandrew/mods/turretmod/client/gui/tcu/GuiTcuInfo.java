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
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

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
        this.buttonList.add(this.dismantle = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, translateBtn("dismantle")));
        this.buttonList.add(this.toggleActive = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, translateBtn("toggleActive")));
        this.buttonList.add(this.toggleRange = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 164, 150, translateBtn("range")));

        GuiTCUHelper.pageInfo.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( this.turret.isDead ) {
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        if( this.turret.isActive() ) {
            this.toggleActive.displayString = translateBtn("toggleActive.disable");
        } else {
            this.toggleActive.displayString = translateBtn("toggleActive.enable");
        }

        if( this.turret.showRange ) {
            this.toggleRange.displayString = translateBtn("range.disable");
        } else {
            this.toggleRange.displayString = translateBtn("range.enable");
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GuiTCUHelper.X_SIZE, GuiTCUHelper.Y_SIZE);

        if( this.specOwnerHead >= 0 ) {
            this.drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 95, GuiTCUHelper.X_SIZE, this.specOwnerHead * 8, 10, 8);
        }

        String value = this.turret.hasCustomNameTag() ? this.turret.getCustomNameTag() : "-n/a-";
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 23, 0x000000);

        value = String.format("%.1f / %.1f HP", this.turret.getHealth(), this.turret.getMaxHealth());
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 35, 0x000000);

        TargetProcessor tgtProc = this.turret.getTargetProcessor();
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

        value = this.turret.getOwnerName();
        this.fontRendererObj.drawString(value, this.guiLeft + 20, this.guiTop + 95, 0x000000);

        if( this.infoStr != null && this.infoTimeShown >= System.currentTimeMillis() - 5000L ) {
            String err = StatCollector.translateToLocal(this.infoStr);
            this.fontRendererObj.drawSplitString(err, this.guiLeft + 10 + (GuiTCUHelper.X_SIZE - 20 - Math.min(GuiTCUHelper.X_SIZE - 20, this.fontRendererObj.getStringWidth(err))) / 2,
                                                 this.guiTop + 178, GuiTCUHelper.X_SIZE - 25, 0xFFFF0000);
        } else {
            this.infoStr = null;
        }

        GuiTCUHelper.drawScreen(this);

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
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
                this.toggleRange.displayString = translateBtn("range.disable");
            } else {
                this.toggleRange.displayString = translateBtn("range.enable");
            }
        } else if( button == this.toggleActive ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(this.turret, PacketPlayerTurretAction.TOGGLE_ACTIVE));
        } else if( !GuiTCUHelper.actionPerformed(button, this) ) {
            super.actionPerformed(button);
        }
    }

    private static String translateBtn(String s) {
        return Lang.translate(String.format(Lang.TCU_BTN, s));
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
