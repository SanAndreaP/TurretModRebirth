package de.sanandrew.mods.turretmod.registry.assembly.recipe;

import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.turret.DualVariants;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
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
        ItemStack          result  = super.getCraftingResult(inv);
        DualVariants.Entry variant = TurretCrossbow.VARIANTS.get(inv);

        if( variant.id != 0 ) {
            new ItemTurret.TurretStats(null, null, variant.id).updateData(result);
        }

        return result;
    }
}
