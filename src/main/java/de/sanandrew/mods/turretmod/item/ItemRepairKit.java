/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.repairkit.IRepairKit;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.item.Item;

public class ItemRepairKit
        extends Item
{
    public final IRepairKit kit;

    public ItemRepairKit(IRepairKit kit) {
        super();

        this.kit = kit;

        this.setCreativeTab(TmrCreativeTabs.MISC);
        this.setRegistryName(kit.getId());
        this.setTranslationKey(kit.getId().toString());
    }
}
