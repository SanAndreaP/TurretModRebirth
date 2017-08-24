package de.sanandrew.mods.turretmod.api.client.turretinfo;

import net.minecraft.util.ResourceLocation;

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
