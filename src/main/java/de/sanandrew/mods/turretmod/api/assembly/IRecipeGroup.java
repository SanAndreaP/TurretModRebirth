package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface IRecipeGroup
{
    void addRecipeId(ResourceLocation id);

    List<ResourceLocation> getRecipeIdList();

    String getName();

    ItemStack getIcon();

    void finalizeGroup(ITurretAssemblyRegistry registry);
}
