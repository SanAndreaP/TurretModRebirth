package de.sanandrew.mods.turretmod.api.client.tcu;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ILabelRegistry
{
    float MIN_WIDTH = 128.0F;

    void registerLabelElement(ILabelElement element);
}
