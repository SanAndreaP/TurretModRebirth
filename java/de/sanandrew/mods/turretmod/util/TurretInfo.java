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
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import net.minecraft.item.ItemStack;

public class TurretInfo<T extends AEntityTurretBase>
{
    private final Class<T> turretEntityCls;
    private final String iconName;
    private final String turretName;
    private AmmoInfo[] ammoItems;
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

    public TurretInfo<T> applyAmmoItems(AmmoInfo... ammoTypes) {
        if( ammoTypes != null && this.ammoItems == null && ammoTypes.length > 0 ) {
            this.ammoItems = ammoTypes;
        }

        return this;
    }

    public TurretInfo<T> applyHealItems(HealInfo... healTypes) {
        if( healTypes != null && this.healItems == null && healTypes.length > 0 ) {
            this.healItems = healTypes;
        }

        return this;
    }

    public AmmoInfo getAmmo(ItemStack stack) {
        if( this.ammoItems == null ) {
            return null;
        }

        for( AmmoInfo info : this.ammoItems ) {
            if( ItemUtils.areStacksEqual(stack, info.item, info.item.hasTagCompound()) ) {
                return info;
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
        if( this.ammoItems == null || ammoCount <= 0 ) {
            return null;
        }

        int minAmmo = Integer.MAX_VALUE;
        AmmoInfo minAmmoType = null;

        for( AmmoInfo info : this.ammoItems ) {
            if( info.getAmount() < minAmmo ) {
                minAmmo = info.getAmount();
                minAmmoType = info;
            }
        }

        if( minAmmoType != null ) {
            ItemStack stack = minAmmoType.getItem().copy();
            stack.stackSize = ammoCount / minAmmo;
            return ItemUtils.getGoodItemStacks(stack);
        }

        return null;
    }

    public static class HealInfo {
        private ItemStack item;
        private float amount;

        public HealInfo(ItemStack ammoItem, float givesAmount) {
            this.item = ammoItem;
            this.amount = givesAmount;
        }

        public ItemStack getItem() {
            return this.item.copy();
        }

        public float getAmount() {
            return this.amount;
        }
    }

    public static class AmmoInfo {
        private ItemStack item;
        private ItemStack type;
        private int amount;

        public AmmoInfo(ItemStack ammoItem, ItemStack typeItem, int givesAmount) {
            this.item = ammoItem;
            this.type = typeItem;
            this.amount = givesAmount;
        }

        public ItemStack getItem() {
            return this.item.copy();
        }

        public ItemStack getTypeItem() {
            return this.type.copy();
        }

        public int getAmount() {
            return this.amount;
        }
    }
}
