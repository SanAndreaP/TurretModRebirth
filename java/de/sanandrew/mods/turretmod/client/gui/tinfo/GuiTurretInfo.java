/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiTurretInfo
        extends GuiScreen
{
    private static final int X_SIZE = 192;
    private static final int Y_SIZE = 236;

    private int guiLeft;
    private int guiTop;

    public float timeDelta;
    private float lastTime;

    public final TurretInfoCategory category;
    public final TurretInfoEntry entry;

    public float scroll = 0.0F;
    private int dHeight;
    private boolean isScrolling;

    public GuiTurretInfo(int category, int entry) {
        this.category = category < 0 ? null : TurretInfoCategory.getCategory(category);
        this.entry = entry < 0 ? null : (this.category != null ? this.category.getEntry(entry) : null);
    }

    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - X_SIZE) / 2;
        this.guiTop = (this.height - Y_SIZE) / 2;

        this.buttonList.clear();

        if( this.category == null ) {
            int catLng = TurretInfoCategory.getCategoryCount();
            for( int i = 0; i < catLng; i++ ) {
                this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), i, this.guiLeft + 12 + 32 * i, this.guiTop + 24, this));
            }
        } else if( this.entry == null ) {
            int entLng = this.category.getEntryCount();
            for( int i = 0; i < entLng; i++ ) {
                this.buttonList.add(new GuiButtonEntry(this.buttonList.size(), i, this.guiLeft + 14, this.guiTop + 24 + 20 * i, this));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        boolean mouseDown = Mouse.isButtonDown(0);

        float time = ClientTickHandler.ticksInGame + partTicks;
        this.timeDelta = time - this.lastTime;
        this.lastTime = time;

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(Resources.GUI_TURRETINFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, X_SIZE, Y_SIZE);

        if( this.entry != null ) {
            this.dHeight = this.entry.getPageHeight() - TurretInfoEntry.MAX_ENTRY_HEIGHT;
            GL11.glPushMatrix();
            GL11.glTranslatef(this.guiLeft + 9 + TurretInfoEntry.MAX_ENTRY_WIDTH, this.guiTop + 19, 0.0F);
            drawRect(0, 0, 6, TurretInfoEntry.MAX_ENTRY_HEIGHT, 0x30000000);
            if( this.dHeight > 0 ) {
                drawRect(0, Math.round((TurretInfoEntry.MAX_ENTRY_HEIGHT - 16) * this.scroll), 6, Math.round((TurretInfoEntry.MAX_ENTRY_HEIGHT - 16) * this.scroll + 16), 0x800000FF);
            }
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            this.doEntryScissoring();
            GL11.glTranslatef(this.guiLeft + 9.0F, this.guiTop + 19.0F, 0.0F);
            GL11.glTranslatef(0.0F, Math.round(-this.scroll * this.dHeight), 0.0F);

            this.entry.drawPage(this, mouseX - this.guiLeft - 9, mouseY - this.guiTop - 19, Math.round(this.scroll * this.dHeight), partTicks);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();

            if( !mouseDown && this.isScrolling ) {
                this.isScrolling = false;
            } else if( mouseDown && !this.isScrolling ) {
                if( mouseY >= this.guiTop + 19 && mouseY < this.guiTop + 19 + TurretInfoEntry.MAX_ENTRY_HEIGHT ) {
                    if( mouseX >= this.guiLeft + 9 + TurretInfoEntry.MAX_ENTRY_WIDTH && mouseX < this.guiLeft + 9 + TurretInfoEntry.MAX_ENTRY_WIDTH + 6 ) {
                        this.isScrolling = true;
                    }
                }
            }

            if( this.isScrolling ) {
                int mouseDelta = Math.min(TurretInfoEntry.MAX_ENTRY_HEIGHT - 16, Math.max(0, mouseY - (this.guiTop + 19 + 8)));
                this.scroll = mouseDelta / (TurretInfoEntry.MAX_ENTRY_HEIGHT - 16.0F);
            }
        }

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    public void doEntryScissoring(int x, int y, int width, int height) {
        int prevX = x;
        int yShifted = y - Math.round(this.scroll * this.dHeight);

        int maxWidth = Math.min(width, width - (x + width - TurretInfoEntry.MAX_ENTRY_WIDTH));
        int maxHeight = Math.min(height, height - (y + height - TurretInfoEntry.MAX_ENTRY_HEIGHT) + Math.round(this.scroll * this.dHeight));

        x = this.guiLeft + 9 + Math.max(0, prevX);
        y = this.guiTop + 19 + Math.max(0, yShifted);

        width = Math.max(0, Math.min(maxWidth, width + prevX));
        height = Math.max(0, Math.min(maxHeight, height + yShifted));

        TmrClientUtils.doGlScissor(x, y, width, height);
    }

    public void doEntryScissoring() {
        TmrClientUtils.doGlScissor(this.guiLeft + 9, this.guiTop + 19, TurretInfoEntry.MAX_ENTRY_WIDTH, TurretInfoEntry.MAX_ENTRY_HEIGHT);
    }

    @Override
    public void handleMouseInput() {
        if( this.dHeight > 0 ) {
            int dwheel = Mouse.getEventDWheel() / 120;
            if( dwheel != 0 ) {
                this.scroll = Math.min(1.0F, Math.max(0.0F, (this.scroll * this.dHeight - dwheel * 16.0F) / this.dHeight));
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button instanceof GuiButtonCategory ) {
            if( TurretInfoCategory.getCategory(((GuiButtonCategory) button).catIndex).getEntryCount() == 1 ) {
                TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, 0, 0);
            } else {
                TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, -1, 0);
            }
            return;
        } else if( button instanceof GuiButtonEntry ) {
            TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, this.category.index, ((GuiButtonEntry) button).entIndex, 0);
            return;
        }

        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
