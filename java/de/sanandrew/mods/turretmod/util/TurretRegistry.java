/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import com.sun.istack.internal.NotNull;
import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretOP;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.techii.EntityTurretRevolver;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import javax.annotation.Nullable;
import java.util.*;

public final class TurretRegistry<T extends AEntityTurretBase>
{
    private static final Map<Class<? extends AEntityTurretBase>, TurretRegistry<? extends AEntityTurretBase>> CLASS_MAP = new HashMap<>();
    private static final Map<String, TurretRegistry<? extends AEntityTurretBase>> NAME_MAP = new HashMap<>();
    private static final List<String> REG_SORTED_TURRET_NAME_LIST = new ArrayList<>();

    public static <T extends AEntityTurretBase> TurretRegistry<T> registerNewTurret(Class<T> turretEntityCls, String turretName, String iconName) {
        TurretRegistry<T> turret = new TurretRegistry<>(turretEntityCls, turretName, iconName);
        CLASS_MAP.put(turretEntityCls, turret);
        NAME_MAP.put(turretName, turret);
        REG_SORTED_TURRET_NAME_LIST.add(turretName);

        return turret;
    }

    public static void initialize() {
        registerNewTurret(EntityTurretCrossbow.class, "turretCrossbow", TurretMod.MOD_ID + ":turret_crossbow")
                .applyAmmoItems(new AmmoInfo(new ItemStack(Items.arrow), new ItemStack(Items.arrow), 1),
                                new AmmoInfo(new ItemStack(Blocks.bedrock), new ItemStack(Items.arrow), 128))
                .applyHealItems(new HealInfo(new ItemStack(Blocks.cobblestone), 10.0F));
        registerNewTurret(EntityTurretRevolver.class, "turretRevolver", TurretMod.MOD_ID + ":turret_revolver")
                .applyAmmoItems(new AmmoInfo(new ItemStack(Items.arrow), new ItemStack(Items.arrow), 1),
                                new AmmoInfo(new ItemStack(Blocks.bedrock), new ItemStack(Items.arrow), 128)) //TODO: add bullets!
                .applyHealItems(new HealInfo(new ItemStack(Items.iron_ingot), 4.0F),
                                new HealInfo(new ItemStack(Blocks.iron_block), 36.0F)
                );
        registerNewTurret(EntityTurretOP.class, "turretOP", TurretMod.MOD_ID + ":turret_op");
    }

    public static TurretRegistry<? extends AEntityTurretBase> getTurretInfo(Class<? extends AEntityTurretBase> clazz) {
        return CLASS_MAP.get(clazz);
    }

    public static TurretRegistry<? extends AEntityTurretBase> getTurretInfo(String name) {
        return NAME_MAP.get(name);
    }

    public static List<String> getAllTurretNamesSorted() {
        return new ArrayList<>(REG_SORTED_TURRET_NAME_LIST);
    }

    private final Class<T> turretEntityCls;
    private final String iconName;
    private final String turretName;
    private AmmoInfo[] ammoItems;
    private HealInfo[] healItems;

    private TurretRegistry(Class<T> turretCls, String tName, String icoName) {
        this.turretEntityCls = turretCls;
        this.turretName = tName;
        this.iconName = icoName;
    }

    public Class<T> getTurretClass() {
        return this.turretEntityCls;
    }

    public String getTurretName() {
        return this.turretName;
    }

    public String getTurretIcon() {
        return this.iconName;
    }

    public TurretRegistry<T> applyAmmoItems(AmmoInfo... ammoTypes) {
        if( ammoTypes != null && this.ammoItems == null && ammoTypes.length > 0 ) {
            this.ammoItems = ammoTypes;
        }

        return this;
    }

    public TurretRegistry<T> applyHealItems(HealInfo... healTypes) {
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

        public HealInfo(@NotNull ItemStack ammoItem, float givesAmount) {
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

        public AmmoInfo(@NotNull ItemStack ammoItem, @NotNull ItemStack typeItem, int givesAmount) {
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
