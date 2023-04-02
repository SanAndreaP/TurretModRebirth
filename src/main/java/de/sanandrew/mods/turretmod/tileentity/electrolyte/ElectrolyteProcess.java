/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.tileentity.electrolyte;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ILeveledInventory;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import de.sanandrew.mods.turretmod.recipe.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ElectrolyteProcess
{
    public static final ElectrolyteProcess EMPTY = new EmptyProcess();

    public final ResourceLocation recipe;

    public final ItemStack processStack;
    private ItemStack trashStack = null;
    private ItemStack treasureStack = null;
    protected int progress = 0;

    private IElectrolyteRecipe recipeInst;

    public ElectrolyteProcess(ResourceLocation recipe, ItemStack stack) {
        this.recipe = recipe;
        this.processStack = stack;
    }

    public ElectrolyteProcess(CompoundNBT nbt) {
        this.processStack = ItemStack.of(nbt.getCompound("ProgressItem"));
        this.progress = nbt.getShort("Progress");
        this.recipe = MiscUtils.get(new ResourceLocation(nbt.getString("Recipe")), EmptyRecipe.INSTANCE.getId());
    }

    ElectrolyteProcess(ItemStack stack) {
        this(null, stack);
    }

    public void write(CompoundNBT nbt) {
        ItemStackUtils.writeStackToTag(this.processStack, nbt, "ProgressItem");
        nbt.putInt("Progress", this.progress);
        nbt.putString("Recipe", this.recipe != null ? this.recipe.toString() : "");
    }

    public ItemStack getTrashStack(ILeveledInventory inv) {
        IElectrolyteRecipe r = this.grabRecipe(inv);

        if( this.trashStack == null ) {
            this.trashStack = MiscUtils.RNG.randomFloat() < r.getTrashChance() ? r.getTrashResult(inv) : ItemStack.EMPTY;
        }

        return this.trashStack;
    }

    public ItemStack getTreasureStack(ILeveledInventory inv) {
        IElectrolyteRecipe r = this.grabRecipe(inv);

        if( this.treasureStack == null ) {
            this.treasureStack = MiscUtils.RNG.randomFloat() < r.getTreasureChance() ? r.getTreasureResult(inv) : ItemStack.EMPTY;
        }

        return this.treasureStack;
    }

    private IElectrolyteRecipe grabRecipe(ILeveledInventory inv) {
        if( this.recipeInst == null ) {
            this.recipeInst = MiscUtils.get(ElectrolyteManager.INSTANCE.getFuel(inv.getLevel(), this.recipe), EmptyRecipe.INSTANCE);
        }

        return this.recipeInst;
    }

    public void incrProgress() {
        this.progress++;
    }

    public boolean hasFinished(ILeveledInventory inv) {
        return this.progress >= getMaxProgress(inv);
    }

    public int getMaxProgress(ILeveledInventory inv) {
        return this.grabRecipe(inv).getProcessTime();
    }

    public float getEfficiency(ILeveledInventory inv) {
        return this.grabRecipe(inv).getEfficiency();
    }

    public boolean isValid() {
        return this.recipe != EmptyRecipe.INSTANCE.getId();
    }

    private static final class EmptyProcess
            extends ElectrolyteProcess
    {
        EmptyProcess() {
            super(null, ItemStack.EMPTY);
            this.progress = -1;
        }

        @Override
        public void incrProgress() { /* no-op */ }

        @Override
        public boolean hasFinished(ILeveledInventory inv) {
            return true;
        }

        @Override
        public boolean isValid() {
            return false;
        }
    }

    private static final class EmptyRecipe
            implements IElectrolyteRecipe
    {
        public static final  EmptyRecipe      INSTANCE = new EmptyRecipe();
        private static final ResourceLocation ID       = new ResourceLocation(TmrConstants.ID, "empty");

        @Nonnull
        @Override
        public ResourceLocation getId() { return ID; }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return RecipeRegistry.ELECTROLYTE_RECIPE_SER;
        }

        @Nonnull
        @Override
        public IRecipeType<?> getType() {
            return ElectrolyteManager.TYPE;
        }

        @Override
        public float getEfficiency() { return 0; }

        @Override
        public int getProcessTime() { return 0; }

        @Override
        public boolean matches(@Nonnull ILeveledInventory inv, @Nonnull World worldIn) { return false; }

        @Override
        public ItemStack getTrashResult(ILeveledInventory inv) { return ItemStack.EMPTY; }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return false;
        }

        @Override
        public ItemStack getTreasureResult(ILeveledInventory inv) { return ItemStack.EMPTY; }

        @Nonnull
        @Override
        public ItemStack getResultItem() { return ItemStack.EMPTY; }

        @Nonnull
        @Override
        public NonNullList<Ingredient> getIngredients() { return NonNullList.create(); }

        @Override
        public float getTrashChance() { return 0; }

        @Override
        public float getTreasureChance() { return 0; }
    }
}
