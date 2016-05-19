/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoArrow;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TurretInfoCategory
{
    private static List<TurretInfoCategory> categories = new ArrayList<>();

    private ResourceLocation catIcon;
    private String title;
    private String desc;
    private List<TurretInfoEntry> entries;

    public static TurretInfoCategory register(ResourceLocation categoryIcon, String title, String desc, TurretInfoEntry... entries) {
        TurretInfoCategory cat = new TurretInfoCategory(categoryIcon, title, desc);
        if( entries != null && entries.length > 0 ) {
            cat.entries.addAll(Arrays.asList(entries));
        }
        categories.add(cat);

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

    private TurretInfoCategory(ResourceLocation categoryIcon, String title, String desc) {
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

    static {
        register(Resources.TINFO_GRP_TURRET.getResource(), "Turrets", "info about turrets");
        register(Resources.TINFO_GRP_AMMO.getResource(), "Ammunition", "info about turret ammo",
                 new TurretInfoEntry.EntryEmpty(ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.ARROW_UUID)), "single_arrows"),
                 new TurretInfoEntry.EntryEmpty(ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.QUIVER_UUID)), "multi_arrows"));
        register(Resources.TINFO_GRP_UPGRADE.getResource(), "Upgrades", "info about turret upgrades");
        register(Resources.TINFO_GRP_MISC.getResource(), "Misc", "info about misc stuff");
        register(Resources.TINFO_GRP_INFO.getResource(), "Info", "about this mod");
    }
}
