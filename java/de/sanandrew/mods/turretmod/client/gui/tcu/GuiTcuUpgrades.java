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
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.network.packet.PacketEjectAllUpgrades;
import de.sanandrew.mods.turretmod.network.packet.PacketEjectUpgrade;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import de.sanandrew.mods.turretmod.util.TmrItems;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiTcuUpgrades
        extends AGuiTurretControlUnit
{
    private static final int COL_COUNT = 9;

    private List<TurretUpgrade> tempUpgradeList = new ArrayList<>();

    private boolean prevIsLmbDown;

    private int rowsVisible = 1;

    private GuiButton ejectAll;

    public GuiTcuUpgrades(EntityTurretBase turret) {
        super(turret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        int center = this.guiLeft + (this.xSize - 150) / 2;
        this.buttonList.add(this.ejectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 190, 150, translateBtn("ejectAll")));

        this.pageUpgrades.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        this.tempUpgradeList = this.myTurret.getUpgradeList();
        this.ejectAll.enabled = this.tempUpgradeList.size() > 0;

        this.rowsVisible = this.myTurret.getMaxUpgradeSlots() / 9;
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        boolean isLmbDown = Mouse.isButtonDown(0);

        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_UPGRADES.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        int col;
        int row;
        ItemStack currStack;

        int maxSlots = MathHelper.ceiling_double_int(TurretAttributes.MAX_UPGRADE_SLOTS.clampValue(Double.MAX_VALUE));
        int maxActiveUpg = this.tempUpgradeList.size();
        for( int i = 0; i < maxSlots; i++ ) {
            col = i % COL_COUNT;
            row = i / COL_COUNT;
            if( i < maxActiveUpg ) {
                TurretUpgrade upgrade = this.tempUpgradeList.get(i);
                currStack = TmrItems.turretUpgrade.getStackWithUpgrade(upgrade, 1);

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                GuiUtils.drawGuiIcon(TmrItems.turretUpgrade.getIcon(currStack, 0), this.guiLeft + 7 + col * 18, this.guiTop + 25 + row * 26);
                GuiUtils.drawGuiIcon(TmrItems.turretUpgrade.getIcon(currStack, 1), this.guiLeft + 7 + col * 18, this.guiTop + 25 + row * 26);

                GL11.glEnable(GL11.GL_BLEND);
                this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_UPGRADES.getResource());
                boolean isHoveringOverEject = GuiUtils.isMouseInRect(mouseX, mouseY, this.guiLeft + 6 + col * 18, this.guiTop + 18 + row * 26, 5, 6);
                this.drawTexturedModalRect(this.guiLeft + 6 + col * 18, this.guiTop + 18 + row * 26, 176, (isHoveringOverEject ? 0 : 6), 5, 6);
                if( isHoveringOverEject && isLmbDown && !this.prevIsLmbDown ) {
                    this.ejectUpgrade(this.tempUpgradeList.get(i));
                }
                if( GuiUtils.isMouseInRect(mouseX, mouseY, this.guiLeft + 6 + col * 18, this.guiTop + 24 + row * 26, 18, 18) ) {
                    GL11.glTranslatef(0.0F, 0.0F, 300.0F);
                    drawRect(this.guiLeft + 7 + col * 18, this.guiTop + 25 + row * 26, this.guiLeft + 23 + col * 18, this.guiTop + 41 + row * 16, 0x80FFFFFF);
                    GL11.glTranslatef(0.0F, 0.0F, 1.0F);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    drawHoveringText(currStack, this.guiLeft + 15 + col * 18, this.guiTop + 32 + row * 26, this.fontRendererObj, col / 5 > 0);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glTranslatef(0.0F, 0.0F, -301.0F);
                }
                GL11.glDisable(GL11.GL_BLEND);
            }

            if( col == 0 ) {
                if( row >= this.rowsVisible ) {
                    GL11.glEnable(GL11.GL_BLEND);
                    this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_UPGRADES.getResource());
                    this.drawTexturedModalRect(this.guiLeft + 6, this.guiTop + 18 + row * 26, 0, 222, 162, 24);
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }

        }

        this.prevIsLmbDown = isLmbDown;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button == this.ejectAll ) {
            PacketEjectAllUpgrades.sendToServer(this.myTurret);
        } else {
            super.actionPerformed(button);
        }
    }

    private void ejectUpgrade(TurretUpgrade upgrade) {
        PacketEjectUpgrade.sendToServer(this.myTurret, upgrade);
    }

    private static String translateBtn(String s) {
        return SAPUtils.translatePreFormat("gui.%s.tcu.page.upgrades.button.%s", TurretMod.MOD_ID, s);
    }

    protected void drawHoveringText(ItemStack stack, int xPos, int yPos, FontRenderer fontRenderer, boolean onRight) {
//        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
//        RenderHelper.disableStandardItemLighting();
//        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);

        List<String> text = new ArrayList<>();
        text.add(stack.getDisplayName());
        stack.getItem().addInformation(stack, this.mc.thePlayer, text, false);

        int textWidth = 0;
        for( String str : text ) {
            textWidth = Math.max(textWidth, fontRenderer.getStringWidth(str));
        }
        if( onRight ) {
            xPos -= textWidth;
        }

        int height = 11 * (text.size() - 1) + 12;

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + height + 3, bkgColor, bkgColor);

        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg, darkBg);

//        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for( int i = 0; i < text.size(); i++ ) {
            fontRenderer.drawStringWithShadow(text.get(i), xPos, yPos + (i == 1 ? 12 : i * 11), -1);
        }

//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glEnable(GL11.GL_LIGHTING);
//        RenderHelper.enableGUIStandardItemLighting();
//        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
}
