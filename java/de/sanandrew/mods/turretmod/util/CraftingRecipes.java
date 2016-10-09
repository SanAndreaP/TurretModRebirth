/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class CraftingRecipes
{
    public static IRecipe assemblyTable;
    public static IRecipe potatoGenerator;

    @SuppressWarnings("unchecked")
    public static void initialize() {
        assemblyTable = CraftingManager.getInstance().addRecipe(new ItemStack(BlockRegistry.assemblyTable, 1),
                "ROR", "IAI", "CFC",
                'R', new ItemStack(Items.REPEATER),
                'O', new ItemStack(Blocks.OBSIDIAN),
                'I', new ItemStack(Items.IRON_INGOT),
                'A', new ItemStack(Blocks.ANVIL, 1, 0),
                'C', new ItemStack(Blocks.COBBLESTONE),
                'F', new ItemStack(Blocks.FURNACE));

        potatoGenerator = new ShapedOreRecipe(BlockRegistry.potatoGenerator,
                true,
                "IBG", "RCR", "BPB",
                'I', "ingotIron",
                'B', "ingotBrick",
                'G', "ingotGold",
                'R', "dustRedstone",
                'C', Items.CAULDRON,
                'P', Items.REPEATER);
        CraftingManager.getInstance().getRecipeList().add(potatoGenerator);

        CraftingManager.getInstance().addShapelessRecipe(new ItemStack(ItemRegistry.turretInfo, 1), Items.WRITABLE_BOOK, Blocks.DISPENSER);
    }
}
