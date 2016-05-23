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
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class GuiTurretInfo
        extends GuiScreen
{
    private static final int X_SIZE = 192;
    private static final int Y_SIZE = 236;
    private static final int MAX_ENTRY_HEIGHT = 183;

    private int guiLeft;
    private int guiTop;

    public float timeDelta;
    private float lastTime;

    public final TurretInfoCategory category;
    public final TurretInfoEntry entry;

    public float scroll = 0.0F;

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
        float time = ClientTickHandler.ticksInGame + partTicks;
        this.timeDelta = time - this.lastTime;
        this.lastTime = time;

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(Resources.GUI_TURRETINFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, X_SIZE, Y_SIZE);

        if( this.entry != null ) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            TmrClientUtils.doGlScissor(this.guiLeft + 9, this.guiTop + 19, TurretInfoEntry.MAX_ENTRY_WIDTH, MAX_ENTRY_HEIGHT);
            GL11.glTranslatef(this.guiLeft + 9.0F, this.guiTop + 19.0F, 0.0F);

            this.entry.drawPage(this, mouseX, mouseY, partTicks);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button instanceof GuiButtonCategory ) {
            TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, -1, 0);
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
