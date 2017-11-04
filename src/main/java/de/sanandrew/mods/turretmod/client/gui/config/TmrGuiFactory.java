/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Set;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class TmrGuiFactory
        implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft mc) { }

//    @Override
//    public boolean hasConfigGui() {
//        return false;
//    }
//
//    @Override
//    public GuiScreen createConfigGui(GuiScreen parentScreen) {
//        return new GuiConfigScreen(parentScreen);
//    }

    @Override
    @Deprecated
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GuiConfigScreen.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Nullable
    @Override
    @Deprecated
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
