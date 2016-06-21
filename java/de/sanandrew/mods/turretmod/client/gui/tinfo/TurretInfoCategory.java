/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryAmmo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryGenerator;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryInfo;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscAssembleable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscCraftable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryTurret;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryUpgrade;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.CraftingRecipes;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
            register(Resources.TINFO_GRP_TURRET.getResource(), String.format(Lang.TINFO_CATEGORY_NAME, "turrets"), entries);
        }

        {
            List<TurretAmmo> infos = AmmoRegistry.INSTANCE.getRegisteredTypes();
            List<UUID> idEntries = new ArrayList<>();
            List<TurretInfoEntry> entriesList = new ArrayList<>();
            TurretInfoEntry[] entries;
            for( TurretAmmo ammo : infos ) {
                UUID groupId = ammo.getGroupId();
                if( !idEntries.contains(groupId) ) {
                    idEntries.add(groupId);
                    entriesList.add(new TurretInfoEntryAmmo(groupId));
                }
            }
            entries = entriesList.toArray(new TurretInfoEntry[entriesList.size()]);
            register(Resources.TINFO_GRP_AMMO.getResource(), String.format(Lang.TINFO_CATEGORY_NAME, "ammo"), entries);
        }

        {
            TurretUpgrade[] infos = UpgradeRegistry.INSTANCE.getRegisteredTypes();
            TurretInfoEntry[] entries = new TurretInfoEntry[infos.length + 3];

            entries[0] = new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.asbAuto), TurretAssemblyRecipes.UPG_AT_AUTO);
            entries[1] = new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.asbFilter), TurretAssemblyRecipes.UPG_AT_FILTER);
            entries[2] = new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.asbSpeed), TurretAssemblyRecipes.UPG_AT_SPEED);

            for( int i = 0; i < infos.length; i++ ) {
                entries[i + 3] = new TurretInfoEntryUpgrade(UpgradeRegistry.INSTANCE.getUpgradeUUID(infos[i]));
            }
            register(Resources.TINFO_GRP_UPGRADE.getResource(), String.format(Lang.TINFO_CATEGORY_NAME, "upgrades"), entries);

        }
        register(Resources.TINFO_GRP_MISC.getResource(), String.format(Lang.TINFO_CATEGORY_NAME, "misc"),
                 new TurretInfoEntryMiscCraftable(CraftingRecipes.assemblyTable),
                 new TurretInfoEntryGenerator(),
                 new TurretInfoEntryMiscAssembleable(new ItemStack(ItemRegistry.tcu), TurretAssemblyRecipes.TCU));
        register(Resources.TINFO_GRP_INFO.getResource(), String.format(Lang.TINFO_CATEGORY_NAME, "info"), new TurretInfoEntryInfo());
    }
}
