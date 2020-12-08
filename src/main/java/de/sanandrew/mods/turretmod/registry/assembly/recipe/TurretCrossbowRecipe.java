package de.sanandrew.mods.turretmod.registry.assembly.recipe;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
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
        int ct = -2;
        int pt = -2;

        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if( ct != -1 ) {
                if( ItemStackUtils.isBlock(slotStack, Blocks.COBBLESTONE) )            { ct = checkType(ct, 0); }
                else if( ItemStackUtils.isBlock(slotStack, Blocks.MOSSY_COBBLESTONE) ) { ct = checkType(ct, 1); }
                else if( ItemStackUtils.isBlock(slotStack, Blocks.STONE) ) {
                    switch( slotStack.getMetadata() ) {
                        case 1: ct = checkType(ct, 2); break;
                        case 3: ct = checkType(ct, 3); break;
                        case 5: ct = checkType(ct, 4); break;
                    }
                }
            }

            if( pt != -1 && ItemStackUtils.isBlock(slotStack, Blocks.PLANKS) ) {
                pt = checkType(pt, slotStack.getItemDamage());
            }

            if( ct == -1 && pt == -1 ) {
                break;
            }
        }

        ct = Math.max(ct, 0);
        pt = Math.max(pt, 0);

        ItemStack result = super.getCraftingResult(inv);

        if( pt + ct > 0) {
            new ItemTurret.TurretStats(null, null, TurretCrossbow.Variant.get(ct, pt).id).updateData(result);
        }

        return result;
    }

    private static int checkType(int currType, int newType) {
        if( currType >= 0 && currType != newType ) {
            return -1;
        }

        return newType;
    }
}
