package de.sanandrew.mods.turretmod.registry.assembly.recipe;

import de.sanandrew.mods.turretmod.api.turret.ITurretVariant;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.turret.VariantHolder;
import de.sanandrew.mods.turretmod.registry.turret.TurretShotgun;
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
        ItemStack      result  = super.getCraftingResult(inv);
        ITurretVariant variant = TurretShotgun.VARIANTS.get(inv);

        if( variant != null ) {
            new ItemTurret.TurretStats(null, null, variant).updateData(result);
        }

        return result;
    }
}
