/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public interface IAssemblyManager
{
    /**
     * <p>Registers a new recipe.</p>
     *
     * @param recipe The recipe to be registered.
     * @return <tt>true</tt>, if the recipe was registered successfully; <tt>false</tt> otherwise.
     */
//    boolean registerRecipe(@Nonnull IAssemblyRecipe recipe);

    /**
     * <p>Returns the ItemStack representing the icon of the given group name or {@link ItemStack#EMPTY}, if no icon was set for that group.</p>
     *
     * @param group The name of the group.
     * @return the icon of the group.
     */
    ItemStack getGroupIcon(String group);

    void setGroupOrder(String group, int sort);

    /**
     * <p>Returns a list of all registered recipes.</p>
     *
     * @return a list of registered recipes.
     */
    List<IAssemblyRecipe> getRecipes(World level);

    /**
     * <p>Returns an array of group names used by all registered recipes.</p>
     *
     * @return an array with group names.
     */
    String[] getGroups(World level);

    /**
     * <p>Returns a list of all registered recipes within the given group.</p>
     *
     * @param level
     * @param groupName The name of the group that should be filtered.
     *
     * @return a list of filtered, registered recipes.
     */
    List<IAssemblyRecipe> getRecipes(World level, String groupName);

    /**
     * <p>Returns a recipe matching the given ID.</p>
     *
     * @param level
     * @param id    The ID of the recipe requested.
     *
     * @return the recipe with the given ID or <tt>null</tt>, if no recipe was found.
     */
    IAssemblyRecipe getRecipe(World level, ResourceLocation id);

//    List<IAssemblyRecipe> getRecipes();

    /**
     * <p>Sets the ItemStack as icon for the given group name.</p>
     *
     * @param group The name of the group.
     * @param icon The new icon for the given group.
     */
    void setGroupIcon(String group, ItemStack icon);

    /**
     * <p>Removes the recipe with the given ID from the turret assembly table.</p>
     *
     * @param id The ID of the recipe to be removed.
     */
//    void removeRecipe(ResourceLocation id);
}
