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
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiTcuContainer
        extends GuiContainer
        implements IGuiTcuInst<GuiTcuContainer>
{
    private final ResourceLocation entryKey;
    private final ITurretInst turret;
    private final IGuiTCU guiDelegate;
    private final GuiTcuHelper helper = new GuiTcuHelper();

    private float currPartTicks;
    private GuiDefinition guiDef;

    public GuiTcuContainer(ResourceLocation entryKey, IGuiTCU gui, Container guiContainer, ITurretInst turretInst) {
        super(guiContainer);
        this.entryKey = entryKey;
        this.turret = turretInst;
        this.guiDelegate = gui;

        try {
            this.guiDef = GuiDefinition.getNewDefinition(this.guiDelegate.getGuiDefinition());
            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }

//        this.xSize = GuiTcuHelper.X_SIZE;
//        this.ySize = GuiTcuHelper.Y_SIZE;
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiHelper.initGuiDef(this.guiDef, this);

        this.buttonList.clear();
//        this.helper.initGui(this);

        this.guiDelegate.initialize(this);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.helper.updateScreen(this.mc, this);
        this.guiDelegate.updateScreen(this);

        this.guiDef.update(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.currPartTicks = partialTicks;
        GuiHelper.drawGDBackground(this.guiDef, this, partialTicks, mouseX, mouseY);

//        this.guiDelegate.drawBackground(this, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        this.guiDef.drawForeground(this, mouseX, mouseY, this.currPartTicks);
        RenderHelper.enableGUIStandardItemLighting();
//        this.guiDelegate.drawForeground(this, mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiDef.mouseClicked(this, mouseX, mouseY, mouseButton);
//        this.guiDelegate.onMouseClick(this, mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        this.guiDef.mouseClickMove(this, mouseX, mouseY, mouseButton, timeSinceLastClick);
//        this.guiDelegate.onMouseClickMove(this, mouseX, mouseY, mouseButton, timeSinceLastClick);
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
//        this.helper.onButtonClick(this, button);
//        this.guiDelegate.onButtonClick(this, button);
    }

    @Override
    public void onGuiClosed() {
        this.guiDelegate.onGuiClose(this);
        super.onGuiClosed();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiDef.handleMouseInput(this);
//        this.guiDelegate.onMouseInput(this);
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
        return this.guiLeft;
    }

    @Override
    public int getPosY() {
        return this.guiTop;
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
    @Deprecated
    public <U extends GuiButton> U addNewButton(U button) {
        this.buttonList.add(button);
        return button;
    }

    @Override
    @Deprecated
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
        return this.guiLeft;
    }

    @Override
    public int getScreenPosY() {
        return this.guiTop;
    }

    @Override
    public void performAction(IGuiElement element, int action) {
        this.guiDelegate.onElementAction(element, action);
    }
}
