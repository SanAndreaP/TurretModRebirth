/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.item.Item;

public class ItemUpgrade
        extends Item
{
    public final IUpgrade upgrade;

    public ItemUpgrade(IUpgrade upgrade) {
        super();

        this.upgrade = upgrade;

        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setRegistryName(upgrade.getId());
        this.setUnlocalizedName(upgrade.getId().toString());
    }
}
