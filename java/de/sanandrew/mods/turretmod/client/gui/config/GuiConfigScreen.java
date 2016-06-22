/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.config;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiConfigScreen
        extends GuiConfig
{
    public GuiConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getCfgElements(), TurretModRebirth.ID, "configMain", false, false, "Turret Mod Configuration");
    }

    @SuppressWarnings("unchecked")
    private static List<IConfigElement> getCfgElements() {
        List<IConfigElement> configElements = new ArrayList<>();
        configElements.addAll(new ConfigElement(TmrConfiguration.getCategory(TmrConfiguration.CAT_CLIENT)).getChildElements());
        return configElements;
    }
}