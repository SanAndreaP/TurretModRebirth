/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.recipe;

import dev.sanandrea.mods.turretmod.api.ILeveledInventory;
import dev.sanandrea.mods.turretmod.api.assembly.ICountedIngredient;
import dev.sanandrea.mods.turretmod.api.turret.IVariant;
import dev.sanandrea.mods.turretmod.api.turret.IVariantHolder;
import dev.sanandrea.mods.turretmod.entity.turret.Turrets;
import dev.sanandrea.mods.turretmod.item.TurretItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class TurretVariantRecipe
        extends AssemblyRecipe
{
    TurretVariantRecipe(ResourceLocation id, String group, NonNullList<ICountedIngredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
        super(id, group, ingredients, fluxPerTick, processTime, result);
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull ILeveledInventory inv) {
        IVariantHolder vh = getVariantHolder();

        ItemStack result  = super.assemble(inv);
        IVariant  variant = vh.getVariant(inv);

        if( !vh.isDefaultVariant(variant) ) {
            new TurretItem.TurretStats(null, null, variant).updateData(result);
        }

        return result;
    }

    abstract IVariantHolder getVariantHolder();

    public static class Crossbow
            extends TurretVariantRecipe
    {
        public static final AssemblyRecipe.Serializer SERIALIZER = new AssemblyRecipe.Serializer(Crossbow::new);

        public Crossbow(ResourceLocation id, String group, NonNullList<ICountedIngredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
            super(id, group, ingredients, fluxPerTick, processTime, result);
        }

        @Override
        IVariantHolder getVariantHolder() {
            return (IVariantHolder) Turrets.CROSSBOW;
        }
    }

//    public static class Shotgun
//            extends TurretVariantRecipe
//    {
//        public Shotgun(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
//            super(id, group, ingredients, fluxPerTick, processTime, result);
//        }
//
//        @Override
//        VariantContainer.ItemVariants<?> getVariantHolder() {
//            return TurretShotgun.VARIANTS;
//        }
//    }

//    public static class Cryolator
//            extends TurretVariantRecipe
//    {
//        public Cryolator(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
//            super(id, group, ingredients, fluxPerTick, processTime, result);
//        }
//
//        @Override
//        VariantContainer.ItemVariants<?> getVariantHolder() {
//            return TurretCryolator.VARIANTS;
//        }
//    }
//
//    public static class Harpoon
//            extends TurretVariantRecipe
//    {
//        public Harpoon(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
//            super(id, group, ingredients, fluxPerTick, processTime, result);
//        }
//
//        @Override
//        VariantContainer.ItemVariants<?> getVariantHolder() {
//            return TurretHarpoon.VARIANTS;
//        }
//    }
//
//    public static class Revolver
//            extends TurretVariantRecipe
//    {
//        public Revolver(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
//            super(id, group, ingredients, fluxPerTick, processTime, result);
//        }
//
//        @Override
//        VariantContainer.ItemVariants<?> getVariantHolder() {
//            return TurretRevolver.VARIANTS;
//        }
//    }
//
//    public static class Minigun
//            extends TurretVariantRecipe
//    {
//        public Minigun(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int fluxPerTick, int processTime, ItemStack result) {
//            super(id, group, ingredients, fluxPerTick, processTime, result);
//        }
//
//        @Override
//        VariantContainer.ItemVariants<?> getVariantHolder() {
//            return TurretMinigun.VARIANTS;
//        }
//    }
}