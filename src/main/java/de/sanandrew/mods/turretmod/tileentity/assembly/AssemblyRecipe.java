/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.tileentity.assembly;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.ILeveledInventory;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.init.RecipeRegistry;
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
    private final String group;
    private final NonNullList<Ingredient> ingredients;
    private final int                     energyConsumption;
    private final int                     processTime;
    private final ItemStack result;

    public AssemblyRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int energyConsumption, int processTime, ItemStack result) {
        this.id = id;
        this.group = "turret_assembly." + group;
        this.ingredients = ingredients;
        this.energyConsumption = energyConsumption;
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
        return RecipeRegistry.ASSEMBLY_RECIPE_SER;
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return AssemblyManager.TYPE;
    }

    @Override
    public int getEnergyConsumption() {
        return this.energyConsumption;
    }

    @Override
    public int getProcessTime() {
        return this.processTime;
    }

    @Override
    public boolean matches(@Nonnull ILeveledInventory inv, @Nonnull World worldIn) {
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
    public ItemStack assemble(@Nonnull ILeveledInventory inv) {
        return this.result.copy();
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
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

        for( int slot = 0, max = inv.getContainerSize(); slot < max; slot++ ) {
            items.add(inv.getItem(slot));
        }

        return ItemStackUtils.getCompactItems(items, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static final class Serializer
            extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<AssemblyRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Nonnull
        @Override
        public AssemblyRecipe fromJson(@Nonnull ResourceLocation recipeId, JsonObject json) {
            String group = JsonUtils.getStringVal(json.get("group"));
            int fluxPerTick = JsonUtils.getIntVal(json.get("energyConsumption"));
            int processTime = JsonUtils.getIntVal(json.get("processTime"));

            NonNullList<Ingredient> ingredients = NonNullList.create();
            for( JsonElement ing : json.getAsJsonArray("ingredients") ) {
                ingredients.add(Ingredient.fromJson(ing));
            }

            ItemStack result = ShapedRecipe.itemFromJson(json.getAsJsonObject("result"));

            return new AssemblyRecipe(recipeId, group, ingredients, fluxPerTick, processTime, result);
        }

        @Nonnull
        @Override
        public AssemblyRecipe fromNetwork(@Nonnull ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readUtf(512);
            int fluxPerTick = buffer.readVarInt();
            int processTime = buffer.readVarInt();
            int ingSz = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for( int i = 0; i < ingSz; i++ ) {
                ingredients.add(Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();

            return new AssemblyRecipe(recipeId, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, AssemblyRecipe recipe) {
            buffer.writeUtf(recipe.group, 2048);
            buffer.writeVarInt(recipe.energyConsumption);
            buffer.writeVarInt(recipe.processTime);
            buffer.writeVarInt(recipe.ingredients.size());

            for( Ingredient i : recipe.ingredients ) {
                i.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
        }
    }
}
