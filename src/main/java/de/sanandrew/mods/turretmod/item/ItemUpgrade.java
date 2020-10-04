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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.TmrCreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemUpgrade
        extends Item
{
    public final IUpgrade upgrade;

    public ItemUpgrade(IUpgrade upgrade) {
        super();

        this.upgrade = upgrade;

        this.setCreativeTab(TmrCreativeTabs.UPGRADES);
        this.setRegistryName(upgrade.getId());
        this.setTranslationKey(upgrade.getId().toString());

        this.addPropertyOverride(new ResourceLocation("onTurret"), (stack, world, entity) -> entity instanceof EntityTurret ? 1 : 0);
    }
}
