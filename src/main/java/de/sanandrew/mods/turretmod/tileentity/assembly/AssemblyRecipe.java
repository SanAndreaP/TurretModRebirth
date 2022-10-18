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
import de.sanandrew.mods.turretmod.api.assembly.ICountedIngredient;
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
import java.util.Arrays;
import java.util.stream.Collectors;

public class AssemblyRecipe
        implements IAssemblyRecipe
{
    private final ResourceLocation id;
    private final String                          group;
    private final NonNullList<ICountedIngredient> ingredients;
    private final int                             energyConsumption;
    private final int                     processTime;
    private final ItemStack result;

    public AssemblyRecipe(ResourceLocation id, String group, NonNullList<ICountedIngredient> ingredients, int energyConsumption, int processTime, ItemStack result) {
        this.id = id;
        this.group = group; //"turret_assembly." +
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
                if( i.getIngredient().test(invStack) ) {
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
        return this.ingredients.stream().map(ICountedIngredient::getIngredient).collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    @Nonnull
    public NonNullList<ICountedIngredient> getCountedIngredients() {
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

            NonNullList<ICountedIngredient> ingredients = NonNullList.create();
            for( JsonElement ing : json.getAsJsonArray("ingredients") ) {
                ingredients.add(CountedIngredient.fromJson(ing));
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
            int                             ingSz       = buffer.readVarInt();
            NonNullList<ICountedIngredient> ingredients = NonNullList.create();

            for( int i = 0; i < ingSz; i++ ) {
                ingredients.add(CountedIngredient.fromNetwork(buffer));
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

            for( ICountedIngredient i : recipe.ingredients ) {
                i.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
        }
    }

    public static class CountedIngredient
            implements ICountedIngredient
    {
        private static final String JSON_COUNT = "count";
        private static final String JSON_INGREDIENT = "ingredient";

        private final Ingredient ingredient;
        private final int count;

        public CountedIngredient(Ingredient ingredient, int count) {
            this.ingredient = ingredient;
            this.count = count;
        }

        @Override
        public Ingredient getIngredient() {
            return this.ingredient;
        }

        @Override
        public ItemStack[] getItems() {
            ItemStack[] stacks = this.ingredient.getItems();

            Arrays.stream(stacks).forEach(i -> i.setCount(this.count));

            return stacks;
        }

        public static ICountedIngredient fromJson(JsonElement json) {
            if( json.isJsonObject() ) {
                JsonObject jObj = json.getAsJsonObject();
                if( jObj.has(JSON_COUNT) && jObj.has(JSON_INGREDIENT) ) {
                    return new CountedIngredient(Ingredient.fromJson(jObj.get(JSON_INGREDIENT)), JsonUtils.getIntVal(jObj.get(JSON_COUNT)));
                }
            }

            return new CountedIngredient(Ingredient.fromJson(json), 1);
        }

        @Override
        public JsonElement toJson() {
            if( this.count == 1 ) {
                return this.ingredient.toJson();
            }

            JsonObject jObj = new JsonObject();
            jObj.addProperty(JSON_COUNT, this.count);
            jObj.add(JSON_INGREDIENT, this.ingredient.toJson());

            return jObj;
        }

        @Override
        public void toNetwork(PacketBuffer buffer) {
            buffer.writeVarInt(this.count);
            this.ingredient.toNetwork(buffer);
        }

        public static ICountedIngredient fromNetwork(PacketBuffer buffer) {
            int count = buffer.readVarInt();
            return new CountedIngredient(Ingredient.fromNetwork(buffer), count);
        }
    }
}
