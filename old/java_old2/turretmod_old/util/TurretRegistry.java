/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretAmmo;
import de.sanandrew.mods.turretmod.api.TurretInfo;
import de.sanandrew.mods.turretmod.api.registry.TurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.registry.TurretHealthpackRegistry;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretOP;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.techii.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.item.ItemTurretAmmo;
import de.sanandrew.mods.turretmod.client.util.ammo.AmmoArrow;
import de.sanandrew.mods.turretmod.client.util.ammo.AmmoBullet;
import de.sanandrew.mods.turretmod.client.util.healitems.HealthpackCobble;

import java.util.*;

public final class TurretRegistry
{
    private static final Map<Class<? extends Turret>, TurretInfo<? extends Turret>> CLASS_MAP = new HashMap<>();
    private static final Map<String, TurretInfo<? extends Turret>> NAME_MAP = new HashMap<>();
    private static final List<String> REG_SORTED_TURRET_NAME_LIST = new ArrayList<>();

    public static <T extends Turret> TurretInfo<T> registerNewTurret(Class<T> turretCls, String turretName, String iconName) {
        TurretInfo<T> turret = new TurretInfoApi<>(turretCls, turretName, iconName);
        CLASS_MAP.put(turretCls, turret);
        NAME_MAP.put(turretName, turret);
        REG_SORTED_TURRET_NAME_LIST.add(turretName);

        return turret;
    }

    public static void initialize() {
        TurretAmmo currType;

        registerNewTurret(EntityTurretCrossbow.class, "turretCrossbow", TurretMod.MOD_ID + ":turret_crossbow");
        registerNewTurret(EntityTurretRevolver.class, "turretRevolver", TurretMod.MOD_ID + ":turret_revolver");
        registerNewTurret(EntityTurretOP.class, "turretOP", TurretMod.MOD_ID + ":turret_op");

        registerAmmo(new AmmoArrow(), UUID.fromString("90A6A271-A4CC-4576-91BD-1EEF9D3C09A4"), "arrow", "ammo/arrow");
        registerAmmo(new AmmoBullet(), UUID.fromString("1F47C905-F5D6-4491-B1CA-04D35ED1A07F"), "bullet", "ammo/bullet");

        TurretHealthpackRegistry.registerHealthpackType(UUID.fromString("404F1CD5-A0B3-458B-A02F-41185BC6FADE"), new HealthpackCobble());
//        TurretHealthpack.HEAL_TYPES.add(new HealthpackCobble());
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

    private static void registerAmmo(TurretAmmo instance, UUID id, String name, String icon, String... desc) {
        TurretAmmoRegistry.registerAmmoType(id, instance);
        ItemTurretAmmo.registerAmmoItem(instance, name, TurretMod.MOD_ID + ':' + icon);
        instance.initializeItem();
    }
}
