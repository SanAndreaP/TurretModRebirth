/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiTcuScreen
        extends GuiScreen
        implements IGuiTcuInst<GuiTcuScreen>
{
    private final ResourceLocation entryKey;
    private final ITurretInst turret;
    private final IGuiTCU guiDelegate;
    private final GuiTcuHelper helper = new GuiTcuHelper();

    private int posX;
    private int posY;
    private int xSize;
    private int ySize;

    private GuiDefinition guiDef;

    public GuiTcuScreen(ResourceLocation entryKey, IGuiTCU gui, ITurretInst turretInst) {
        super();
        this.entryKey = entryKey;
        this.turret = turretInst;
        this.guiDelegate = gui;

        try {
            this.guiDef = GuiDefinition.getNewDefinition(this.guiDelegate.getGuiDefinition());
            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;

            helper.checkRequiredElements(this.guiDef);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiHelper.initGuiDef(this.guiDef, this);

        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;

        this.buttonList.clear();

        this.guiDelegate.initialize(this);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.helper.updateScreen(this.mc, this);

        this.guiDef.update(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawGDBackground(this.guiDef, this, partialTicks, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0);
        this.guiDef.drawForeground(this, mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDef.mouseClicked(this, mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        this.guiDef.mouseClickMove(this, mouseX, mouseY, mouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.guiDef.mouseReleased(this, mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiDef.handleMouseInput(this);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if( !this.guiDef.keyTyped(this, typedChar, keyCode) ) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.guiDef.guiClosed(this);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public GuiTcuScreen getGui() {
        return this;
    }

    @Override
    public ITurretInst getTurretInst() {
        return this.turret;
    }

    @Override
    public int getPosX() {
        return this.posX;
    }

    @Override
    public int getPosY() {
        return this.posY;
    }

    @Override
    public int getWidth() {
        return this.xSize;
    }

    @Override
    public int getHeight() {
        return this.ySize;
    }

    @Override
    public boolean hasPermision() {
        return this.helper.hasPermission(this.mc, this.turret);
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    @Override
    public void drawGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        this.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    @Override
    public ResourceLocation getCurrentEntryKey() {
        return this.entryKey;
    }

    @Override
    public GuiScreen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.posX;
    }

    @Override
    public int getScreenPosY() {
        return this.posY;
    }

    @Override
    public boolean performAction(IGuiElement element, int action) {
        return this.guiDelegate.onElementAction(element, action);
    }
}
