/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TurretInfoEntryInfo
        implements ITurretInfoEntry
{
    private int drawHeight;

    private IGuiTurretInfo guiInfo;
    private final ItemStack icon;

    public TurretInfoEntryInfo() {
        this.icon = new ItemStack(Blocks.GRASS);
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public String getTitle() {
        return Lang.TINFO_ENTRY_INFO_TITLE.get();
    }

    @Override
    public void initEntry(IGuiTurretInfo gui) {
        this.guiInfo = gui;
        List<GuiButton> btns = gui.__getButtons();
//TODO: fix buttons
        btns.add(new GuiTurretInfo.GuiButtonLink(btns.size(), 4, 65, "SanAndreasP", "https://twitter.com/SanAndreasP"));
        btns.add(new GuiTurretInfo.GuiButtonLink(btns.size(), 4, 85, "Darkhax", "https://twitter.com/Darkh4x"));
        btns.add(new GuiTurretInfo.GuiButtonLink(btns.size(), 4, 96, "Drullkus", "https://twitter.com/Drullkus"));
        btns.add(new GuiTurretInfo.GuiButtonLink(btns.size(), 4, 107, "Vazkii", "https://twitter.com/Vazkii"));

        btns.add(new GuiTurretInfo.GuiButtonLink(btns.size(), 2, 125, "Github Homepage", "https://github.com/SanAndreasP/TurretModRebirth"));
        btns.add(new GuiTurretInfo.GuiButtonLink(btns.size(), 2, 136, "Github Issue Tracker", "https://github.com/SanAndreasP/TurretModRebirth/issues"));
    }

    @Override
    public void drawPage(int mouseX, int mouseY, int scrollY, float partTicks) {
        Minecraft mc = this.guiInfo.__getMc();
        
        mc.fontRenderer.drawString(TextFormatting.ITALIC + Lang.translate(this.getTitle()), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_NAME.get()), 2, 16, 0xFF808080, false);
        mc.fontRenderer.drawString(TmrConstants.NAME, 4, 25, 0xFF000000, false);

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_VERSION.get()), 2, 36, 0xFF808080, false);
        mc.fontRenderer.drawString(TmrConstants.VERSION, 4, 45, 0xFF000000, false);

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_AUTHOR.get()), 2, 56, 0xFF808080, false);

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_INFO_CREDITS.get()), 2, 76, 0xFF808080, false);

        this.drawHeight = 147;
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
