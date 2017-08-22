/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.compat.jei;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import mezz.jei.api.*;
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

        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.turret_assembly), AssemblyRecipeCategory.UID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.turret_placer, itemStack -> {
            TurretInfo stype = ItemTurret.getTurretInfo(itemStack);
            return stype != null ? stype.getUUID().toString() : null;
        });

        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.turret_upgrade, itemStack -> UpgradeRegistry.INSTANCE.getUpgradeUUID(itemStack).toString());

        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.turret_ammo, itemStack -> TurretAmmoRegistry.INSTANCE.getType(itemStack).getId().toString());

        subtypeRegistry.registerSubtypeInterpreter(ItemRegistry.repair_kit, itemStack -> RepairKitRegistry.INSTANCE.getType(itemStack).getUUID().toString());
    }
}
