/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import net.minecraft.item.Item;

public class TmrItems
{
    public static Item turretItem;

    public static void initialize() {
        initializeItems();
        registerItems();
    }

    private static void initializeItems() {
        turretItem = new ItemTurret();
    }

    private static void registerItems() {
        SAPUtils.registerItems(turretItem);
    }
}
