/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class TmrCreativeTabs
{
    public static final CreativeTabs TURRETS = new CreativeTabs("turrets") {
        @Override public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.dispenser);
        }
    };
}
