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
import de.sanandrew.mods.turretmod.util.TmrConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
class GuiConfigScreen
        extends GuiConfig
{
    GuiConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getCfgElements(), TmrConstants.ID, "configMain", false, false, "Turret Mod Configuration");
    }

    @SuppressWarnings("unchecked")
    private static List<IConfigElement> getCfgElements() {
        return TmrConfig.getCategoriesForGUI().entrySet().stream().map(e -> new ConfigFileElement(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    private static final class ConfigFileElement
            implements IConfigElement
    {
        final IConfigElement[] children;
        final String name;

        ConfigFileElement(String name, ConfigCategory[] children) {
            this.children = Arrays.stream(children).map(ConfigElement::new).toArray(IConfigElement[]::new);
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getQualifiedName() {
            return this.name;
        }

        @Override
        public String getLanguageKey() {
            return this.name;
        }

        @Override
        public List<IConfigElement> getChildElements() {
            return Arrays.asList(this.children);
        }

        @Override
        public ConfigGuiType getType() {
            return ConfigGuiType.CONFIG_CATEGORY;
        }

        @Override
        public boolean isDefault() {
            return true;
        }

        @Override
        public boolean showInGui() {
            return true;
        }

        @Override public boolean isProperty() { return false; }
        @Override public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass() { return null; }
        @Override public Class<? extends GuiEditArrayEntries.IArrayEntry> getArrayEntryClass() { return null; }
        @Override public String getComment() { return null; }
        @Override public boolean isList() { return false; }
        @Override public boolean isListLengthFixed() { return false; }
        @Override public int getMaxListLength() { return -1; }
        @Override public Object getDefault() { return null; }
        @Override public Object[] getDefaults() { return null; }
        @Override public void setToDefault() { }
        @Override public boolean requiresWorldRestart() { return false; }
        @Override public boolean requiresMcRestart() { return false; }
        @Override public Object get() { return null; }
        @Override public Object[] getList() { return null; }
        @Override public void set(Object value) { }
        @Override public void set(Object[] aVal) { }
        @Override public String[] getValidValues() { return null; }
        @Override public Object getMinValue() { return null; }
        @Override public Object getMaxValue() { return null; }
        @Override public Pattern getValidationPattern() { return null; }
    }
}
