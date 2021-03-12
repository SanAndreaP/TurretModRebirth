/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.assembly;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyInventory;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import net.minecraft.inventory.IInventory;
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

public class AssemblyRecipe
        implements IAssemblyRecipe
{
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final int fluxPerTick;
    private final int processTime;
    private final ItemStack result;
    private final String group;

    public AssemblyRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
        this.id = id;
        this.group = group;
        this.ingredients = ingredients;
        this.fluxPerTick = fluxPerTick;
        this.processTime = processTime;
        this.result = result;
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
        return AssemblyManager.TYPE;
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
    public boolean matches(@Nonnull IAssemblyInventory inv, @Nonnull World worldIn) {
        NonNullList<ItemStack> cmpInv = getCompactInventory(inv);
        return this.ingredients.stream().allMatch(i -> {
            for( ItemStack invStack : cmpInv ) {
                if( i.test(invStack) ) {
                    return true;
                }
            }

            return false;
        });
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IAssemblyInventory inv) {
        return this.result.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Nonnull
    @Override
    public String getGroup() {
        return this.group;
    }

    private static NonNullList<ItemStack> getCompactInventory(IInventory inv) {
        NonNullList<ItemStack> items = NonNullList.create();

        for( int slot = 0, max = inv.getSizeInventory(); slot < max; slot++ ) {
            items.add(inv.getStackInSlot(slot));
        }

        return ItemStackUtils.getCompactItems(items, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static final class Serializer
            extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<AssemblyRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public AssemblyRecipe read(@Nonnull ResourceLocation recipeId, JsonObject json) {
            String group = JsonUtils.getStringVal(json.get("group"));
            int fluxPerTick = JsonUtils.getIntVal(json.get("fluxPerTick"));
            int processTime = JsonUtils.getIntVal(json.get("processTime"));

            NonNullList<Ingredient> ingredients = NonNullList.create();
            for( JsonElement ing : json.getAsJsonArray("ingredients") ) {
                ingredients.add(Ingredient.deserialize(ing));
            }

            ItemStack result = ShapedRecipe.deserializeItem(json.getAsJsonObject("result"));

            return new AssemblyRecipe(recipeId, group, ingredients, fluxPerTick, processTime, result);
        }

        @Nonnull
        @Override
        public AssemblyRecipe read(@Nonnull ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readString();
            int fluxPerTick = buffer.readVarInt();
            int processTime = buffer.readVarInt();
            int ingSz = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for( int i = 0; i < ingSz; i++ ) {
                ingredients.add(Ingredient.read(buffer));
            }

            ItemStack result = buffer.readItemStack();

            return new AssemblyRecipe(recipeId, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        public void write(PacketBuffer buffer, AssemblyRecipe recipe) {
            buffer.writeString(recipe.group);
            buffer.writeVarInt(recipe.fluxPerTick);
            buffer.writeVarInt(recipe.processTime);
            buffer.writeVarInt(recipe.ingredients.size());

            for( Ingredient i : recipe.ingredients ) {
                i.write(buffer);
            }

            buffer.writeItemStack(recipe.result);
        }
    }
}
