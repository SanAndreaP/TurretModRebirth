/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class TurretInfoEntry
{
    public static final int MAX_ENTRY_WIDTH = 160;
    private ItemStack icon;
    private String title;

    protected TurretInfoEntry(ItemStack icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public final ItemStack getIcon() {
        return this.icon.copy();
    }

    public final String getTitle() {
        return title;
    }

    public abstract void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, float partTicks);

    public abstract int getPageHeight();

    public static class EntryEmpty
            extends TurretInfoEntry
    {
        protected EntryEmpty(ItemStack icon, String title) {
            super(icon, title);
        }

        @Override
        public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, float partTicks) { }

        @Override
        public int getPageHeight() {
            return 0;
        }
    }
}
