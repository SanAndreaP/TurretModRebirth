package de.sanandrew.mods.turretmod.api.assembly;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * <p>An object defining a turret assembly table recipe.</p>
 * <p>This is a custom declaration (pre 1.14) / extension (1.14) of {@link net.minecraft.item.crafting.IRecipe}</p>
 */
public interface IAssemblyRecipe
//        extends IRecipe //TODO: implement in 1.14
{
    /**
     * @return the ID of this recipe.
     */
    ResourceLocation getId();

    /**
     * @return the amount of Redstone Flux (RF) used per tick (1/20th of a second) during crafting.
     */
    int getFluxPerTick();

    /**
     * @return the duration in ticks (1/20th of a second) the crafting process takes for this recipe.
     */
    int getProcessTime();

    /**
     * <p>Checks wether this recipe can be crafted in the given crafting inventory and world.</p>
     *
     * @param inv The inventory where the crafting should occur.
     * @param worldIn The world of the inventory.
     * @return <tt>true</tt>, if this recipe is craftable; <tt>false</tt> otherwise.
     */
    boolean matches(IInventory inv, World worldIn);

    /**
     * <p>Returns the resulting ItemStack from crafting this recipe.</p>
     * <p>Dynamic recipes can return different results depending on the crafting inventory contents.</p>
     *
     * @param inv The inventory where the crafting occurs.
     * @return the resulting ItemStack of this recipe.
     */
    ItemStack getCraftingResult(IInventory inv);

    /**
     * <p>Checks wether this recipe fits the given width and height.</p>
     * <p>Width and height are measured in slot count: An inventory that has 2 rows with each row having 9 slots means <i>height = 2</i> and <i>width = 9</i>.</p>
     *
     * @param width The width of the crafting inventory.
     * @param height The height of the crafting inventory.
     * @return <tt>true</tt>, if this recipe fits; <tt>false</tt> otherwise.
     */
    default boolean canFit(int width, int height) {
        return true;
    }

    /**
     * <p>Returns the resulting ItemStack from this recipe.</p>
     * <p>Dynamic recipes (recipes with different possible results) return {@link ItemStack#EMPTY}.</p>
     *
     * @return the resulting ItemStack of this recipe or <tt>ItemStack.EMPTY</tt>, if this is a dynamic recipe.
     */
    ItemStack getRecipeOutput();

    /**
     * @return a list of ingredients needed for this recipe.
     */
    NonNullList<Ingredient> getIngredients();

    /**
     * <p>Returns a group name. Recipes with the same group name are grouped together in the crafting inventory GUI.</p>
     *
     * @return the name of a group
     */
    String getGroup();
}
