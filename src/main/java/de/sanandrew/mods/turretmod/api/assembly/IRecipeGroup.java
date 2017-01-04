package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.UUID;

public interface IRecipeGroup
{
    void addRecipeId(UUID id);
    List<UUID> getRecipeIdList();
    String getName();
    ItemStack getIcon();
}
