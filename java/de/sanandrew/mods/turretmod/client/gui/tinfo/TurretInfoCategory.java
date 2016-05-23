/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
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

    static {
        register(Resources.TINFO_GRP_TURRET.getResource(), "Turrets", "info about turrets",
                new TurretInfoEntryTurret(EntityTurretCrossbow.class));
        register(Resources.TINFO_GRP_AMMO.getResource(), "Ammunition", "info about turret ammo",
                 new TurretInfoEntry.EntryEmpty(ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.ARROW_UUID)), "single_arrows"),
                 new TurretInfoEntry.EntryEmpty(ItemRegistry.ammo.getAmmoItem(1, AmmoRegistry.INSTANCE.getType(TurretAmmoArrow.QUIVER_UUID)), "multi_arrows"));
        register(Resources.TINFO_GRP_UPGRADE.getResource(), "Upgrades", "info about turret upgrades");
        register(Resources.TINFO_GRP_MISC.getResource(), "Misc", "info about misc stuff",
                new TurretInfoEntry.EntryEmpty(new ItemStack(BlockRegistry.turretAssembly), "assembly"),
                new TurretInfoEntry.EntryEmpty(new ItemStack(ItemRegistry.tcu), "tcu"));
        register(Resources.TINFO_GRP_INFO.getResource(), "Info", "about this mod");
    }
}
