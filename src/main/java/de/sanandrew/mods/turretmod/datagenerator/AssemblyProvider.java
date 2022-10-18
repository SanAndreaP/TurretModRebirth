package de.sanandrew.mods.turretmod.datagenerator;

import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.Turrets;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AssemblyProvider
        extends RecipeProvider
{
    public AssemblyProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        AssemblyBuilder.newAssembly("turrets", TurretRegistry.INSTANCE.getItem(Turrets.CROSSBOW)).energyConsumption(10).processTime(100)
                       .ingredients(new AssemblyBuilder.CompoundIngredientBuilder(12).tag(Tags.Items.COBBLESTONE)
                                                                                     .item(Items.STONE_BRICKS)
                                                                                     .item(Items.MOSSY_STONE_BRICKS)
                                                                                     .item(Items.CRACKED_STONE_BRICKS)
                                                                                     .item(Items.CHISELED_STONE_BRICKS).build())
                       .ingredient(1, Items.BOW)
                       .ingredient(4, Tags.Items.DUSTS_REDSTONE)
                       .ingredient(4, ItemTags.PLANKS)
                       .build(consumer);

        AssemblyBuilder.newAssembly("ammo", AmmunitionRegistry.INSTANCE.getItem(Ammunitions.BOLT)).energyConsumption(5).processTime(60)
                       .ingredient(1, Items.ARROW)
                       .build(consumer);
    }
}
