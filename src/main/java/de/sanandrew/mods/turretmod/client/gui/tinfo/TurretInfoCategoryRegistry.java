/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategory;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryAmmo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryGenerator;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryInfo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscAssembleable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscCraftable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryTurret;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public final class TurretInfoCategoryRegistry
        implements ITurretInfoCategoryRegistry
{
    private static final List<ITurretInfoCategory> CATEGORIES = new ArrayList<>();
    public static final TurretInfoCategoryRegistry INSTANCE = new TurretInfoCategoryRegistry();

    private TurretInfoCategoryRegistry() { }

    @Override
    public ITurretInfoCategory registerCategory(ResourceLocation categoryIcon, String title) {
        int ind = CATEGORIES.size();
        TurretInfoCategory cat = new TurretInfoCategory(ind, categoryIcon, title);
        CATEGORIES.add(ind, cat);

        return cat;
    }

    public static void initialize(ITurretInfoCategoryRegistry registry) {
        {
            ITurretInfoEntry[] entries = TurretRegistry.INSTANCE.getTurrets().stream().map(TurretInfoEntryTurret::new).toArray(ITurretInfoEntry[]::new);
            registry.registerCategory(Resources.TINFO_GRP_TURRET.getResource(), Lang.TINFO_CATEGORY_NAME.get("turrets")).addEntry(entries);
        }

        {
            ITurretInfoEntry[] entries = TurretAmmoRegistry.INSTANCE.getGroups().stream().map(TurretInfoEntryAmmo::new).toArray(ITurretInfoEntry[]::new);
            registry.registerCategory(Resources.TINFO_GRP_AMMO.getResource(), Lang.TINFO_CATEGORY_NAME.get("turret_ammo")).addEntry(entries);
        }

        {
            ITurretInfoEntry[] entries = UpgradeRegistry.INSTANCE.getUpgrades().stream().map(TurretInfoEntryUpgrade::new).toArray(ITurretInfoEntry[]::new);

            registry.registerCategory(Resources.TINFO_GRP_UPGRADE.getResource(), Lang.TINFO_CATEGORY_NAME.get("upgrades"))
                    .addEntry(new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.assembly_upg_auto), TurretAssemblyRecipes.UPG_AT_AUTO))
                    .addEntry(new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.assembly_upg_filter), TurretAssemblyRecipes.UPG_AT_FILTER))
                    .addEntry(new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.assembly_upg_speed), TurretAssemblyRecipes.UPG_AT_SPEED))
                    .addEntry(entries);
        }

        registry.registerCategory(Resources.TINFO_GRP_MISC.getResource(), Lang.TINFO_CATEGORY_NAME.get("misc"))
                .addEntry(new TurretInfoEntryMiscCraftable(getRecipe(new ItemStack(BlockRegistry.turret_assembly))))
                .addEntry(new TurretInfoEntryGenerator(getRecipe(new ItemStack(BlockRegistry.electrolyte_generator))))
                .addEntry(new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.turret_control_unit), TurretAssemblyRecipes.TCU));

        registry.registerCategory(Resources.TINFO_GRP_INFO.getResource(), Lang.TINFO_CATEGORY_NAME.get("info")).addEntry(new TurretInfoEntryInfo());
    }

    @Override
    public ITurretInfoCategory[] getCategories() {
        return CATEGORIES.toArray(new ITurretInfoCategory[CATEGORIES.size()]);
    }

    @Override
    public ITurretInfoCategory getCategory(int index) {
        return CATEGORIES.get(index);
    }

    @Override
    public int getCategoryCount() {
        return CATEGORIES.size();
    }

    private static IRecipe getRecipe(ItemStack stack) {
        IRecipe rcp;
        for( ResourceLocation key : CraftingManager.REGISTRY.getKeys() ) {
            rcp = CraftingManager.REGISTRY.getObject(key);

            if( rcp != null && ItemStackUtils.isValid(rcp.getRecipeOutput()) && ItemStackUtils.areEqual(rcp.getRecipeOutput(), stack, false, false, false) ) {
                return rcp;
            }
        }

        return null;
    }
}
