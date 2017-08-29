package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface GuiTurretCtrlUnit
{
    int getGuiLeft();
    int getGuiTop();
    List getButtonList();
    ITurretInst getTurretInst();
    FontRenderer getFontRenderer();
    Minecraft getMc();
    default boolean hasPermision() { return true; };
}
