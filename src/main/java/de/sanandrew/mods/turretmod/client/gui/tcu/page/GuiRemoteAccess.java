/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.util.ResourceLocation;

public class GuiRemoteAccess
        implements IGuiTCU
{
    @Override
    public void initialize(IGuiTcuInst<?> gui, GuiDefinition guiDefinition) { }

    @Override
    public ResourceLocation getGuiDefinition() {
        return Resources.GUI_STRUCT_TCU_REMOTE_ACCESS.resource;
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) { }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.isRemote() && gui.getTurretInst().getUpgradeProcessor().canAccessRemotely();
    }
}
