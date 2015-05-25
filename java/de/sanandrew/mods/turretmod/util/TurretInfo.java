/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretAmmo;
import de.sanandrew.mods.turretmod.util.TurretRegistry.HealInfo;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TurretInfo<T extends Turret>
{
    private final Class<T> turretEntityCls;
    private final String iconName;
    private final String turretName;
    private List<TurretAmmo> ammoTypes = new ArrayList<>();
    private HealInfo[] healItems;

    protected TurretInfo(Class<T> turretCls, String tName, String icoName) {
        this.turretEntityCls = turretCls;
        this.turretName = tName;
        this.iconName = icoName;
    }

    public Class<T> getTurretClass() {
        return this.turretEntityCls;
    }

    public String getName() {
        return this.turretName;
    }

    public String getIcon() {
        return this.iconName;
    }

    public TurretInfo<T> addAmmoType(TurretAmmo ammoType) {
        if( ammoType != null ) {
            this.ammoTypes.add(ammoType);
        }

        return this;
    }

    public TurretInfo<T> applyHealItems(HealInfo... healTypes) {
        if( healTypes != null && this.healItems == null && healTypes.length > 0 ) {
            this.healItems = healTypes;
        }

        return this;
    }

    public TurretAmmo getAmmo(ItemStack stack) {
        if( this.ammoTypes == null ) {
            return null;
        }

        for( TurretAmmo type : this.ammoTypes) {
            ItemStack ammoItem = type.getAmmoItem();
            if( ItemUtils.areStacksEqual(stack, ammoItem, ammoItem.hasTagCompound()) ) {
                return type;
            }
        }

        return null;
    }

    public HealInfo getHeal(ItemStack stack) {
        if( this.healItems == null ) {
            return null;
        }

        for( HealInfo info : this.healItems ) {
            if( ItemUtils.areStacksEqual(stack, info.item, info.item.hasTagCompound()) ) {
                return info;
            }
        }

        return null;
    }

    public ItemStack[] getDepletedAmmoStacks(int ammoCount) {
        if( this.ammoTypes == null || ammoCount <= 0 ) {
            return null;
        }

        int minAmmo = Integer.MAX_VALUE;
        TurretAmmo minAmmoType = null;

        for( TurretAmmo type : this.ammoTypes) {
            int typeAmount = type.getAmount();
            if( typeAmount < minAmmo ) {
                minAmmo = typeAmount;
                minAmmoType = type;
            }
        }

        if( minAmmoType != null ) {
            ItemStack stack = minAmmoType.getAmmoItem().copy();
            stack.stackSize = ammoCount / minAmmo;
            return ItemUtils.getGoodItemStacks(stack);
        }

        return null;
    }
}
