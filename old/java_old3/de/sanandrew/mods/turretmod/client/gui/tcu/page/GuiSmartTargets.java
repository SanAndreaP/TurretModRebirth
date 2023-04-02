/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting.AdvTargetSettings;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class GuiSmartTargets
        implements IGuiTCU
{
    private static final int TURRET_IGNORE = 0;
    private static final int TURRET_CHECK_SAME = 1;
    private static final int TURRET_CHECK_ALL = 2;

    private static final int TAMED_ALL = 3;
    private static final int TAMED_PLAYERS = 4;
    private static final int TAMED_NONE = 5;

    private static final int CHILD_AND_ADULT = 6;
    private static final int CHILD_ONLY = 7;
    private static final int ADULT_ONLY = 8;

    private static final int NO_COUNT = 9;
    private static final int COUNT_GLOBAL_LESS = 10;
    private static final int COUNT_GLOBAL_MORE = 11;
    private static final int COUNT_INDIV_LESS = 12;
    private static final int COUNT_INDIV_MORE = 13;

    private Button turretIgnore;
    private Button turretCheckSame;
    private Button turretCheckAll;

    private Button tamedAll;
    private Button tamedPlayers;
    private Button tamedNone;

    private Button childAndAdult;
    private Button childOnly;
    private Button adultOnly;

    private Button noCount;
    private Button countGlobalLess;
    private Button countGlobalMore;
    private Button countIndivLess;
    private Button countIndivMore;

    private TextField countEntities;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.turretIgnore    = guiDefinition.getElementById("turretIgnore").get(Button.class);
        this.turretCheckSame = guiDefinition.getElementById("turretCheckSame").get(Button.class);
        this.turretCheckAll  = guiDefinition.getElementById("turretCheckAll").get(Button.class);
        this.tamedAll        = guiDefinition.getElementById("tamedAll").get(Button.class);
        this.tamedPlayers    = guiDefinition.getElementById("tamedPlayers").get(Button.class);
        this.tamedNone       = guiDefinition.getElementById("tamedNone").get(Button.class);
        this.childAndAdult   = guiDefinition.getElementById("childAdult").get(Button.class);
        this.childOnly       = guiDefinition.getElementById("childOnly").get(Button.class);
        this.adultOnly       = guiDefinition.getElementById("adultOnly").get(Button.class);
        this.noCount         = guiDefinition.getElementById("noCount").get(Button.class);
        this.countGlobalLess = guiDefinition.getElementById("countGlobalLess").get(Button.class);
        this.countGlobalMore = guiDefinition.getElementById("countGlobalMore").get(Button.class);
        this.countIndivLess  = guiDefinition.getElementById("countIndivLess").get(Button.class);
        this.countIndivMore  = guiDefinition.getElementById("countIndivMore").get(Button.class);

        this.countEntities = guiDefinition.getElementById("countEntities").get(TextField.class);
        this.countEntities.setMaxStringLength(3);
        this.countEntities.setValidator(s -> {
                if( Strings.isNullOrEmpty(s) ) {
                    return true;
                }

                Integer val = getInteger(s);
                return val != null && val >= 0 && val <= 256;
            });
        this.countEntities.setResponder(s -> {
                AdvTargetSettings settings = getSettings(gui);
                if( settings != null ) {
                    Integer val = getInteger(s);
                    if( val != null ) {
                        settings.setCountEntities(val);
                        syncSettings(gui.getTurretInst());
                    }
                }
            });
        AdvTargetSettings settings = getSettings(gui);
        if( settings != null ) {
            this.countEntities.setText(String.format("%d", settings.getCountEntities()));
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        AdvTargetSettings settings = getSettings(gui);
        if( settings != null ) {
            AdvTargetSettings.TurretAwareness tra = settings.getTurretAwareness();
            this.turretIgnore   .setEnabled(tra != AdvTargetSettings.TurretAwareness.UNAWARE);
            this.turretCheckSame.setEnabled(tra != AdvTargetSettings.TurretAwareness.SAME_TYPE);
            this.turretCheckAll .setEnabled(tra != AdvTargetSettings.TurretAwareness.ALL_TYPES);

            AdvTargetSettings.TamedAwareness tma = settings.getTamedAwareness();
            this.tamedAll    .setEnabled(tma != AdvTargetSettings.TamedAwareness.UNAWARE);
            this.tamedPlayers.setEnabled(tma != AdvTargetSettings.TamedAwareness.TARGETED_PLAYERS);
            this.tamedNone   .setEnabled(tma != AdvTargetSettings.TamedAwareness.IGNORE_ALL_TAMED);

            AdvTargetSettings.ChildAwareness cla = settings.getChildAwareness();
            this.childAndAdult.setEnabled(cla != AdvTargetSettings.ChildAwareness.UNAWARE);
            this.childOnly    .setEnabled(cla != AdvTargetSettings.ChildAwareness.CHILDREN_ONLY);
            this.adultOnly    .setEnabled(cla != AdvTargetSettings.ChildAwareness.ADULTS_ONLY);

            AdvTargetSettings.CountAwareness cna = settings.getCountAwareness();
            this.noCount        .setEnabled(cna != AdvTargetSettings.CountAwareness.NO_COUNT);
            this.countGlobalLess.setEnabled(cna != AdvTargetSettings.CountAwareness.BELOW_GLOBAL);
            this.countGlobalMore.setEnabled(cna != AdvTargetSettings.CountAwareness.ABOVE_GLOBAL);
            this.countIndivLess .setEnabled(cna != AdvTargetSettings.CountAwareness.BELOW_INDIVIDUAL);
            this.countIndivMore .setEnabled(cna != AdvTargetSettings.CountAwareness.ABOVE_INDIVIDUAL);

            this.countEntities.setEnabled(cna != AdvTargetSettings.CountAwareness.NO_COUNT);
        }
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_TARGET_SMART.resource;
    }

    @Override
    public boolean onElementAction(IGuiTcuInst<?> gui, IGuiElement element, int action) {
        AdvTargetSettings settings = getSettings(gui);
        switch( action ) {
            case TURRET_IGNORE:     settings.setTurretAwareness(AdvTargetSettings.TurretAwareness.UNAWARE);        syncSettings(gui.getTurretInst()); return true;
            case TURRET_CHECK_SAME: settings.setTurretAwareness(AdvTargetSettings.TurretAwareness.SAME_TYPE);      syncSettings(gui.getTurretInst()); return true;
            case TURRET_CHECK_ALL:  settings.setTurretAwareness(AdvTargetSettings.TurretAwareness.ALL_TYPES);      syncSettings(gui.getTurretInst()); return true;

            case TAMED_ALL:         settings.setTamedAwareness(AdvTargetSettings.TamedAwareness.UNAWARE);          syncSettings(gui.getTurretInst()); return true;
            case TAMED_PLAYERS:     settings.setTamedAwareness(AdvTargetSettings.TamedAwareness.TARGETED_PLAYERS); syncSettings(gui.getTurretInst()); return true;
            case TAMED_NONE:        settings.setTamedAwareness(AdvTargetSettings.TamedAwareness.IGNORE_ALL_TAMED); syncSettings(gui.getTurretInst()); return true;

            case CHILD_AND_ADULT:   settings.setChildAwareness(AdvTargetSettings.ChildAwareness.UNAWARE);          syncSettings(gui.getTurretInst()); return true;
            case CHILD_ONLY:        settings.setChildAwareness(AdvTargetSettings.ChildAwareness.CHILDREN_ONLY);    syncSettings(gui.getTurretInst()); return true;
            case ADULT_ONLY:        settings.setChildAwareness(AdvTargetSettings.ChildAwareness.ADULTS_ONLY);      syncSettings(gui.getTurretInst()); return true;

            case NO_COUNT:          settings.setCountAwareness(AdvTargetSettings.CountAwareness.NO_COUNT);         syncSettings(gui.getTurretInst()); return true;
            case COUNT_GLOBAL_LESS: settings.setCountAwareness(AdvTargetSettings.CountAwareness.BELOW_GLOBAL);     syncSettings(gui.getTurretInst()); return true;
            case COUNT_GLOBAL_MORE: settings.setCountAwareness(AdvTargetSettings.CountAwareness.ABOVE_GLOBAL);     syncSettings(gui.getTurretInst()); return true;
            case COUNT_INDIV_LESS:  settings.setCountAwareness(AdvTargetSettings.CountAwareness.BELOW_INDIVIDUAL); syncSettings(gui.getTurretInst()); return true;
            case COUNT_INDIV_MORE:  settings.setCountAwareness(AdvTargetSettings.CountAwareness.ABOVE_INDIVIDUAL); syncSettings(gui.getTurretInst()); return true;
        }

        return false;
    }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.SMART_TGT);
    }

    private static AdvTargetSettings getSettings(IGuiTcuInst<?> gui) {
        AdvTargetSettings settings = gui.getTurretInst().getUpgradeProcessor().getUpgradeInstance(Upgrades.SMART_TGT.getId());
        if( settings == null ) {
            gui.getGui().mc.player.closeScreen();
        }
        return settings;
    }

    private static void syncSettings(ITurretInst turretInst) {
        UpgradeRegistry.INSTANCE.syncWithServer(turretInst, Upgrades.SMART_TGT.getId());
    }

    private static Integer getInteger(String s) {
        try {
            return Integer.decode(s);
        } catch( NumberFormatException ex ) {
            return null;
        }
    }
}
