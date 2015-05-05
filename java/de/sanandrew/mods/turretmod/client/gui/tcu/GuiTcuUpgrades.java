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
import de.sanandrew.mods.turretmod.network.packet.PacketEjectAllUpgrades;
import de.sanandrew.mods.turretmod.network.packet.PacketEjectUpgrade;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import de.sanandrew.mods.turretmod.util.TmrItems;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiTcuUpgrades
        extends AGuiTurretControlUnit
{
    private static final int ROW_COUNT = 9;
    private static final int COL_COUNT = 6;

    private List<TurretUpgrade> tempUpgradeList = new ArrayList<>();

    private float scroll = 0.0F;
    private float scrollAmount = 0.0F;
    private boolean isScrolling;
    private boolean canScroll;
    private boolean prevIsLmbDown;

    private GuiButton ejectAll;

    public GuiTcuUpgrades(AEntityTurretBase turret) {
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

        this.canScroll = this.tempUpgradeList.size() / COL_COUNT > ROW_COUNT;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.tempUpgradeList.size() / (float) COL_COUNT - ROW_COUNT));
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = this.guiLeft + 163 + 6;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = this.guiTop + 179;

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollMinX && mouseX < scrollMaxX && mouseY > scrollMinY && mouseY < scrollMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollMinY) / 154.0F));
        }

        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_UPGRADES.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 163, this.guiTop + 19 + MathHelper.floor_float(scroll * 154.0F), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.doGlScissor(this.guiLeft + 6, this.guiTop + 18, 153, 162);

        int offsetY = Math.round(-this.scroll * (this.tempUpgradeList.size() / (float) COL_COUNT - ROW_COUNT)) * 18;
        int col;
        int row;
        ItemStack currStack;
        for( int i = 0; i < this.tempUpgradeList.size(); i++ ) {
            col = i % COL_COUNT;
            row = i / COL_COUNT;
            currStack = TmrItems.turretUpgrade.getStackWithUpgrade(this.tempUpgradeList.get(i), 1);

            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            GuiUtils.drawGuiIcon(TmrItems.turretUpgrade.getIcon(currStack, 0), this.guiLeft + 7 + col * 26, this.guiTop + 19 + row * 18 + offsetY);
            GuiUtils.drawGuiIcon(TmrItems.turretUpgrade.getIcon(currStack, 1), this.guiLeft + 7 + col * 26, this.guiTop + 19 + row * 18 + offsetY);

            GL11.glEnable(GL11.GL_BLEND);
            this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_UPGRADES.getResource());
            boolean isHoveringOverEject = GuiUtils.isMouseInRect(mouseX, mouseY, this.guiLeft + 24 + col * 26, this.guiTop + 18 + row * 18 + offsetY, 5, 6);
            this.drawTexturedModalRect(this.guiLeft + 24 + col * 26, this.guiTop + 18 + row * 18 + offsetY, 176, 12 + (isHoveringOverEject ? 0 : 6), 5, 6);
            if( isHoveringOverEject && isLmbDown && !this.prevIsLmbDown ) {
                this.ejectUpgrade(this.tempUpgradeList.get(i));
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.prevIsLmbDown = isLmbDown;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        if( this.canScroll ) {
            int dWheelDir = Mouse.getEventDWheel();
            if( dWheelDir < 0 ) {
                this.scroll = Math.min(1.0F, this.scroll + this.scrollAmount);
            } else if( dWheelDir > 0 ) {
                this.scroll = Math.max(0.0F, this.scroll - this.scrollAmount);
            }
        }
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
        return SAPUtils.translatePreFormat("gui.%s.tcu.pageUpgrades.%s", TurretMod.MOD_ID, s);
    }
}
