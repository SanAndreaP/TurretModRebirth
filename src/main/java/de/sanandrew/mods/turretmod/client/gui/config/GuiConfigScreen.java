/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.config;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiConfigScreen
        extends GuiConfig
{
    public GuiConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getCfgElements(), TmrConstants.ID, "configMain", false, false, "Turret Mod Configuration");
    }

    @SuppressWarnings("unchecked")
    private static List<IConfigElement> getCfgElements() {
        List<IConfigElement> configElements = new ArrayList<>();
        configElements.add(new ConfigElement(TmrConfiguration.getCategory(Configuration.CATEGORY_CLIENT)));
        configElements.add(new ConfigElement(TmrConfiguration.getCategory(Configuration.CATEGORY_GENERAL)));
        configElements.add(new ConfigElement(TmrConfiguration.getCategory(TmrConfiguration.CAT_SERVER)));
        return configElements;
    }
}
