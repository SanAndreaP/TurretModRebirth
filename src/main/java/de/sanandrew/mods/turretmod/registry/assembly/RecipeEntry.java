/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.turretmod.api.assembly.IRecipeItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RecipeEntry
{
    public final IRecipeItem[] resources;
    public final int fluxPerTick;
    public final int ticksProcessing;
    public final ItemStack result;

    RecipeEntry(IRecipeItem[] resources, int fluxPerTick, int ticksProcessing, ItemStack result) {
        this.resources = resources;
        this.fluxPerTick = fluxPerTick;
        this.ticksProcessing = ticksProcessing;
        this.result = result;
    }

    public RecipeEntry copy() {
        List<IRecipeItem> stacks = new ArrayList<>();
        for( IRecipeItem stack : this.resources ) {
            stacks.add(stack.copy());
        }
        return new RecipeEntry(stacks.toArray(new IRecipeItem[0]), this.fluxPerTick, this.ticksProcessing, this.result.copy());
    }
}
