package de.sanandrew.mods.turretmod.registry.assembly.recipe;

import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
import de.sanandrew.mods.turretmod.registry.turret.TurretCryolator;
import de.sanandrew.mods.turretmod.registry.turret.TurretHarpoon;
import de.sanandrew.mods.turretmod.registry.turret.TurretMinigun;
import de.sanandrew.mods.turretmod.registry.turret.TurretRevolver;
import de.sanandrew.mods.turretmod.registry.turret.TurretShotgun;
import de.sanandrew.mods.turretmod.registry.turret.variant.VariantContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public abstract class TurretVariantRecipe
        extends AssemblyRecipe
{
    public TurretVariantRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
        super(id, group, ingredients, fluxPerTick, processTime, result);
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        VariantContainer.ItemVariants<?> vh = getVariantHolder();

        ItemStack result  = super.getCraftingResult(inv);
        IVariant  variant = vh.get(inv);

        if( !vh.isDefaultVariant(variant) ) {
            new ItemTurret.TurretStats(null, null, variant).updateData(result);
        }

        return result;
    }

    abstract VariantContainer.ItemVariants<?> getVariantHolder();

    public static class Crossbow
            extends TurretVariantRecipe
    {
        public Crossbow(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        VariantContainer.ItemVariants<?> getVariantHolder() {
            return TurretCrossbow.VARIANTS;
        }
    }

    public static class Shotgun
            extends TurretVariantRecipe
    {
        public Shotgun(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        VariantContainer.ItemVariants<?> getVariantHolder() {
            return TurretShotgun.VARIANTS;
        }
    }

    public static class Cryolator
            extends TurretVariantRecipe
    {
        public Cryolator(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        VariantContainer.ItemVariants<?> getVariantHolder() {
            return TurretCryolator.VARIANTS;
        }
    }

    public static class Harpoon
            extends TurretVariantRecipe
    {
        public Harpoon(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        VariantContainer.ItemVariants<?> getVariantHolder() {
            return TurretHarpoon.VARIANTS;
        }
    }

    public static class Revolver
            extends TurretVariantRecipe
    {
        public Revolver(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        VariantContainer.ItemVariants<?> getVariantHolder() {
            return TurretRevolver.VARIANTS;
        }
    }

    public static class Minigun
            extends TurretVariantRecipe
    {
        public Minigun(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        VariantContainer.ItemVariants<?> getVariantHolder() {
            return TurretMinigun.VARIANTS;
        }
    }
}
