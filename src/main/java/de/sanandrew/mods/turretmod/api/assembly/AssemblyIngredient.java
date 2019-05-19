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

public class AssemblyIngredient
        extends Ingredient
{
    private final int count;
    private NonNullList<ItemStack> itemList;
    private ItemStack[] items;
    private IntList itemIds;

    public AssemblyIngredient(int count, ItemStack... stacks) {
        super(0);
        this.itemList = NonNullList.from(ItemStack.EMPTY, stacks);
        this.count = count;
    }

    public AssemblyIngredient(int count, String... ores)
    {
        super(0);
        this.itemList = Arrays.stream(ores)
                .map(OreDictionary::getOres)
                .collect(NonNullList::create, NonNullList::addAll, NonNullList::addAll);
        this.count = count;
    }

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
