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
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.util.TmrCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.stream.Collectors;

public class ItemAmmo
        extends Item
{
    public ItemAmmo() {
        super();
        this.setCreativeTab(TmrCreativeTabs.TURRETS);
        this.setUnlocalizedName(TmrConstants.ID + ":turret_ammo");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TurretAmmo type = AmmoRegistry.INSTANCE.getType(stack);
        return String.format("%s.%s", this.getUnlocalizedName(), type.getName());
    }

    public ItemStack getAmmoItem(int stackSize, TurretAmmo type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret_ammo item with NULL type!");
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("ammoType", type.getId().toString());
        ItemStack stack = new ItemStack(this, stackSize);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.addAll(AmmoRegistry.INSTANCE.getRegisteredTypes().stream().map(type -> this.getAmmoItem(1, type)).collect(Collectors.toList()));
    }
}
