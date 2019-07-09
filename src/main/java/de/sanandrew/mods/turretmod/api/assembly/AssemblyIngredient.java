package de.sanandrew.mods.turretmod.api.assembly;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <p>An ingredient for a recipe used by the turret assembly table.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AssemblyIngredient
        extends Ingredient
{
    private final int count;
    private NonNullList<ItemStack> itemList;
    private ItemStack[] items;
    private IntList itemIds;

    /**
     * <p>Creates a new ingredient defined by a list of items.</p>
     *
     * @param count the amount of items required by this ingredient.
     * @param stacks one or more items that represent this ingredient.
     */
    public AssemblyIngredient(int count, ItemStack... stacks) {
        super(0);
        this.itemList = NonNullList.from(ItemStack.EMPTY, stacks);
        this.count = count;
    }

    /**
     * <p>Creates a new ingredient defined by a list of ore dictionary names.</p>
     * @param count The amount of items required by this ingredient.
     * @param ores One or more ore dictionary names, whose items represent this ingredient.
     */
    public AssemblyIngredient(int count, String... ores) {
        super(0);
        this.itemList = Arrays.stream(ores).map(OreDictionary::getOres).collect(NonNullList::create, NonNullList::addAll, NonNullList::addAll);
        this.count = count;
    }

    /**
     * <p>Returns the amount of items this ingredient should consume upon crafting.</p>
     *
     * @return the amount of items required by this ingredient.
     */

    public int getCount() {
        return this.count;
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if( this.items == null || this.items.length != this.itemList.size() ) {
            NonNullList<ItemStack> lst = NonNullList.create();
            for( ItemStack stack : this.itemList ) {
                if( stack.getMetadata() == OreDictionary.WILDCARD_VALUE ) {
                    stack.getItem().getSubItems(CreativeTabs.SEARCH, lst);
                } else {
                    lst.add(stack);
                }
            }
            this.items = lst.toArray(new ItemStack[0]);
        }

        return this.items;
    }


    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        if( this.itemIds == null || this.itemIds.size() != this.itemList.size() ) {
            this.items = new ItemStack[0];
            this.itemIds = Arrays.stream(this.getMatchingStacks()).map(RecipeItemHelper::pack).collect(Collectors.toCollection(() -> new IntArrayList(this.itemList.size())));
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }


    @Override
    public boolean apply(@Nullable ItemStack input) {
        if( input == null ) {
            return false;
        }

        for( ItemStack target : this.itemList ) {
            if( ItemStackUtils.areEqualNbtFit(input, target, false, true, false) && input.getCount() >= this.count ) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void invalidate() {
        this.itemIds = null;
        this.items = null;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    /**
     * <p>Reads an ingredient from the given JSON element.</p>
     *
     * @param json The JSON element an ingredient should be read from.
     * @param ctx The JSON context.
     * @return the ingredient read from the JSON element.
     * @throws JsonSyntaxException if the JSON element does not contain a valid ingredient.
     */
    public static AssemblyIngredient fromJson(@Nullable JsonElement json, JsonContext ctx) {
        if( json != null && !json.isJsonNull() ) {
            if( json.isJsonObject() ) {
                JsonObject jsonObj = json.getAsJsonObject();
                int count = JsonUtils.getIntVal(jsonObj.get("count"), 1);
                Ingredient defIng = CraftingHelper.getIngredient(jsonObj.get("items"), ctx);

                return new AssemblyIngredient(count, defIng.getMatchingStacks());
            } else {
                throw new JsonSyntaxException("Assembly Table Ingredient needs to be object");
            }
        } else {
            throw new JsonSyntaxException("Assembly Table Ingredient cannot be null");
        }
    }
}
