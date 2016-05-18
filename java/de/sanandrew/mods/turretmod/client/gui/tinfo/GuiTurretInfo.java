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
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class GuiTurretInfo
        extends GuiScreen
{
    private int guiLeft;
    private int guiTop;

    public float timeDelta;
    private float lastTime;

    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - 192) / 2;
        this.guiTop = (this.height - 256) / 2;

        this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), this.guiLeft + 12, this.guiTop + 24, Resources.TINFO_GRP_TURRET, this));
        this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), this.guiLeft + 12 + 32, this.guiTop + 24, Resources.TINFO_GRP_AMMO, this));
        this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), this.guiLeft + 12 + 32*2, this.guiTop + 24, Resources.TINFO_GRP_UPGRADE, this));
        this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), this.guiLeft + 12 + 32*3, this.guiTop + 24, Resources.TINFO_GRP_MISC, this));
        this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), this.guiLeft + 12 + 32*4, this.guiTop + 24, Resources.TINFO_GRP_INFO, this));
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
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 192, 256);

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
