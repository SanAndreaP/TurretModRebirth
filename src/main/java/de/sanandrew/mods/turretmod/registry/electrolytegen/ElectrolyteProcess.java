/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.electrolytegen;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ElectrolyteProcess
{
    public final IElectrolyteRecipe recipe;

    public final ItemStack processStack;
    private ItemStack trashStack = null;
    private ItemStack treasureStack = null;
    private short progress = 0;

    public ElectrolyteProcess(IElectrolyteRecipe recipe, ItemStack stack) {
        this.recipe = MiscUtils.defIfNull(recipe, InvalidRecipe.INSTANCE);
        this.processStack = stack;
    }

    public ElectrolyteProcess(ByteBuf buf) {
        this.processStack = ByteBufUtils.readItemStack(buf);
        this.progress = buf.readShort();
        this.recipe = MiscUtils.defIfNull(ElectrolyteManager.INSTANCE.getFuel(new ResourceLocation(ByteBufUtils.readUTF8String(buf))), InvalidRecipe.INSTANCE);
    }

    public ElectrolyteProcess(NBTTagCompound nbt) {
        this.processStack = new ItemStack(nbt.getCompoundTag("ProgressItem"));
        this.progress = nbt.getShort("Progress");
        this.recipe = MiscUtils.defIfNull(ElectrolyteManager.INSTANCE.getFuel(new ResourceLocation(nbt.getString("Recipe"))), InvalidRecipe.INSTANCE);
    }

    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.processStack);
        buf.writeShort(this.progress);
        ByteBufUtils.writeUTF8String(buf, this.recipe != null ? this.recipe.getId().toString() : "");
    }

    public void writeToNBT(NBTTagCompound nbt) {
        ItemStackUtils.writeStackToTag(this.processStack, nbt, "ProgressItem");
        nbt.setShort("Progress", this.progress);
        nbt.setString("Recipe", this.recipe != null ? this.recipe.getId().toString() : "");
    }

    public int getProgress() {
        return this.progress;
    }

    public ItemStack getTrashStack(IInventory inv) {
        if( this.trashStack == null ) {
            this.trashStack = MiscUtils.RNG.randomFloat() < this.recipe.getTrashChance() ? this.recipe.getCraftingResult(inv) : ItemStack.EMPTY;
        }

        return this.trashStack;
    }

    public ItemStack getTreasureStack(IInventory inv) {
        if( this.treasureStack == null ) {
            this.treasureStack = MiscUtils.RNG.randomFloat() < this.recipe.getTreasureChance() ? this.recipe.getCraftingResult(inv) : ItemStack.EMPTY;
        }

        return this.treasureStack;
    }

    public void incrProgress() {
        this.progress++;
    }

    public boolean hasFinished() {
        return this.progress >= this.recipe.getProcessTime();
    }

    private static final class InvalidRecipe
        implements IElectrolyteRecipe
    {
        public static final InvalidRecipe INSTANCE = new InvalidRecipe();

        @Override
        public ResourceLocation getId() { return null; }

        @Override
        public float getEfficiency() { return 0; }

        @Override
        public int getProcessTime() { return 0; }

        @Override
        public boolean matches(IInventory inv, World worldIn) { return false; }

        @Override
        public ItemStack getCraftingResult(IInventory inv) { return null; }

        @Override
        public ItemStack getTreasureResult(IInventory inv) { return null; }

        @Override
        public ItemStack getRecipeOutput() { return null; }

        @Override
        public NonNullList<Ingredient> getIngredients() { return null; }

        @Override
        public String getGroup() { return null; }

        @Override
        public float getTrashChance() { return 0; }

        @Override
        public float getTreasureChance() { return 0; }
    }
}
