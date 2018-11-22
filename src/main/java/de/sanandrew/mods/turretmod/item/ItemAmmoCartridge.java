/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.item;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

//TODO
public class ItemAmmoCartridge
        extends Item
{
    public ItemAmmoCartridge() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setRegistryName(TmrConstants.ID, "ammo.cartridge");
        this.setUnlocalizedName(TmrConstants.ID + ":ammo.cartridge");
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if( this.isInCreativeTab(tab) ) {
            list.addAll(AmmunitionRegistry.INSTANCE.getTypes().stream().map(a -> AmmunitionRegistry.INSTANCE.getItem(a.getId())).collect(Collectors.toList()));
        }
    }
}
