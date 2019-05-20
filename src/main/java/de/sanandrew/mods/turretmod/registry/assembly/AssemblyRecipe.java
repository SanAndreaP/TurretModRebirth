/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AssemblyRecipe
        implements IAssemblyRecipe
{
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final int fluxPerTick;
    private final int processTime;
    private final ItemStack result;
    private final String group;

    AssemblyRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
        this.id = id;
        this.group = group;
        this.ingredients = ingredients;
        this.fluxPerTick = fluxPerTick;
        this.processTime = processTime;
        this.result = result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public int getFluxPerTick() {
        return this.fluxPerTick;
    }

    @Override
    public int getProcessTime() {
        return this.processTime;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        NonNullList<ItemStack> cmpInv = getCompactInventory(inv);
        return this.ingredients.stream().allMatch(i -> {
            for( ItemStack invStack : cmpInv ) {
                if( i.apply(invStack) ) {
                    return true;
                }
            }

            return false;
        });
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return this.result.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    private static NonNullList<ItemStack> getCompactInventory(IInventory inv) {
        NonNullList<ItemStack> items = NonNullList.create();
        NonNullList<ItemStack> cmpItems = NonNullList.create();

        for( int slot = 0, max = inv.getSizeInventory(); slot < max; slot++ ) {
            items.add(inv.getStackInSlot(slot));
        }

        items.sort((i1, i2) -> ItemStackUtils.areEqual(i1, i2, false, true, true) ? 0 : i1.getUnlocalizedName().compareTo(i2.getUnlocalizedName()));
        items.forEach(v -> {
            int cmpSize = cmpItems.size();
            if( cmpSize < 1 ) {
                cmpItems.add(v.copy());
            } else {
                ItemStack cs = cmpItems.get(cmpSize - 1);
                if( ItemStackUtils.areEqual(cs, v, false, true, true) ) {
                    int rest = Math.min(cs.getMaxStackSize(), inv.getInventoryStackLimit()) - cs.getCount();
                    if( rest >= v.getCount() ) {
                        cs.grow(v.getCount());
                    } else {
                        cs.grow(rest);
                        ItemStack restStack = v.copy();
                        restStack.shrink(rest);
                        cmpItems.add(restStack);
                    }
                } else {
                    cmpItems.add(v.copy());
                }
            }
        });

        return cmpItems;
    }
}
