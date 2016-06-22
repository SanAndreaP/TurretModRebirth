/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class TurretInfoEntryInfo
        extends TurretInfoEntry
{
    private int drawHeight;

    public TurretInfoEntryInfo() {
        super(new ItemStack(Blocks.GRASS), Lang.TINFO_ENTRY_INFO_TITLE.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initEntry(GuiTurretInfo gui) {
        super.initEntry(gui);

        gui.entryButtons.add(new GuiTurretInfo.GuiButtonLink(gui.getButtonList().size(), 4, 65, "SanAndreasP", "https://twitter.com/SanAndreasP"));
        gui.entryButtons.add(new GuiTurretInfo.GuiButtonLink(gui.getButtonList().size(), 4, 85, "Darkhax", "https://twitter.com/Darkh4x"));
        gui.entryButtons.add(new GuiTurretInfo.GuiButtonLink(gui.getButtonList().size(), 4, 96, "Drullkus", "https://twitter.com/Drullkus"));
        gui.entryButtons.add(new GuiTurretInfo.GuiButtonLink(gui.getButtonList().size(), 4, 107, "Vazkii", "https://twitter.com/Vazkii"));

        gui.entryButtons.add(new GuiTurretInfo.GuiButtonLink(gui.getButtonList().size(), 2, 125, "Github Homepage", "https://github.com/SanAndreasP/TurretModRebirth"));
        gui.entryButtons.add(new GuiTurretInfo.GuiButtonLink(gui.getButtonList().size(), 2, 136, "Github Issue Tracker", "https://github.com/SanAndreasP/TurretModRebirth/issues"));
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        gui.mc.fontRendererObj.drawString(TextFormatting.ITALIC + Lang.translate(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.mc.fontRendererObj.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_NAME.get()), 2, 16, 0xFF808080, false);
        gui.mc.fontRendererObj.drawString(TurretModRebirth.NAME, 4, 25, 0xFF000000, false);

        gui.mc.fontRendererObj.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_VERSION.get()), 2, 36, 0xFF808080, false);
        gui.mc.fontRendererObj.drawString(TurretModRebirth.VERSION, 4, 45, 0xFF000000, false);

        gui.mc.fontRendererObj.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_AUTHOR.get()), 2, 56, 0xFF808080, false);

        gui.mc.fontRendererObj.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_CREDITS.get()), 2, 76, 0xFF808080, false);

        this.drawHeight = 147;
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
