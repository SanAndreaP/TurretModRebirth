package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IRecipeEntry
{
    RecipeEntry put(Item... items);

    RecipeEntry put(Block... blocks);

    RecipeEntry put(ItemStack... stacks);

    RecipeEntry put(String... oreDictNames);

    RecipeEntry drawTooltip();

    boolean shouldDrawTooltip();

    RecipeEntry copy();

    boolean isItemFitting(ItemStack stack);

    @SideOnly(Side.CLIENT)
    ItemStack[] getEntryItemStacks();

    int getItemCount();

    void decreaseItemCount(int amount);
}
