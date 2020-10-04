package de.sanandrew.mods.turretmod.registry.assembly.recipe;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class TurretCrossbowRecipe
        extends AssemblyRecipe
{
    public TurretCrossbowRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
        super(id, group, ingredients, fluxPerTick, processTime, result);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        Integer type = null;

        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);
            if( ItemStackUtils.isBlock(slotStack, Blocks.PLANKS) ) {
                int meta = slotStack.getItemDamage();

                if( type != null && type != meta ) {
                    type = null;
                    break;
                }

                type = meta;
            }
        }

        ItemStack result = super.getCraftingResult(inv);

        if( type != null && BlockPlanks.EnumType.byMetadata(type) != BlockPlanks.EnumType.OAK ) {
            new ItemTurret.TurretStats(null, null, type).updateData(result);
        }

        return result;
    }
}
