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

public class ItemAmmo
        extends Item
{
    public ItemAmmo() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_ammo");
        this.setRegistryName(TmrConstants.ID, "turret_ammo");
    }

    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        IAmmunition type = AmmunitionRegistry.INSTANCE.getType(stack);
        return String.format("%s.%s", this.getUnlocalizedName(), type.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if( this.isInCreativeTab(tab) ) {
            list.addAll(AmmunitionRegistry.INSTANCE.getRegisteredTypes().stream().map(AmmunitionRegistry.INSTANCE::getAmmoItem).collect(Collectors.toList()));
        }
    }
}
