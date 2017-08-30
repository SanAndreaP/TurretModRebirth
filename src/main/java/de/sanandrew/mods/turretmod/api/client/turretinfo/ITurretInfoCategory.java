package de.sanandrew.mods.turretmod.api.client.turretinfo;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public interface ITurretInfoCategory
{
    ITurretInfoCategory addEntry(ITurretInfoEntry... entry);

    ResourceLocation getIcon();

    String getTitle();

    ITurretInfoEntry[] getEntries();

    ITurretInfoEntry getEntry(int index);

    int getEntryCount();

    int getIndex();
}
