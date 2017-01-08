/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTargets;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.PlayerList;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

public class GuiTcuPlayerTargets
        extends GuiScreen
        implements GuiTurretCtrlUnit
{
    private EntityTurret turret;

    private int guiLeft;
    private int guiTop;

    private Map<UUID, Boolean> tempTargetList = new HashMap<>();

    private float scroll = 0.0F;
    private float scrollAmount = 0.0F;
    private boolean isScrolling;
    private boolean canScroll;
    private boolean prevIsLmbDown;

    private boolean doSelectAll;
    private boolean doDeselectAll;

    private GuiButton selectAll;
    private GuiButton deselectAll;

    public GuiTcuPlayerTargets(EntityTurret turret) {
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

        int center = this.guiLeft + (GuiTCUHelper.X_SIZE - 150) / 2;
        this.buttonList.add(this.selectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("selectAll"))));
        this.buttonList.add(this.deselectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("deselectAll"))));

        GuiTCUHelper.pagePlayerTargets.enabled = false;

        this.updateList();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( this.turret.isDead ) {
            this.mc.player.closeScreen();
        }

        this.canScroll = this.tempTargetList.size() >= 11;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.tempTargetList.size() - 11.0F));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = this.guiLeft + 163 + 9;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = this.guiTop + 134;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollMinX && mouseX < scrollMaxX && mouseY > scrollMinY && mouseY < scrollMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollMinY) / 109.0F));
        }

        this.mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GuiTCUHelper.X_SIZE, GuiTCUHelper.Y_SIZE);
        this.drawTexturedModalRect(this.guiLeft + 163, this.guiTop + 19 + MathHelper.floor_float(scroll * 109.0F), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.glScissor(this.guiLeft + 6, this.guiTop + 19, GuiTCUHelper.X_SIZE - 23, 115);

        int offsetY = Math.round(-this.scroll * (this.tempTargetList.size() - 11)) * (this.fontRendererObj.FONT_HEIGHT + 1);
        boolean targetListChanged = false;

        for( Entry<UUID, Boolean> entry : this.tempTargetList.entrySet() ) {
            int btnTexOffY = 12 + (entry.getValue() ? 16 : 0);
            int btnMinOffY = this.guiTop + 20;
            int btnMaxOffY = this.guiTop + 20 + 110;

            if( this.doSelectAll && !entry.getValue() ) {
                this.turret.getTargetProcessor().updatePlayerTarget(entry.getKey(), true);
                targetListChanged = true;
            } else if( this.doDeselectAll && entry.getValue() ) {
                this.turret.getTargetProcessor().updatePlayerTarget(entry.getKey(), false);
                targetListChanged = true;
            }

            if( mouseY >= btnMinOffY && mouseY < btnMaxOffY ) {
                if( mouseX >= this.guiLeft + 8 && mouseX < this.guiLeft + 16 && mouseY >= this.guiTop + 20 + offsetY && mouseY < this.guiTop + 28 + offsetY ) {
                    btnTexOffY += 8;
                    if( isLmbDown && !this.prevIsLmbDown ) {
                        this.turret.getTargetProcessor().updatePlayerTarget(entry.getKey(), !entry.getValue());
                        targetListChanged = true;
                    }
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());
            this.drawTexturedModalRect(this.guiLeft + 8, this.guiTop + 20 + offsetY, 176, btnTexOffY, 8, 8);

            int textColor = 0xFFFFFF;

            this.fontRendererObj.drawString(PlayerList.INSTANCE.getPlayerName(entry.getKey()), this.guiLeft + 18, this.guiTop + 21 + offsetY, textColor, false);

            offsetY += this.fontRendererObj.FONT_HEIGHT + 1;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.doSelectAll = false;
        this.doDeselectAll = false;

        if( targetListChanged ) {
            this.updateTargets();
            this.updateList();
        }

        this.prevIsLmbDown = isLmbDown;

        GuiTCUHelper.drawScreen(this);

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
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
    protected void actionPerformed(GuiButton button) throws IOException {
        if( button == this.selectAll ) {
            this.doSelectAll = true;
        } else if( button == this.deselectAll ) {
            this.doDeselectAll = true;
        } else if( !GuiTCUHelper.actionPerformed(button, this) ) {
            super.actionPerformed(button);
        }
    }

    private void updateTargets() {
        PacketRegistry.sendToServer(new PacketUpdateTargets(this.turret.getTargetProcessor()));
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

    private static final class TargetComparatorName
            implements Comparator<UUID>
    {
        @Override
        public int compare(UUID o1, UUID o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }

    private void updateList() {
        TreeMap<UUID, Boolean> btwSortMapNm = new TreeMap<>(new TargetComparatorName());
        btwSortMapNm.putAll(PlayerList.INSTANCE.getDefaultPlayerList());
        btwSortMapNm.putAll(this.turret.getTargetProcessor().getPlayerTargets());
        this.tempTargetList = btwSortMapNm;
    }
}
