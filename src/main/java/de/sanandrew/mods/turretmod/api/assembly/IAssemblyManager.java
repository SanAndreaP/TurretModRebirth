/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IAssemblyManager
{
    boolean registerRecipe(@Nonnull IAssemblyRecipe recipe);

    ItemStack getGroupIcon(String group);

    String[] getGroups();

    List<IAssemblyRecipe> getRecipes(String groupName);

    IAssemblyRecipe getRecipe(ResourceLocation id);

    List<IAssemblyRecipe> getRecipes();

    void setGroupIcon(String group, ItemStack icon);

    void removeRecipe(ResourceLocation id);
}
