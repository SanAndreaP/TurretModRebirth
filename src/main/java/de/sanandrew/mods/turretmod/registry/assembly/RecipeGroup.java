/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.turretmod.api.assembly.IRecipeGroup;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeGroup
        implements IRecipeGroup
{
    private final String name;
    @Nonnull
    private final ItemStack icon;
    private final List<ResourceLocation> recipes = new ArrayList<>();

    public RecipeGroup(String name, @Nonnull ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }

    public void addRecipeId(ResourceLocation id) {
        this.recipes.add(id);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nonnull
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public List<ResourceLocation> getRecipeIdList() {
        return new ArrayList<>(this.recipes);
    }

    @Override
    public void finalizeGroup(ITurretAssemblyRegistry registry) {
        this.recipes.sort((o1, o2) -> {
            ItemStack is1 = registry.getRecipeResult(o1);
            ItemStack is2 = registry.getRecipeResult(o2);
            int i = Integer.compare(Item.getIdFromItem(is1.getItem()), Item.getIdFromItem(is2.getItem()));
            if( i == 0 ) {
                NonNullList<ItemStack> subtypes = NonNullList.create();
                is1.getItem().getSubItems(CreativeTabs.SEARCH, subtypes);
                return Integer.compare(TurretAssemblyRegistry.getStackIndexInList(subtypes, is1), TurretAssemblyRegistry.getStackIndexInList(subtypes, is2));
            }
            return i;
        });
    }
}
