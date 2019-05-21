/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class ElectrolyteRecipe
        implements IElectrolyteRecipe
{
    private final float efficiency;
    private final int procTime;
    private final NonNullList<Ingredient> ingredient;
    private final ItemStack trash;
    private final ItemStack treasure;
    private final ResourceLocation id;
    private final float trashChance;
    private final float treasureChance;

    public ElectrolyteRecipe(ResourceLocation id, @Nonnull Ingredient ingredient, @Nonnull ItemStack trash, @Nonnull ItemStack treasure, float efficiency, int processTime, float trashChance, float treasureChance) {
        this.id = id;
        this.ingredient = NonNullList.withSize(1, ingredient);
        this.efficiency = efficiency;
        this.procTime = processTime;
        this.trash = trash;
        this.treasure = treasure;
        this.trashChance = trashChance;
        this.treasureChance = treasureChance;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public float getEfficiency() {
        return this.efficiency;
    }

    @Override
    public int getProcessTime() {
        return this.procTime;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            for( ItemStack stack : this.ingredient.get(0).getMatchingStacks() ) {
                if( ItemStackUtils.areEqualNbtFit(stack, inv.getStackInSlot(i), false, true, false) ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return this.trash.copy();
    }

    @Override
    public ItemStack getTreasureResult(IInventory inv) {
        return this.treasure.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.trash;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredient;
    }

    @Override
    public String getGroup() {
        return "electrolytegen";
    }

    @Override
    public float getTrashChance() {
        return this.trashChance;
    }

    @Override
    public float getTreasureChance() {
        return this.treasureChance;
    }
}
