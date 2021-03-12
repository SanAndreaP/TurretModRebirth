/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.function.Procedure;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.CheckBox;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.ColorPicker;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.ShieldRender;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.ShieldColorizer;
import net.minecraft.util.ResourceLocation;

public class GuiShieldColorizer
        implements IGuiTCU
{
    private ColorPicker  picker;
    private TextField    code;
    private CheckBox     cullFaces;
    private ShieldRender shield;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.picker = gui.getDefinition().getElementById("color-picker").get(ColorPicker.class);
        this.code = gui.getDefinition().getElementById("color-code").get(TextField.class);
        this.cullFaces = gui.getDefinition().getElementById("cull-faces").get(CheckBox.class);
        this.shield = gui.getDefinition().getElementById("shield").get(ShieldRender.class);

        Procedure callback = () -> {
            int clr = this.picker.getColor();
            this.code.setText(String.format("%08X", clr));
            this.shield.setColor(clr);
        };
        this.picker.setOnChangeCallback(() -> {
            callback.work();
            this.syncColor(gui.getTurretInst());
        });

        this.code.setValidator(s -> MiscUtils.getInteger(s) != null);
        this.code.setResponder(s -> {
            int clr = MiscUtils.defIfNull(MiscUtils.getInteger(s), 0);
            this.picker.setColor(clr);
            this.shield.setColor(clr);
            this.syncColor(gui.getTurretInst());
        });

        this.cullFaces.setOnCheckedChanged(byUser -> {
            if( byUser ) {
                this.syncColor(gui.getTurretInst());
            }
        });

        ShieldColorizer settings = getSettings(gui);
        if( settings != null ) {
            this.picker.setColor(settings.getColor());
            this.cullFaces.setChecked(settings.doCullFaces(), false);
        } else {
            this.picker.setColor(0x80FFFFFF);
        }

        callback.work();
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_COLORIZER.resource;
    }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_COLORIZER);
    }

    private static ShieldColorizer getSettings(IGuiTcuInst<?> gui) {
        ShieldColorizer settings = gui.getTurretInst().getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_COLORIZER.getId());
        if( settings == null ) {
            gui.getGui().mc.player.closeScreen();
        }
        return settings;
    }

    private void syncColor(ITurretInst turretInst) {
        ShieldColorizer settings = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.SHIELD_COLORIZER.getId());
        if( settings != null ) {
            settings.setColor(this.picker.getColor());
            settings.setCullFaces(this.cullFaces.isChecked());
        }
        UpgradeRegistry.INSTANCE.syncWithServer(turretInst, Upgrades.SHIELD_COLORIZER.getId());
    }
}
