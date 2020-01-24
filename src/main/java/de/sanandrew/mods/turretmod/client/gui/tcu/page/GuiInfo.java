/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.info.ErrorLabel;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketTurretNaming;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.util.ResourceLocation;

public class GuiInfo
        implements IGuiTCU
{
    private static final int ACTION_DISMANTLE  = 0;
    private static final int ACTION_ACTIVATE   = 1;
    private static final int ACTION_DEACTIVATE = 2;
    private static final int ACTION_RANGE_SHOW = 3;
    private static final int ACTION_RANGE_HIDE = 4;

    private Button setActive;
    private Button setDeactive;
    private Button showRange;
    private Button hideRange;

    private TextField turretName;

    private ErrorLabel errorDismantle;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        Button dismantle = guiDefinition.getElementById("dismantle").get(Button.class);
        this.setActive = guiDefinition.getElementById("activate").get(Button.class);
        this.setDeactive = guiDefinition.getElementById("deactivate").get(Button.class);
        this.showRange = guiDefinition.getElementById("showRange").get(Button.class);
        this.hideRange = guiDefinition.getElementById("hideRange").get(Button.class);

        this.turretName = guiDefinition.getElementById("turretNameInput").get(TextField.class);

        this.errorDismantle = guiDefinition.getElementById("errorDismantle").get(ErrorLabel.class);

        this.setActive.setVisible(false);
        this.hideRange.setVisible(false);

        this.turretName.setMaxStringLength(128);
        this.turretName.setText(gui.getTurretInst().get().hasCustomName() ? gui.getTurretInst().get().getCustomNameTag() : "");

        if( !gui.hasPermision() ) {
            dismantle.setEnabled(false);
            this.setActive.setEnabled(false);
            this.setDeactive.setEnabled(false);
            this.showRange.setEnabled(false);
            this.hideRange.setEnabled(false);
            this.turretName.setEnabled(false);
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        ITurretInst turretInst = gui.getTurretInst();
        this.setDeactive.setVisible(turretInst.isActive());
        this.setActive.setVisible(!this.setDeactive.isVisible());
        this.hideRange.setVisible(turretInst.showRange());
        this.showRange.setVisible(!this.hideRange.isVisible());
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_INFO.resource;
    }

    @Override
    public boolean onElementAction(IGuiTcuInst<?> gui, IGuiElement element, int action) {
        ITurretInst turretInst = gui.getTurretInst();
        switch( action ) {
            case ACTION_DISMANTLE:
                if( !PacketPlayerTurretAction.tryDismantle(gui.getGui().mc.player, turretInst) ) {
                    this.errorDismantle.activate();
                } else {
                    gui.getGui().mc.player.closeScreen();
                }
                return true;
            case ACTION_RANGE_SHOW:
                turretInst.setShowRange(true);
                turretInst.get().ignoreFrustumCheck = true;
                return true;
            case ACTION_RANGE_HIDE:
                turretInst.setShowRange(false);
                turretInst.get().ignoreFrustumCheck = false;
                return true;
            case ACTION_ACTIVATE:
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.SET_ACTIVE));
                return true;
            case ACTION_DEACTIVATE:
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(turretInst, PacketPlayerTurretAction.SET_DEACTIVE));
                return true;
        }

        return false;
    }

    @Override
    public void guiClosed(IGuiTcuInst<?> gui) {
        PacketRegistry.sendToServer(new PacketTurretNaming(gui.getTurretInst(), this.turretName.getText()));
    }
}
