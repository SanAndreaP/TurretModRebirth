package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTurretProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.turret.forcefield.TurretForcefield;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiTurretInfo
        extends GuiScreen
        implements IGui, IGuiTurretProvider
{
    private static final int ACTION_ACTIVATE   = 1;
    private static final int ACTION_DEACTIVATE = 2;
    private static final int ACTION_RANGE_SHOW = 3;
    private static final int ACTION_RANGE_HIDE = 4;

    private final ITurretInst turretInst;

    private int posX;
    private int posY;
    private int xSize;
    private int ySize;

    private GuiDefinition guiDef;

    private GuiElementInst setActive;
    private GuiElementInst setDeactive;
    private GuiElementInst showRange;
    private GuiElementInst hideRange;

    public GuiTurretInfo(ITurretInst turretInst) {
        super();

        this.turretInst = turretInst;

        try {
            this.guiDef = GuiDefinition.getNewDefinition(turretInst.getTurret() instanceof TurretForcefield
                                                         ? Resources.GUI_STRUCT_TINFO_FORCEFIELD.resource
                                                         : Resources.GUI_STRUCT_TINFO.resource);

            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;
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

        this.setActive = this.guiDef.getElementById("activate");
        this.setDeactive = this.guiDef.getElementById("deactivate");
        this.showRange = this.guiDef.getElementById("showRange");
        this.hideRange = this.guiDef.getElementById("hideRange");

        this.setActive.setVisible(false);
        this.hideRange.setVisible(false);

        if( !this.getTurretInst().hasPlayerPermission(mc.player) ) {
            this.setActive.get(Button.class).setEnabled(false);
            this.setDeactive.get(Button.class).setEnabled(false);
            this.showRange.get(Button.class).setEnabled(false);
            this.hideRange.get(Button.class).setEnabled(false);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        this.checkGuiClose();

        this.guiDef.update(this);

        this.setDeactive.setVisible(this.turretInst.isActive());
        this.setActive.setVisible(!this.setDeactive.isVisible());
        this.hideRange.setVisible(this.turretInst.showRange());
        this.showRange.setVisible(!this.hideRange.isVisible());
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
        if( keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) ) {
            this.mc.player.closeScreen();
        }

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
        switch( action ) {
            case ACTION_RANGE_SHOW:
                this.turretInst.setShowRange(true);
                this.turretInst.get().ignoreFrustumCheck = true;
                return true;
            case ACTION_RANGE_HIDE:
                this.turretInst.setShowRange(false);
                this.turretInst.get().ignoreFrustumCheck = false;
                return true;
            case ACTION_ACTIVATE:
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(this.turretInst, PacketPlayerTurretAction.SET_ACTIVE));
                return true;
            case ACTION_DEACTIVATE:
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(this.turretInst, PacketPlayerTurretAction.SET_DEACTIVE));
                return true;
        }

        return false;
    }

    @Override
    public ITurretInst getTurretInst() {
        return this.turretInst;
    }

    private void checkGuiClose() {
        EntityLivingBase turretL = this.turretInst.get();

        if( turretL.isDead || turretL.getDistance(this.mc.player) > 6.0D ) {
            this.mc.player.closeScreen();
        }
    }
}
