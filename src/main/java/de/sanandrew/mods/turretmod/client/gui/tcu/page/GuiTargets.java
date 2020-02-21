/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.target.TargetList;
import de.sanandrew.mods.turretmod.client.gui.tcu.TargetType;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.util.ResourceLocation;

public class GuiTargets
        implements IGuiTCU
{
    private static final int ACTION_TOGGLE_BLACKLIST = 0;
    private static final int ACTION_SELECT_ALL       = 1;
    private static final int ACTION_DESELECT_ALL     = 2;
    private static final int ACTION_SELECT_MOBS      = 3;
    private static final int ACTION_SELECT_ANIMALS   = 4;
    private static final int ACTION_SELECT_OTHER     = 5;

    private final TargetType<?> type;

    private GuiElementInst blacklist;
    private GuiElementInst whitelist;

    private GuiElementInst targetList;
    private TextField      search;

    public GuiTargets(TargetType<?> type) {
        this.type = type;
    }

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.targetList = guiDefinition.getElementById("targetList");
        this.blacklist = guiDefinition.getElementById("blacklist");
        this.whitelist = guiDefinition.getElementById("whitelist");
        this.search = guiDefinition.getElementById("search").get(TextField.class);
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        ITurretInst turretInst = gui.getTurretInst();

        boolean isBlacklist = this.type.isBlacklist(turretInst);

        this.blacklist.setVisible(isBlacklist);
        this.whitelist.setVisible(!isBlacklist);
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        if( this.type == TargetType.CREATURE ) {
            return Resources.GUI_STRUCT_TCU_TARGET_CREATURES.resource;
        } else if( this.type == TargetType.PLAYER ) {
            return Resources.GUI_STRUCT_TCU_TARGET_PLAYERS.resource;
        }
        return null;
    }

    @Override
    public boolean onElementAction(IGuiTcuInst<?> gui, IGuiElement element, int action) {
        ITurretInst turretInst = gui.getTurretInst();
        switch( action ) {
            case ACTION_TOGGLE_BLACKLIST:
                this.type.toggleBlacklist(turretInst);
                break;
            case ACTION_SELECT_ALL:
                this.type.toggleAllTargets(null, turretInst, true);
                break;
            case ACTION_DESELECT_ALL:
                this.type.toggleAllTargets(null, turretInst, false);
                break;
            case ACTION_SELECT_MOBS:
                this.type.toggleAllTargets(TargetType.EntityType.HOSTILE, turretInst, true);
                break;
            case ACTION_SELECT_ANIMALS:
                this.type.toggleAllTargets(TargetType.EntityType.PEACEFUL, turretInst, true);
                break;
            case ACTION_SELECT_OTHER:
                this.type.toggleAllTargets(TargetType.EntityType.NEUTRAL, turretInst, true);
                break;
        }

        return false;
    }

    @Override
    public void keyTyped(IGuiTcuInst<?> gui, char typedChar, int keyCode) {
        if( this.search.isFocused() ) {
            this.targetList.get(TargetList.class).rebuild(gui, this.targetList.data, this.search.getText());
        }
    }
}
