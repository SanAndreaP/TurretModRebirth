package de.sanandrew.mods.turretmod.datagenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.recipes.BetterNBTIngredient;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.ICountedIngredient;
import de.sanandrew.mods.turretmod.recipe.RecipeRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.recipe.AssemblyRecipe;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CompoundIngredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AssemblyBuilder
{
    private final String                          group;
    private final NonNullList<ICountedIngredient> ingredients = NonNullList.create();
    private       int       energyConsumption;
    private       int       processTime;
    private final ItemStack result;
    private ResourceLocation customType;

    private AssemblyBuilder(String group, ItemStack result) {
        this.group = group;
        this.result = result;
    }

    public static AssemblyBuilder newAssembly(String group, ItemStack result) {
        return new AssemblyBuilder(group, result);
    }

    public AssemblyBuilder ingredient(int count, IItemProvider... items) {
        return this.ingredients(Arrays.stream(items).map(i -> new AssemblyRecipe.CountedIngredient(Ingredient.of(i), count))
                                      .toArray(ICountedIngredient[]::new));
    }

    public AssemblyBuilder ingredient(int count, ItemStack... items) {
        return this.ingredients(Arrays.stream(items).map(i -> new AssemblyRecipe.CountedIngredient(Ingredient.of(i), count))
                                      .toArray(ICountedIngredient[]::new));
    }

    @SafeVarargs
    public final AssemblyBuilder ingredient(int count, ITag<Item>... tags) {
        return this.ingredients(Arrays.stream(tags).map(i -> new AssemblyRecipe.CountedIngredient(Ingredient.of(i), count))
                                      .toArray(ICountedIngredient[]::new));
    }

    public AssemblyBuilder ingredients(ICountedIngredient... ingredients) {
        this.ingredients.addAll(Arrays.asList(ingredients));
        return this;
    }

    public AssemblyBuilder energyConsumption(int fluxPerTick) {
        this.energyConsumption = fluxPerTick;
        return this;
    }

    public AssemblyBuilder processTime(int ticksToProcess) {
        this.processTime = ticksToProcess;
        return this;
    }

    public AssemblyBuilder customType(String typeName) {
        return customType(new ResourceLocation(TmrConstants.ID, typeName));
    }

    public AssemblyBuilder customType(String domain, String typeName) {
        return customType(new ResourceLocation(domain, typeName));
    }

    public AssemblyBuilder customType(ResourceLocation typeId) {
        this.customType = typeId;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        ResourceLocation resId = Objects.requireNonNull(this.result.getItem().getRegistryName());
        consumerIn.accept(new Result(new ResourceLocation(TmrConstants.ID, "assembly/" + this.group + "_" + resId.getPath())));
    }

    public static final class CompoundIngredientBuilder
    {
        private final List<Ingredient> ingredients = new ArrayList<>();
        private final int count;

        public CompoundIngredientBuilder(int count) {
            this.count = count;
        }

        public CompoundIngredientBuilder tag(ITag<Item> tag) {
            this.ingredients.add(Ingredient.of(tag));
            return this;
        }

        public CompoundIngredientBuilder item(IItemProvider... items) {
            this.ingredients.add(Ingredient.of(items));
            return this;
        }

        public CompoundIngredientBuilder item(ItemStack... items) {
            this.ingredients.add(Ingredient.of(items));
            return this;
        }

        public CompoundIngredientBuilder itemNbt(ItemStack item) {
            this.ingredients.add(new BetterNBTIngredient(item));
            return this;
        }

        public ICountedIngredient build() {
            return new AssemblyRecipe.CountedIngredient(new AssemblyCompoundIngredient(this.ingredients), this.count);
        }
    }

    private static class AssemblyCompoundIngredient
            extends CompoundIngredient
    {
        protected AssemblyCompoundIngredient(List<Ingredient> children) {
            super(children);
        }
    }

    private class Result
            implements IFinishedRecipe
    {
        private final ResourceLocation id;

        public Result(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("type", (customType == null ? AssemblyManager.TYPE : customType).toString());
            json.addProperty("group", group);
            json.addProperty("energyConsumption", energyConsumption);
            json.addProperty("processTime", processTime);

            JsonArray jarr = new JsonArray();
            ingredients.forEach(i -> jarr.add(i.toJson()));
            json.add("ingredients", jarr);

            JsonObject res = new JsonObject();
            res.addProperty("item", Objects.requireNonNull(result.getItem().getRegistryName()).toString());
            res.addProperty("count", result.getCount());
            json.add("result", res);
        }

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getType() {
            return RecipeRegistry.ASSEMBLY_RECIPE_SER;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
