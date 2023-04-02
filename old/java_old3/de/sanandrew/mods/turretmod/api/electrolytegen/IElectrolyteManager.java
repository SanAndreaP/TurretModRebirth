package de.sanandrew.mods.turretmod.api.electrolytegen;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface IElectrolyteManager
{
    List<IElectrolyteRecipe> getFuels();

    IElectrolyteRecipe getFuel(ResourceLocation id);

    IElectrolyteRecipe getFuel(ItemStack stack);

    boolean registerFuel(IElectrolyteRecipe recipe);

    void removeFuel(ResourceLocation id);
}
