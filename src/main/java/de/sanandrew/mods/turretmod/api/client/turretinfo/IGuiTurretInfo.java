package de.sanandrew.mods.turretmod.api.client.turretinfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public interface IGuiTurretInfo
{
    List<GuiButton> __getButtons();

    Minecraft __getMc();

    void __drawTexturedRect(int x, int y, int u, int v, int w, int h);

    void renderStack(@Nonnull ItemStack stack, int x, int y, double scale);

    void doEntryScissoring(int x, int y, int width, int height);

    void doEntryScissoring();

    void drawMiniItem(int x, int y, int mouseX, int mouseY, int scrollY, @Nonnull ItemStack stack, boolean drawTooltip);

    int getEntryX();

    int getEntryY();
}
