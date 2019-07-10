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

/**
 * An object used to manage recipes for the turret assembly table.
 *
 * @see de.sanandrew.mods.turretmod.api.ITmrPlugin#registerAssemblyRecipes(IAssemblyManager) ITmrPlugin.registerAssemblyRecipes(IAssemblyManager)
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IAssemblyManager
{
    /**
     * <p>Registers a new recipe.</p>
     *
     * @param recipe The recipe to be registered.
     * @return <tt>true</tt>, if the recipe was registered successfully; <tt>false</tt> otherwise.
     */
    boolean registerRecipe(@Nonnull IAssemblyRecipe recipe);

    /**
     * <p>Returns the ItemStack representing the icon of the given group name or {@link ItemStack#EMPTY}, if no icon was set for that group.</p>
     *
     * @param group The name of the group.
     * @return the icon of the group.
     */
    ItemStack getGroupIcon(String group);

    /**
     * <p>Returns an array of group names used by all registered recipes.</p>
     *
     * @return an array with group names.
     */
    String[] getGroups();

    /**
     * <p>Returns a list of all registered recipes within the given group.</p>
     *
     * @param groupName The name of the group that should be filtered.
     * @return a list of filtered, registered recipes.
     */
    List<IAssemblyRecipe> getRecipes(String groupName);

    /**
     * <p>Returns a recipe matching the given ID.</p>
     *
     * @param id The ID of the recipe requested.
     * @return the recipe with the given ID or <tt>null</tt>, if no recipe was found.
     */
    IAssemblyRecipe getRecipe(ResourceLocation id);

    /**
     * <p>Returns a list of all registered recipes.</p>
     *
     * @return a list of registered recipes.
     */
    List<IAssemblyRecipe> getRecipes();

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
    void removeRecipe(ResourceLocation id);
}
