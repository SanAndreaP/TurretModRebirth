package de.sanandrew.mods.turretmod.datagenerator;

import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.Turrets;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class AssemblyProvider
        extends RecipeProvider
{
    public AssemblyProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        AssemblyBuilder.newAssembly("turrets", TurretRegistry.INSTANCE.getItem(Turrets.CROSSBOW)).energyConsumption(10).processTime(100)
                       .ingredients(Ingredient)
    }
}
