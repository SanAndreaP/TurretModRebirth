/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.item.ItemTurretAmmo;
import de.sanandrew.mods.turretmod.item.ItemTurretControlUnit;
import de.sanandrew.mods.turretmod.item.ItemTurretUpgrade;
import net.minecraft.item.Item;

public class TmrItems
{
    public static Item turretItem;
    public static Item turretCtrlUnit;
    public static ItemTurretUpgrade turretUpgrade;
    public static ItemTurretAmmo turretAmmo;

    public static void initialize() {
        initializeItems();
        registerItems();
    }

    private static void initializeItems() {
        turretItem = new ItemTurret();
        turretCtrlUnit = new ItemTurretControlUnit();
        turretUpgrade = new ItemTurretUpgrade();
        turretAmmo = new ItemTurretAmmo();
    }

    private static void registerItems() {
        SAPUtils.registerItems(turretItem, turretCtrlUnit, turretUpgrade, turretAmmo);
    }
}
