/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;

import java.io.IOException;

public class GuiTcuContainer
        extends GuiContainer
        implements IGuiTcuInst<GuiTcuContainer>
{
    private final ITurretInst turret;
    private final IGuiTCU guiDelegate;
    private final GuiTCUHelper helper = new GuiTCUHelper();

    private int posX;
    private int posY;

    public GuiTcuContainer(IGuiTCU gui, Container guiContainer, ITurretInst turretInst) {
        super(guiContainer);
        this.turret = turretInst;
        this.guiDelegate = gui;

        this.xSize = GuiTCUHelper.X_SIZE;
        this.ySize = GuiTCUHelper.Y_SIZE;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;

        this.buttonList.clear();
        this.helper.initGui(this);

        this.guiDelegate.initGui(this);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.helper.updateScreen(this.mc, this);
        this.guiDelegate.updateScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.guiDelegate.drawBackground(this, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-this.posX, -this.posY, 0.0F);
        this.helper.drawScreen(this);
        GlStateManager.popMatrix();

        this.guiDelegate.drawForeground(this, mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDelegate.onMouseClick(this, mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if( !this.guiDelegate.doKeyIntercept(this, typedChar, keyCode) ) {
            super.keyTyped(typedChar, keyCode);
            this.guiDelegate.onKeyType(this, typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        this.helper.onButtonClick(this, button);
        this.guiDelegate.onButtonClick(this, button);
    }

    @Override
    public void onGuiClosed() {
        this.guiDelegate.onGuiClose(this);
        super.onGuiClosed();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiDelegate.onMouseInput(this);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public GuiTcuContainer getGui() {
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
    public int getGuiWidth() {
        return this.xSize;
    }

    @Override
    public int getGuiHeight() {
        return this.ySize;
    }

    @Override
    public boolean hasPermision() {
        return this.helper.hasPermission(this.mc, this.turret);
    }

    @Override
    public <U extends GuiButton> U addNewButton(U button) {
        this.buttonList.add(button);
        return button;
    }

    @Override
    public int getNewButtonId() {
        return this.buttonList.size();
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    @Override
    public void drawGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        this.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }
}
