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
import de.sanandrew.mods.turretmod.api.TurretHealthpack;
import de.sanandrew.mods.turretmod.api.TurretInfo;
import de.sanandrew.mods.turretmod.api.registry.TurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.registry.TurretHealthpackRegistry;
import net.minecraft.item.ItemStack;

public class TurretInfoApi<T extends Turret>
        implements TurretInfo<T>
{
    private final Class<T> turretEntityCls;
    private final String iconName;
    private final String turretName;

    protected TurretInfoApi(Class<T> turretCls, String tName, String icoName) {
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

    public static ItemStack[] getDepletedAmmoStacks(int ammoCount) {
        if( ammoCount <= 0 ) {
            return null;
        }

        int minAmmo = Integer.MAX_VALUE;
        TurretAmmo minAmmoType = null;

        for( TurretAmmo type : TurretAmmoRegistry.getTypes() ) {
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

    public static TurretAmmo getAmmo(ItemStack stack) {
        if( stack == null ) {
            return null;
        }

        for( TurretAmmo type : TurretAmmoRegistry.getTypes() ) {
            ItemStack ammoItem = type.getAmmoItem();
            if( ItemUtils.areStacksEqual(stack, ammoItem, ammoItem.hasTagCompound()) ) {
                return type;
            }
        }

        return null;
    }

    public static TurretHealthpack getHeal(ItemStack stack) {
        if( stack == null ) {
            return null;
        }

        for( TurretHealthpack type : TurretHealthpackRegistry.getTypes() ) {
            ItemStack healItem = type.getHealItem();
            if( ItemUtils.areStacksEqual(stack, healItem, healItem.hasTagCompound()) ) {
                return type;
            }
        }

        return null;
    }
}
