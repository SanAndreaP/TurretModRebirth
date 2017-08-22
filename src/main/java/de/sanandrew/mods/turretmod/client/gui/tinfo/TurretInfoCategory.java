/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryAmmo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryGenerator;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryInfo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscAssembleable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscCraftable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryTurret;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmo;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TmrInternalPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TurretInfoCategory
{
    private static List<TurretInfoCategory> categories = new ArrayList<>();

    public final int index;
    private ResourceLocation catIcon;
    private String title;
    private List<TurretInfoEntry> entries;

    public static TurretInfoCategory register(ResourceLocation categoryIcon, String title, TurretInfoEntry... entries) {
        int ind = categories.size();
        TurretInfoCategory cat = new TurretInfoCategory(ind, categoryIcon, title);
        if( entries != null && entries.length > 0 ) {
            cat.entries.addAll(Arrays.asList(entries));
        }
        categories.add(ind, cat);

        return cat;
    }

    public static TurretInfoCategory[] getCategories() {
        return categories.toArray(new TurretInfoCategory[categories.size()]);
    }

    public static TurretInfoCategory getCategory(int index) {
        return categories.get(index);
    }

    public static int getCategoryCount() {
        return categories.size();
    }

    private TurretInfoCategory(int index, ResourceLocation categoryIcon, String title) {
        this.index = index;
        this.catIcon = categoryIcon;
        this.title = title;
        this.entries = new ArrayList<>();
    }

    public void addEntry(TurretInfoEntry entry) {
        this.entries.add(entry);
    }

    public ResourceLocation getIcon() {
        return this.catIcon;
    }

    public String getTitle() {
        return this.title;
    }

    public TurretInfoEntry[] getEntries() {
        return this.entries.toArray(new TurretInfoEntry[this.entries.size()]);
    }

    public TurretInfoEntry getEntry(int index) {
        return this.entries.get(index);
    }

    public int getEntryCount() {
        return this.entries.size();
    }

    public static void initialize() {
        {
            List<TurretInfo> infos = TurretRegistry.INSTANCE.getRegisteredInfos();
            TurretInfoEntry[] entries = new TurretInfoEntry[infos.size()];
            for( int i = 0, cnt = infos.size(); i < cnt; i++ ) {
                entries[i] = new TurretInfoEntryTurret(infos.get(i).getTurretClass());
            }
            register(Resources.TINFO_GRP_TURRET.getResource(), Lang.TINFO_CATEGORY_NAME.get("turrets"), entries);
        }

        {
            List<ITurretAmmo> infos = TmrInternalPlugin.ammoRegistry.getRegisteredTypes();
            List<UUID> idEntries = new ArrayList<>();
            List<TurretInfoEntry> entriesList = new ArrayList<>();
            TurretInfoEntry[] entries;
            for( ITurretAmmo ammo : infos ) {
                UUID groupId = ammo.getGroupId();
                if( !idEntries.contains(groupId) ) {
                    idEntries.add(groupId);
                    entriesList.add(new TurretInfoEntryAmmo(groupId));
                }
            }
            entries = entriesList.toArray(new TurretInfoEntry[entriesList.size()]);
            register(Resources.TINFO_GRP_AMMO.getResource(), Lang.TINFO_CATEGORY_NAME.get("turret_ammo"), entries);
        }

        {
            TurretUpgrade[] infos = UpgradeRegistry.INSTANCE.getRegisteredTypes();
            TurretInfoEntry[] entries = new TurretInfoEntry[infos.length + 3];

            entries[0] = new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.assembly_upg_auto), TurretAssemblyRecipes.UPG_AT_AUTO);
            entries[1] = new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.assembly_upg_filter), TurretAssemblyRecipes.UPG_AT_FILTER);
            entries[2] = new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.assembly_upg_speed), TurretAssemblyRecipes.UPG_AT_SPEED);

            for( int i = 0; i < infos.length; i++ ) {
                entries[i + 3] = new TurretInfoEntryUpgrade(UpgradeRegistry.INSTANCE.getUpgradeUUID(infos[i]));
            }
            register(Resources.TINFO_GRP_UPGRADE.getResource(), Lang.TINFO_CATEGORY_NAME.get("upgrades"), entries);

        }

        register(Resources.TINFO_GRP_MISC.getResource(), Lang.TINFO_CATEGORY_NAME.get("misc"),
                 new TurretInfoEntryMiscCraftable(getRecipe(new ItemStack(BlockRegistry.turret_assembly))),
                 new TurretInfoEntryGenerator(getRecipe(new ItemStack(BlockRegistry.electrolyte_generator))),
                 new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.turret_control_unit), TurretAssemblyRecipes.TCU));
        register(Resources.TINFO_GRP_INFO.getResource(), Lang.TINFO_CATEGORY_NAME.get("info"), new TurretInfoEntryInfo());
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
