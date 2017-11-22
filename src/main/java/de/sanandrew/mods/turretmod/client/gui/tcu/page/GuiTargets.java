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
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTargets;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@SideOnly(Side.CLIENT)
public abstract class GuiTargets<T>
        implements IGuiTCU
{
    protected static final int MAX_ITEMS = 13;
    protected SortedMap<T, Boolean> tempTargets = new TreeMap<>();
    protected SortedMap<T, Boolean> filteredTargets = new TreeMap<>();

    private float scroll = 0.0F;
    private float scrollAmount = 0.0F;
    private boolean isScrolling;
    private boolean canScroll;
    private boolean prevIsLmbDown;

    protected GuiButton whitelist;
    protected GuiButton blacklist;
    protected GuiButton selectAll;
    protected GuiButton deselectAll;

    private GuiTextField searchBar;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        this.whitelist = gui.addNewButton(new GuiButtonTargetIcon(gui.getNewButtonId(), gui.getPosX() + 7, gui.getPosY() + 190, 184, 0, Lang.translate(Lang.TCU_BTN.get("whitelist"))));
        this.blacklist = gui.addNewButton(new GuiButtonTargetIcon(gui.getNewButtonId(), gui.getPosX() + 7, gui.getPosY() + 190, 202, 0, Lang.translate(Lang.TCU_BTN.get("blacklist"))));
        this.selectAll = gui.addNewButton(new GuiButtonTargetIcon(gui.getNewButtonId(), gui.getPosX() + 26, gui.getPosY() + 190, 220, 0, Lang.translate(Lang.TCU_BTN.get("selectAll"))));
        this.deselectAll = gui.addNewButton(new GuiButtonTargetIcon(gui.getNewButtonId(), gui.getPosX() + 45, gui.getPosY() + 190, 238, 0, Lang.translate(Lang.TCU_BTN.get("deselectAll"))));

        this.searchBar = new GuiTextField(0, gui.getFontRenderer(), gui.getPosX() + 8, gui.getPosY() + 40, 160, 10);
        this.searchBar.setMaxStringLength(1024);
        this.searchBar.setText("");

        this.updateList(gui.getTurretInst());
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.blacklist.visible = this.blacklist.enabled = this.isBlacklist(gui.getTurretInst());
        this.whitelist.visible = this.whitelist.enabled = !this.blacklist.visible;

        this.canScroll = this.filteredTargets.size() > MAX_ITEMS;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.filteredTargets.size() - (float) MAX_ITEMS));

        this.searchBar.updateCursorCounter();
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollHeight = 129;
        int scrollAreaX = gui.getPosX() + 8;
        int scrollAreaY = gui.getPosY() + 53;
        int scrollAreaWidth = 153;
        int scrollAreaHeight = 135;
        int scrollBarMinX = gui.getPosX() + 162;
        int scrollBarMaxX = scrollBarMinX + 9;
        int scrollBarMaxY = scrollAreaY + scrollAreaHeight;
        GuiScreen guiInst = gui.getGui();

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollBarMinX && mouseX < scrollBarMaxX && mouseY > scrollAreaY && mouseY < scrollBarMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollAreaY) / (float)scrollHeight));
        }

        gui.getGui().mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        guiInst.drawTexturedModalRect(gui.getPosX(), gui.getPosY(), 0, 0, gui.getWidth(), gui.getHeight());
        guiInst.drawTexturedModalRect(scrollBarMinX, scrollAreaY + MathHelper.floor(this.scroll * scrollHeight), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.glScissor(scrollAreaX, scrollAreaY, scrollAreaWidth, scrollAreaHeight);

        int offsetY = Math.round(-this.scroll * (this.filteredTargets.size() - MAX_ITEMS)) * (gui.getFontRenderer().FONT_HEIGHT + 1);
        int btnMinOffY = scrollAreaY + 1;
        int btnMaxOffY = btnMinOffY + MAX_ITEMS * (gui.getFontRenderer().FONT_HEIGHT + 1);
        boolean targetListChanged = false;

        for( Map.Entry<T, Boolean> entry : this.filteredTargets.entrySet() ) {
            int btnTexOffY = 12 + (entry.getValue() ? 16 : 0);

            if( mouseY >= btnMinOffY && mouseY < btnMaxOffY ) {
                if( mouseX >= scrollAreaX + 1 && mouseX < scrollAreaX + 9 && mouseY >= scrollAreaY + 1 + offsetY && mouseY < scrollAreaY + 9 + offsetY ) {
                    btnTexOffY += 8;
                    if( isLmbDown && !this.prevIsLmbDown ) {
                        this.updateEntry(gui.getTurretInst(), entry.getKey(), !entry.getValue());
                        targetListChanged = true;
                    }
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            guiInst.mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());
            guiInst.drawTexturedModalRect(scrollAreaX + 1, scrollAreaY + 1 + offsetY, 176, btnTexOffY, 8, 8);

            this.drawEntry(gui, entry.getKey(), scrollAreaX + 10, scrollAreaY + 2 + offsetY);

            offsetY += gui.getFontRenderer().FONT_HEIGHT + 1;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if( targetListChanged ) {
            updateTargets(gui.getTurretInst());
        }

        this.prevIsLmbDown = isLmbDown;

        this.searchBar.drawTextBox();
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
            this.tempTargets.forEach((key, val) -> this.updateEntry(gui.getTurretInst(), key, true));
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.deselectAll ) {
            this.tempTargets.forEach((key, val) -> this.updateEntry(gui.getTurretInst(), key, false));
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.whitelist ) {
            this.setBlacklist(gui.getTurretInst(), true);
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.blacklist ) {
            this.setBlacklist(gui.getTurretInst(), false);
            this.updateTargets(gui.getTurretInst());
        }
    }

    @Override
    public void onMouseClick(IGuiTcuInst<?> gui, int mouseX, int mouseY, int mouseButton) throws IOException {
        this.searchBar.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doKeyIntercept(IGuiTcuInst<?> gui, char typedChar, int keyCode) throws IOException {
        if( this.searchBar.textboxKeyTyped(typedChar, keyCode) ) {
            this.filterList();
            this.scroll = 0.0F;
            return true;
        }

        return false;
    }

    protected void updateTargets(ITurretInst turretInst) {
        PacketRegistry.sendToServer(new PacketUpdateTargets(turretInst.getTargetProcessor()));
        this.updateList(turretInst);
    }

    private void updateList(ITurretInst turretInst) {
        this.tempTargets = getTargetList(turretInst);
        this.filterList();
    }

    private void filterList() {
        this.filteredTargets = new TreeMap<>(this.tempTargets.comparator());
        this.tempTargets.entrySet().stream()
                        .filter(e -> isEntryVisible(e.getKey(), this.searchBar.getText()))
                        .forEach(e -> this.filteredTargets.put(e.getKey(), e.getValue()));
    }

    protected abstract SortedMap<T, Boolean> getTargetList(ITurretInst turretInst);

    protected abstract void updateEntry(ITurretInst turretInst, T type, boolean active);

    protected abstract boolean isEntryVisible(T type, String srcText);

    protected abstract void drawEntry(IGuiTcuInst<?> gui, T type, int posX, int posY);

    protected abstract boolean isBlacklist(ITurretInst turretInst);

    protected abstract void setBlacklist(ITurretInst turretInst, boolean isBlacklist);
}
