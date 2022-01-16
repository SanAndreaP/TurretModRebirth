package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.ErrorTooltip;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.Objects;

public class TcuInfoPage
        extends JsonTcuPage
{
    private final ITurretEntity turret;
    private final boolean isRemote;

    private ButtonSL dismantle;
    private ButtonSL setActive;
    private ButtonSL setInactive;
    private ButtonSL showRange;
    private ButtonSL hideRange;

    public TcuInfoPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);

        this.turret = tcuScreen.getMenu().turret;
        this.isRemote = tcuScreen.getMenu().isRemote;

        if( this.guiDefinition != null ) {
            this.guiDefinition.width = tcuScreen.getXSize();
            this.guiDefinition.height = tcuScreen.getYSize();
        }
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_INFO);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        final ErrorTooltip errorDismantle = this.guiDefinition.getElementById("errorDismantle").get(ErrorTooltip.class);

        this.dismantle = this.guiDefinition.getElementById("dismantle").get(ButtonSL.class);
        this.dismantle.setFunction(btn -> {
            if( !TurretPlayerActionPacket.tryDismantle(Objects.requireNonNull(this.mc.player), this.turret) ) {
                errorDismantle.activate();
            } else {
                this.mc.setScreen(null);
            }
        });

        this.setActive = this.guiDefinition.getElementById("activate").get(ButtonSL.class);
        this.setActive.setVisible(false);
        this.setActive.setFunction(btn -> TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(this.turret, TurretPlayerActionPacket.SET_ACTIVE)));

        this.setInactive = this.guiDefinition.getElementById("deactivate").get(ButtonSL.class);
        this.setInactive.setFunction(btn -> TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(this.turret, TurretPlayerActionPacket.SET_DEACTIVE)));

        this.showRange = this.guiDefinition.getElementById("showRange").get(ButtonSL.class);
        this.showRange.setFunction(btn -> this.turret.setShowRange(true));

        this.hideRange = this.guiDefinition.getElementById("hideRange").get(ButtonSL.class);
        this.hideRange.setFunction(btn -> this.turret.setShowRange(false));
        this.hideRange.setVisible(false);

        if( !this.turret.hasPlayerPermission(this.mc.player) ) {
            this.dismantle.setActive(false);
            this.setActive.setActive(false);
            this.setInactive.setActive(false);
            this.showRange.setActive(false);
            this.hideRange.setActive(false);
        } else {
            if( this.isRemote && !this.turret.canRemoteTransfer() ) {
                this.dismantle.setActive(false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.setInactive.setVisible(this.turret.isActive());
        this.setActive.setVisible(!this.setInactive.isVisible());
        this.hideRange.setVisible(this.turret.shouldShowRange());
        this.showRange.setVisible(!this.hideRange.isVisible());

        if( this.isRemote && !this.turret.canRemoteTransfer() ) { // prevent external upgrade removal from this staying enabled
            this.dismantle.setActive(false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if( keyCode == GLFW.GLFW_KEY_ESCAPE ) {
            return false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public ITurretEntity getTurret() {
        return this.turret;
    }
}
