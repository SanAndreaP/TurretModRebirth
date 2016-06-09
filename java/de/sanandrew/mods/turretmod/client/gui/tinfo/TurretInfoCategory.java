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
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryMiscCraftable;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryTurret;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntryUpgrade;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.TurretUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.CraftingRecipes;
import de.sanandrew.mods.turretmod.util.Resources;
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
    private String desc;
    private List<TurretInfoEntry> entries;

    public static TurretInfoCategory register(ResourceLocation categoryIcon, String title, String desc, TurretInfoEntry... entries) {
        int ind = categories.size();
        TurretInfoCategory cat = new TurretInfoCategory(ind, categoryIcon, title, desc);
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

    private TurretInfoCategory(int index, ResourceLocation categoryIcon, String title, String desc) {
        this.index = index;
        this.catIcon = categoryIcon;
        this.title = title;
        this.desc = desc;
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

    public String getDesc() {
        return this.desc;
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
            register(Resources.TINFO_GRP_TURRET.getResource(), "Turrets", "info about turrets", entries);
        }

        {
            List<TurretAmmo> infos = AmmoRegistry.INSTANCE.getRegisteredTypes();
            List<UUID> idEntries = new ArrayList<>();
            List<TurretInfoEntry> entries = new ArrayList<>();
            for( TurretAmmo ammo : infos ) {
                UUID groupId = ammo.getGroupId();
                if( !idEntries.contains(groupId) ) {
                    idEntries.add(groupId);
                    entries.add(new TurretInfoEntryAmmo(groupId));
                }
            }
            register(Resources.TINFO_GRP_AMMO.getResource(), "Ammunition", "info about turret ammo", entries.toArray(new TurretInfoEntry[entries.size()]));
        }

        {
            TurretUpgrade[] infos = UpgradeRegistry.INSTANCE.getRegisteredUpgrades();
            TurretInfoEntry[] entries = new TurretInfoEntry[infos.length];
            for( int i = 0; i < infos.length; i++ ) {
                entries[i] = new TurretInfoEntryUpgrade(UpgradeRegistry.INSTANCE.getUpgradeUUID(infos[i]));
            }
            register(Resources.TINFO_GRP_UPGRADE.getResource(), "Upgrades", "info about turret upgrades", entries);
        }
        register(Resources.TINFO_GRP_MISC.getResource(), "Misc", "info about misc stuff",
                 new TurretInfoEntryMiscCraftable(CraftingRecipes.assemblyTable),
                 new TurretInfoEntryGenerator());
        register(Resources.TINFO_GRP_INFO.getResource(), "Info", "about this mod", new TurretInfoEntryInfo());
    }
}
