package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.registry.assembly.RecipeItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IRecipeItem
{
    RecipeItem put(Item... items);

    RecipeItem put(Block... blocks);

    RecipeItem put(@Nonnull ItemStack... stacks);

    RecipeItem put(String... oreDictNames);

    RecipeItem drawTooltip();

    boolean shouldDrawTooltip();

    RecipeItem copy();

    boolean isItemFitting(@Nonnull ItemStack stack);

    @SideOnly(Side.CLIENT)
    ItemStack[] getEntryItemStacks();

    int getItemCount();

    void decreaseItemCount(int amount);
}
