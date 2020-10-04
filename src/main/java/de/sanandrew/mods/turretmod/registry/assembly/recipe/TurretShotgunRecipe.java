package de.sanandrew.mods.turretmod.registry.assembly.recipe;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class TurretShotgunRecipe
        extends AssemblyRecipe
{
    public TurretShotgunRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
        super(id, group, ingredients, fluxPerTick, processTime, result);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        int typeStone = -1;
        int typeLog = -1;

        boolean checkStone = true;
        boolean checkLog = true;
        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);
            if( checkStone && ItemStackUtils.isBlock(slotStack, Blocks.STONE) ) {
                int meta = slotStack.getItemDamage();

                if( typeStone >= 0 && typeStone != meta ) {
                    typeStone = -1;
                    checkStone = false;
                } else {
                    typeStone = meta;
                }
            }

            if( checkLog && (ItemStackUtils.isBlock(slotStack, Blocks.LOG) || ItemStackUtils.isBlock(slotStack, Blocks.LOG2)) ) {
                int meta = slotStack.getItemDamage() + (ItemStackUtils.isBlock(slotStack, Blocks.LOG2) ? 4 : 0);

                if( typeLog >= 0 && typeLog != meta ) {
                    typeLog = -1;
                    checkLog = false;
                } else {
                    typeLog = meta;
                }
            }

            if( !checkStone && !checkLog ) {
                break;
            }
        }

        ItemStack result = super.getCraftingResult(inv);

        new ItemTurret.TurretStats(null, null, (typeStone & 0b1111) | ((typeLog & 0b1111) << 4)).updateData(result);

        return result;
    }
}
