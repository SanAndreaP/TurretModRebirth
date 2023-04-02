/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.datagenerator;

import com.google.gson.JsonObject;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.recipe.RecipeRegistry;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteManager;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ElectrolyteBuilder
{
    private final Ingredient ingredient;
    private       float      efficiency;
    private       int        processTime;
    private       Item       trashResult;
    private       float      trashChance;
    private       Item       treasureResult;
    private       float      treasureChance;

    private ElectrolyteBuilder(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public static ElectrolyteBuilder newElectrolyte(IItemProvider item) {
        return new ElectrolyteBuilder(Ingredient.of(item));
    }

    public static ElectrolyteBuilder newElectrolyte(ITag<Item> tag) {
        return new ElectrolyteBuilder(Ingredient.of(tag));
    }

    public ElectrolyteBuilder efficiency(float percent) {
        this.efficiency = percent;

        return this;
    }

    public ElectrolyteBuilder processTime(int ticks) {
        this.processTime = ticks;

        return this;
    }

    public ElectrolyteBuilder trash(IItemProvider result) {
        return this.trash(result, 0.2F);
    }

    public ElectrolyteBuilder trash(IItemProvider result, float chance) {
        this.trashResult = result.asItem();
        this.trashChance = chance;

        return this;
    }

    public ElectrolyteBuilder treasure(IItemProvider result) {
        return this.treasure(result, 0.02F);
    }

    public ElectrolyteBuilder treasure(IItemProvider result, float chance) {
        this.treasureResult = result.asItem();
        this.treasureChance = chance;

        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        ResourceLocation ingId = Objects.requireNonNull(this.ingredient.getItems()[0].getItem().getRegistryName());
        consumerIn.accept(new Result(new ResourceLocation(TmrConstants.ID, "electrolytes/" + ingId.getPath())));
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
            json.addProperty("type", ElectrolyteManager.TYPE.toString());
            json.add("ingredient", ingredient.toJson());
            json.addProperty("efficiency", efficiency);
            json.addProperty("processTime", processTime);
            if( trashResult != null && trashChance >= 0.00 ) {
                json.addProperty("trashChance", trashChance);
                json.add("trashResult", getItem(trashResult));
            }
            if( treasureResult != null && treasureChance >= 0.00 ) {
                json.addProperty("treasureChance", treasureChance);
                json.add("treasureResult", getItem(treasureResult));
            }
        }

        private JsonObject getItem(Item item) {
            JsonObject jobj = new JsonObject();
            jobj.addProperty("item", Objects.requireNonNull(item.getRegistryName()).toString());

            return jobj;
        }

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getType() {
            return RecipeRegistry.ELECTROLYTE_RECIPE_SER;
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
