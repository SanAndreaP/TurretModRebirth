package de.sanandrew.mods.turretmod.api.client.turretinfo;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public interface ITurretInfoEntry
{
    int MAX_ENTRY_WIDTH = 168;
    int MAX_ENTRY_HEIGHT = 183;

    void initEntry(IGuiTurretInfo gui);

    void drawPage(int mouseX, int mouseY, int scrollY, float partTicks);

    int getPageHeight();

    ItemStack getIcon();

    String getTitle();

    default boolean actionPerformed(GuiButton btn) {
        return false;
    }
}
