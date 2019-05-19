package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IAssemblyRecipe
//        extends IRecipe TODO: implement on 1.14
{
    ResourceLocation getId();

    int getFluxPerTick();

    int getProcessTime();

    boolean matches(IInventory inv, World worldIn);

    ItemStack getCraftingResult(IInventory inv);

    default boolean canFit(int width, int height) {
        return true;
    }

    ItemStack getRecipeOutput();

    NonNullList<Ingredient> getIngredients();

    String getGroup();
}
