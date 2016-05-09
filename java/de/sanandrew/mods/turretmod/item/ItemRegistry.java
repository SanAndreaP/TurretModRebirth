/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemRegistry
{
    public static ItemTurret turret;
    public static ItemAmmo ammo;
    public static ItemTurretControlUnit tcu;
    public static ItemRepairKit repairKit;
    public static ItemAssemblyUpgrade asbAuto;
    public static ItemAssemblyUpgrade asbSpeed;
    public static ItemAssemblyFilter asbFilter;
    public static ItemTurretUpgrade turretUpgrade;

    public static void initialize() {
        turret = new ItemTurret();
        ammo = new ItemAmmo();
        tcu = new ItemTurretControlUnit();
        repairKit = new ItemRepairKit();
        asbAuto = new ItemAssemblyUpgrade("auto");
        asbSpeed = new ItemAssemblyUpgrade("speed");
        asbFilter = new ItemAssemblyFilter();
        turretUpgrade = new ItemTurretUpgrade();

        registerItems(turret, ammo, tcu, repairKit, asbAuto, asbSpeed, asbFilter, turretUpgrade);
    }

    private static void registerItems(Item... items) {
        for(Item item : items) {
            String unlocName = item.getUnlocalizedName();
            unlocName = unlocName.substring(unlocName.indexOf(':') + 1);
            GameRegistry.registerItem(item, unlocName);
        }
    }
}
