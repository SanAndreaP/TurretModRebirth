package de.sanandrew.mods.turretmod.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.util.ResourceLocation;

public interface IListedItemMeshDefinition
        extends ItemMeshDefinition
{
    ResourceLocation[] getDefinedResources();
}
