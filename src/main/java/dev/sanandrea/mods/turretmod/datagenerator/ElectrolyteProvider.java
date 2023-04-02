/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.datagenerator;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElectrolyteProvider
        extends RecipeProvider
{
    public ElectrolyteProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        ElectrolyteBuilder.newElectrolyte(Items.APPLE)           .efficiency(1.3F).processTime(220).trash(Items.WHEAT_SEEDS).treasure(Items.GOLD_NUGGET)        .build(consumer);
        ElectrolyteBuilder.newElectrolyte(Items.BEETROOT)        .efficiency(1.0F).processTime(200).trash(Items.RED_DYE)    .treasure(Items.REDSTONE)           .build(consumer);
        ElectrolyteBuilder.newElectrolyte(Items.CARROT)          .efficiency(1.0F).processTime(250).trash(Items.ORANGE_DYE) .treasure(Items.SUGAR, 0.1F)        .build(consumer);
        ElectrolyteBuilder.newElectrolyte(Items.POTATO)          .efficiency(1.0F).processTime(200).trash(Items.SUGAR)      .treasure(Items.BAKED_POTATO)       .build(consumer);
        ElectrolyteBuilder.newElectrolyte(Items.POISONOUS_POTATO).efficiency(1.2F).processTime(150).trash(Items.SUGAR, 0.1F).treasure(Items.BAKED_POTATO, 0.01F).build(consumer);
    }
}
