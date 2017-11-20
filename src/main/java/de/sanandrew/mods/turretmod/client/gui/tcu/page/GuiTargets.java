/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTargets;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class GuiTargets<T>
        implements IGuiTCU
{
    protected static final int MAX_ITEMS = 11;
    protected Map<T, Boolean> tempTargetList = new HashMap<>();

    private float scroll = 0.0F;
    private float scrollAmount = 0.0F;
    private boolean isScrolling;
    private boolean canScroll;
    private boolean prevIsLmbDown;

    protected GuiButton selectAll;
    protected GuiButton deselectAll;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        int center = gui.getPosX() + (gui.getGuiWidth() - 150) / 2;
        this.selectAll = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 138, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("selectAll"))));
        this.deselectAll = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 151, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("deselectAll"))));

        this.updateList(gui.getTurretInst());
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.canScroll = this.tempTargetList.size() >= MAX_ITEMS;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.tempTargetList.size() - (float) MAX_ITEMS));
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollMinX = gui.getPosX() + 163;
        int scrollMaxX = gui.getPosX() + 163 + 9;
        int scrollMinY = gui.getPosY() + 19;
        int scrollMaxY = gui.getPosY() + 134;
        GuiScreen guiInst = gui.getGui();

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollMinX && mouseX < scrollMaxX && mouseY > scrollMinY && mouseY < scrollMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollMinY) / 109.0F));
        }

        gui.getGui().mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        guiInst.drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getGuiWidth(), gui.getGuiHeight());
        guiInst.drawTexturedModalRect(gui.getPosX() + 163, gui.getPosY() + 19 + MathHelper.floor(scroll * 109.0F), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.glScissor(gui.getPosX() + 6, gui.getPosY() + 19, gui.getGuiWidth() - 23, 115);

        int offsetY = Math.round(-this.scroll * (this.tempTargetList.size() - 11)) * (gui.getFontRenderer().FONT_HEIGHT + 1);
        boolean targetListChanged = false;

        for( Map.Entry<T, Boolean> entry : this.tempTargetList.entrySet() ) {
            int btnTexOffY = 12 + (entry.getValue() ? 16 : 0);
            int btnMinOffY = gui.getPosY() + 20;
            int btnMaxOffY = gui.getPosY() + 20 + 110;

            if( mouseY >= btnMinOffY && mouseY < btnMaxOffY ) {
                if( mouseX >= gui.getPosX() + 8 && mouseX < gui.getPosX() + 16 && mouseY >= gui.getPosY() + 20 + offsetY && mouseY < gui.getPosY() + 28 + offsetY ) {
                    btnTexOffY += 8;
                    if( isLmbDown && !this.prevIsLmbDown ) {
                        this.updateEntry(gui.getTurretInst(), entry.getKey(), !entry.getValue());
                        targetListChanged = true;
                    }
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            guiInst.mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());
            guiInst.drawTexturedModalRect(gui.getPosX() + 8, gui.getPosY() + 20 + offsetY, 176, btnTexOffY, 8, 8);

            this.drawEntry(gui, entry.getKey(), gui.getPosX() + 20, gui.getPosY());

            offsetY += gui.getFontRenderer().FONT_HEIGHT + 1;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if( targetListChanged ) {
            updateTargets(gui.getTurretInst());
        }

        this.prevIsLmbDown = isLmbDown;
    }

    @Override
    public void onMouseInput(IGuiTcuInst<?> gui) throws IOException {
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
    public void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {
        if( button == this.selectAll ) {
            this.tempTargetList.forEach((key, val) -> this.updateEntry(gui.getTurretInst(), key, true));
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.deselectAll ) {
            this.tempTargetList.forEach((key, val) -> this.updateEntry(gui.getTurretInst(), key, false));
            this.updateTargets(gui.getTurretInst());
        }
    }

    protected void updateTargets(ITurretInst turretInst) {
        PacketRegistry.sendToServer(new PacketUpdateTargets(turretInst.getTargetProcessor()));
        this.updateList(turretInst);
    }

    private void updateList(ITurretInst turretInst) {
        this.tempTargetList = getTargetList(turretInst);
    }

    protected abstract Map<T, Boolean> getTargetList(ITurretInst turretInst);

    protected abstract void updateEntry(ITurretInst turretInst, T type, boolean active);

    protected abstract void drawEntry(IGuiTcuInst<?> gui, T type, int posX, int posY);

}
