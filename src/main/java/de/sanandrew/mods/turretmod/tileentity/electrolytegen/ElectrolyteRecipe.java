/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.electrolytegen;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteInventory;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class ElectrolyteRecipe
        implements IElectrolyteRecipe
{
    private final float efficiency;
    private final int procTime;
    private final Ingredient ingredient;
    private final ItemStack trash;
    private final ItemStack treasure;
    private final ResourceLocation id;
    private final float trashChance;
    private final float treasureChance;

    public ElectrolyteRecipe(ResourceLocation id, @Nonnull Ingredient ingredient, @Nonnull ItemStack trash, @Nonnull ItemStack treasure, float efficiency, int processTime, float trashChance, float treasureChance) {
        this.id = id;
        this.ingredient = ingredient;
        this.efficiency = efficiency;
        this.procTime = processTime;
        this.trash = trash;
        this.treasure = treasure;
        this.trashChance = trashChance;
        this.treasureChance = treasureChance;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return ElectrolyteManager.TYPE;
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
    public boolean matches(IElectrolyteInventory inv, @Nonnull World worldIn) {
        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            for( ItemStack stack : this.ingredient.getMatchingStacks() ) {
                if( ItemStackUtils.areEqualNbtFit(stack, inv.getStackInSlot(i), false, true) ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack getTrashResult(IElectrolyteInventory inv) {
        return this.trash.copy();
    }

    @Override
    public ItemStack getTreasureResult(IElectrolyteInventory inv) {
        return this.treasure.copy();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return this.trash;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, this.ingredient);
    }

    @Nonnull
    @Override
    public String getGroup() {
        return "electrolyte_generator";
    }

    @Override
    public float getTrashChance() {
        return this.trashChance;
    }

    @Override
    public float getTreasureChance() {
        return this.treasureChance;
    }

    public static final class Serializer
            extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<ElectrolyteRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ElectrolyteRecipe read(@Nonnull ResourceLocation recipeId, JsonObject json) {
            float efficiency = JsonUtils.getFloatVal(json.get("efficiency"));
            int processTime = JsonUtils.getIntVal(json.get("processTime"));
            Ingredient ingredient = Ingredient.deserialize(json.get("ingredient"));
            ItemStack trashResult = ShapedRecipe.deserializeItem(json.getAsJsonObject("trashResult"));
            ItemStack treasureResult = ShapedRecipe.deserializeItem(json.getAsJsonObject("treasureResult"));
            float trashChance = JsonUtils.getFloatVal(json.get("trashChance"), 0.2F);
            float treasureChance = JsonUtils.getFloatVal(json.get("treasureChance"), 0.02F);

            return new ElectrolyteRecipe(recipeId, ingredient, trashResult, treasureResult, efficiency, processTime, trashChance, treasureChance);
        }

        @Nonnull
        @Override
        public ElectrolyteRecipe read(@Nonnull ResourceLocation recipeId, PacketBuffer buffer) {
            float efficiency = buffer.readFloat();
            int processTime = buffer.readVarInt();
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack trashResult = buffer.readItemStack();
            ItemStack treasureResult = buffer.readItemStack();
            float trashChance = buffer.readFloat();
            float treasureChance = buffer.readFloat();

            return new ElectrolyteRecipe(recipeId, ingredient, trashResult, treasureResult, efficiency, processTime, trashChance, treasureChance);
        }

        @Override
        public void write(PacketBuffer buffer, ElectrolyteRecipe recipe) {
            buffer.writeFloat(recipe.efficiency);
            buffer.writeVarInt(recipe.procTime);
            Ingredient.read(buffer);
            buffer.writeItemStack(recipe.trash);
            buffer.writeItemStack(recipe.treasure);
            buffer.writeFloat(recipe.trashChance);
            buffer.writeFloat(recipe.treasureChance);
        }
    }
}
