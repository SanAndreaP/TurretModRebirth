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
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.level.LevelIndicator;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.level.LevelModifiers;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import net.minecraft.util.ResourceLocation;

public class GuiLevels
        implements IGuiTCU
{
    private static final int ACTION_RETRIEVE_XP = 0;

    private GuiElementInst lvlIndicator;
    private GuiElementInst lvlModifiers;
    private GuiElementInst btnRetrieveExcess;

    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) {
        this.lvlIndicator = guiDefinition.getElementById("level_indicator");
        this.lvlModifiers = guiDefinition.getElementById("level_modifiers");
        this.btnRetrieveExcess = guiDefinition.getElementById("retrieve_excess_xp");
    }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_LEVELS.resource;
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        ITurretInst turretInst = gui.getTurretInst();
        IUpgradeProcessor processor = turretInst.getUpgradeProcessor();
        if( processor.hasUpgrade(Upgrades.LEVELING) ) {
            LevelStorage storage = processor.getUpgradeInstance(Upgrades.LEVELING.getId());
            if( storage != null ) {
                this.lvlIndicator.get(LevelIndicator.class).setLevel(storage);
                this.lvlModifiers.get(LevelModifiers.class).setModifierList(gui, this.lvlModifiers.data, storage, turretInst);
                this.btnRetrieveExcess.get(Button.class).setEnabled(storage.getExcessXp() > 0);
            }
        }
    }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.LEVELING);
    }

    @Override
    public boolean onElementAction(IGuiTcuInst<?> gui, IGuiElement element, int action) {
        if( action == ACTION_RETRIEVE_XP ) {
            PacketRegistry.sendToServer(new PacketPlayerTurretAction(gui.getTurretInst(), PacketPlayerTurretAction.RETRIEVE_XP));
            return true;
        }

        return false;
    }
}
