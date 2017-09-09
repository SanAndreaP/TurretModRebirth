/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@JEIPlugin
@SideOnly(Side.CLIENT)
public class JeiPlugin
    implements IModPlugin
{
    public JeiPlugin() { }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new AssemblyRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(TurretAssemblyRegistry.RecipeKeyEntry.class, new AssemblyRecipeWrapper.Factory(), AssemblyRecipeCategory.UID);

        registry.addRecipes(TurretAssemblyRegistry.INSTANCE.getRecipeList(), AssemblyRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.TURRET_ASSEMBLY), AssemblyRecipeCategory.UID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.TURRET_PLACER, itemStack -> {
            ITurret stype = TurretRegistry.INSTANCE.getTurret(itemStack);
            return stype != null ? stype.getId().toString() : null;
        });

        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.TURRET_UPGRADE, itemStack -> UpgradeRegistry.INSTANCE.getUpgradeId(itemStack).toString());

        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.TURRET_AMMO, itemStack -> AmmunitionRegistry.INSTANCE.getType(itemStack).getId().toString());

        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.REPAIR_KIT, itemStack -> RepairKitRegistry.INSTANCE.getType(itemStack).getUUID().toString());
    }
}
