/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteInventory;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
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

    public ElectrolyteProcess(ResourceLocation recipe, ItemStack stack) {
        this.recipe = recipe;
        this.processStack = stack;
    }

    public ElectrolyteProcess(CompoundNBT nbt) {
        this.processStack = ItemStack.of(nbt.getCompound("ProgressItem"));
        this.progress = nbt.getShort("Progress");
        this.recipe = MiscUtils.defIfNull(new ResourceLocation(nbt.getString("Recipe")), EmptyRecipe.INSTANCE.getId());
    }

    ElectrolyteProcess(ItemStack stack) {
        this(null, stack);
    }

    public void write(CompoundNBT nbt) {
        ItemStackUtils.writeStackToTag(this.processStack, nbt, "ProgressItem");
        nbt.putInt("Progress", this.progress);
        nbt.putString("Recipe", this.recipe != null ? this.recipe.toString() : "");
    }

    public ItemStack getTrashStack(IElectrolyteInventory inv) {
        IElectrolyteRecipe recipe = grabRecipe(inv.getWorld());

        if( this.trashStack == null ) {
            this.trashStack = MiscUtils.RNG.randomFloat() < recipe.getTrashChance() ? recipe.getTrashResult(inv) : ItemStack.EMPTY;
        }

        return this.trashStack;
    }

    public ItemStack getTreasureStack(IElectrolyteInventory inv) {
        IElectrolyteRecipe recipe = grabRecipe(inv.getWorld());

        if( this.treasureStack == null ) {
            this.treasureStack = MiscUtils.RNG.randomFloat() < recipe.getTreasureChance() ? recipe.getTreasureResult(inv) : ItemStack.EMPTY;
        }

        return this.treasureStack;
    }

    public IElectrolyteRecipe grabRecipe(World world) {
        return MiscUtils.defIfNull(ElectrolyteManager.INSTANCE.getFuel(world, this.recipe), EmptyRecipe.INSTANCE);
    }

    public void incrProgress() {
        this.progress++;
    }

    public boolean hasFinished(IElectrolyteInventory inv) {
        return this.progress >= getMaxProgress(inv);
    }

    public int getMaxProgress(IElectrolyteInventory inv) {
        return grabRecipe(inv.getWorld()).getProcessTime();
    }

    public float getEfficiency(IElectrolyteInventory inv) {
        return grabRecipe(inv.getWorld()).getEfficiency();
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
        public void incrProgress() { }

        @Override
        public boolean hasFinished(IElectrolyteInventory inv) {
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
            return ElectrolyteRecipe.Serializer.INSTANCE;
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
        public boolean matches(@Nonnull IElectrolyteInventory inv, @Nonnull World worldIn) { return false; }

        @Override
        public ItemStack getTrashResult(IElectrolyteInventory inv) { return ItemStack.EMPTY; }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return false;
        }

        @Override
        public ItemStack getTreasureResult(IElectrolyteInventory inv) { return ItemStack.EMPTY; }

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
