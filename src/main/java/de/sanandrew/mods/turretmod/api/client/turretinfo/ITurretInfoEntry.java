package de.sanandrew.mods.turretmod.api.client.turretinfo;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface ITurretInfoEntry
{
    int MAX_ENTRY_WIDTH = 168;
    int MAX_ENTRY_HEIGHT = 183;

    void initEntry(IGuiTurretInfo gui);

    void drawPage(int mouseX, int mouseY, int scrollY, float partTicks);

    int getPageHeight();

    @Nonnull
    ItemStack getIcon();

    String getTitle();

    default boolean actionPerformed(GuiButton btn) {
        return false;
    }
}
