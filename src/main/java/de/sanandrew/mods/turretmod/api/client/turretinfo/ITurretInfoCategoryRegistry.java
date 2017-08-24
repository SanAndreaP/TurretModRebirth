package de.sanandrew.mods.turretmod.api.client.turretinfo;

import de.sanandrew.mods.turretmod.client.gui.tinfo.TurretInfoCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITurretInfoCategoryRegistry
{
    ITurretInfoCategory registerCategory(ResourceLocation categoryIcon, String title);

    ITurretInfoCategory[] getCategories();

    ITurretInfoCategory getCategory(int index);

    int getCategoryCount();
}
