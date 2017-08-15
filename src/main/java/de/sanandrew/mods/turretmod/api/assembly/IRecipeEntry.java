package de.sanandrew.mods.turretmod.api.assembly;

import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntryItem;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public interface IRecipeEntry
{
    RecipeEntryItem put(Item... items);

    RecipeEntryItem put(Block... blocks);

    RecipeEntryItem put(@Nonnull ItemStack... stacks);

    RecipeEntryItem put(String... oreDictNames);

    RecipeEntryItem drawTooltip();

    boolean shouldDrawTooltip();

    RecipeEntryItem copy();

    boolean isItemFitting(@Nonnull ItemStack stack);

    @SideOnly(Side.CLIENT)
    ItemStack[] getEntryItemStacks();

    int getItemCount();

    void decreaseItemCount(int amount);
}
