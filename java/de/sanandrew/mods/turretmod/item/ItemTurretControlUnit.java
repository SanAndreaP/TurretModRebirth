/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.item.Item;

public class ItemTurretControlUnit
        extends Item
{
    public ItemTurretControlUnit() {
        super();

        this.setUnlocalizedName(TurretMod.MOD_ID + ":turretControlUnit");
        this.setTextureName(TurretMod.MOD_ID + ":tcu");
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
    }
}
