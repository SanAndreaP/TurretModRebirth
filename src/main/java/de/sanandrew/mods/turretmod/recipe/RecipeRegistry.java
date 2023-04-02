/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.recipe;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.tileentity.electrolyte.ElectrolyteRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class RecipeRegistry
{
    private static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TmrConstants.ID);

    public static final ElectrolyteRecipe.Serializer ELECTROLYTE_RECIPE_SER = new ElectrolyteRecipe.Serializer();
    public static final AssemblyRecipe.Serializer    ASSEMBLY_RECIPE_SER = new AssemblyRecipe.Serializer();

    private RecipeRegistry() { /* no-op */ }

    public static void register(IEventBus bus)
    {
        RECIPE_SERIALIZERS.register("electrolyte_generator", () -> ELECTROLYTE_RECIPE_SER);
        RECIPE_SERIALIZERS.register("turret_assembly", () -> ASSEMBLY_RECIPE_SER);

        RECIPE_SERIALIZERS.register("turret_assembly_special_crossbow_turret", () -> TurretVariantRecipe.Crossbow.SERIALIZER);
        RECIPE_SERIALIZERS.register("crafting_special_tippedcrossbowbolt", () -> TippedCrossbowBoltRecipe.SERIALIZER);

        RECIPE_SERIALIZERS.register(bus);
    }
}
