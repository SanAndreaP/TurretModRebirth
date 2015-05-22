/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretOP;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.techii.EntityTurretRevolver;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TurretRegistry
{
    private static final Map<Class<? extends Turret>, TurretInfo<? extends Turret>> CLASS_MAP = new HashMap<>();
    private static final Map<String, TurretInfo<? extends Turret>> NAME_MAP = new HashMap<>();
    private static final List<String> REG_SORTED_TURRET_NAME_LIST = new ArrayList<>();

    public static <T extends Turret> TurretInfo<T> registerNewTurret(Class<T> turretCls, String turretName, String iconName) {
        TurretInfo<T> turret = new TurretInfo<>(turretCls, turretName, iconName);
        CLASS_MAP.put(turretCls, turret);
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

    public static TurretInfo<? extends Turret> getTurretInfo(Class<? extends Turret> clazz) {
        return CLASS_MAP.get(clazz);
    }

    public static TurretInfo<? extends Turret> getTurretInfo(String name) {
        return NAME_MAP.get(name);
    }

    public static List<String> getAllTurretNamesSorted() {
        return new ArrayList<>(REG_SORTED_TURRET_NAME_LIST);
    }

    public static class HealInfo {
        public final ItemStack item;
        public final float amount;

        public HealInfo(ItemStack ammoItem, float givesAmount) {
            this.item = ammoItem;
            this.amount = givesAmount;
        }
    }

    public static class AmmoInfo {
        public final ItemStack item;
        public final ItemStack type;
        public final int amount;

        public AmmoInfo(ItemStack ammoItem, ItemStack typeItem, int givesAmount) {
            this.item = ammoItem;
            this.type = typeItem;
            this.amount = givesAmount;
        }
    }
}
