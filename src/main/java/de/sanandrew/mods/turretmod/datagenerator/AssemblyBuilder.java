package de.sanandrew.mods.turretmod.datagenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.RecipeRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyRecipe;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class AssemblyBuilder
{
    private final String                                        group;
    private final NonNullList<AssemblyRecipe.CountedIngredient> ingredients = NonNullList.create();
    private       int                                           energyConsumption;
    private       int                     processTime;
    private final ItemStack               result;

    private AssemblyBuilder(String group, ItemStack result) {
        this.group = group;
        this.result = result;
    }

    public static AssemblyBuilder newAssembly(String group, ItemStack result) {
        return new AssemblyBuilder(group, result);
    }

    public AssemblyBuilder ingredient(int count, IItemProvider... items) {
        return this.ingredients(Arrays.stream(items).map(i -> new AssemblyRecipe.CountedIngredient(Ingredient.of(i), count))
                                      .toArray(AssemblyRecipe.CountedIngredient[]::new));
    }

    @SafeVarargs
    public final AssemblyBuilder ingredient(int count, ITag<Item>... tags) {
        return this.ingredients(Arrays.stream(tags).map(i -> new AssemblyRecipe.CountedIngredient(Ingredient.of(i), count))
                                      .toArray(AssemblyRecipe.CountedIngredient[]::new));
    }

    public AssemblyBuilder ingredients(AssemblyRecipe.CountedIngredient... ingredients) {
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

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        ResourceLocation resId = Objects.requireNonNull(this.result.getItem().getRegistryName());
        consumerIn.accept(new Result(new ResourceLocation(TmrConstants.ID, "assembly/" + resId.getPath())));
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
            json.addProperty("type", AssemblyManager.TYPE.toString());
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
