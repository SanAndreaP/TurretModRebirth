package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@JEIPlugin
public class JeiPlugin extends BlankModPlugin {
    public JeiPlugin() {}

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeCategories(new AssemblyRecipeCategory(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(new AssemblyRecipeHandler());

        registry.addRecipes(TurretAssemblyRecipes.INSTANCE.getRecipeList());

        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.turret_assembly), AssemblyRecipeCategory.UID);
    }
}
