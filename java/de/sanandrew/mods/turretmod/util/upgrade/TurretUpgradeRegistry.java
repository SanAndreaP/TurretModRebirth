/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import com.google.common.eventbus.EventBus;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeReloadTime.TUpgradeReloadTimeI;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeReloadTime.TUpgradeReloadTimeII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeUpgStorage.TUpgradeUpgStorageI;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeUpgStorage.TUpgradeUpgStorageII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeUpgStorage.TUpgradeUpgStorageIII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthI;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthIII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthIV;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.*;

public class TurretUpgradeRegistry
{
    private static final Map<String, TurretUpgrade> NAME_TO_UPGRADE_MAP = new HashMap<>();
    private static final List<TurretUpgrade> REG_SORTED_UPGRADE_LIST = new ArrayList<>();
    public static final EventBus EVENT_BUS = new EventBus("TurretUpgradesEvtBus");

    public static final TurretUpgrade HEALTH_INCR_I   = new TUpgradeHealthI();
    public static final TurretUpgrade HEALTH_INCR_II  = new TUpgradeHealthII(HEALTH_INCR_I);
    public static final TurretUpgrade HEALTH_INCR_III = new TUpgradeHealthIII(HEALTH_INCR_II);
    public static final TurretUpgrade HEALTH_INCR_IV  = new TUpgradeHealthIV(HEALTH_INCR_III);
    public static final TurretUpgrade RELOAD_TIME_I = new TUpgradeReloadTimeI();
    public static final TurretUpgrade RELOAD_TIME_II = new TUpgradeReloadTimeII(RELOAD_TIME_I);
    public static final TurretUpgrade UPG_STORAGE_I = new TUpgradeUpgStorageI();
    public static final TurretUpgrade UPG_STORAGE_II = new TUpgradeUpgStorageII(UPG_STORAGE_I);
    public static final TurretUpgrade UPG_STORAGE_III = new TUpgradeUpgStorageIII(UPG_STORAGE_II);
    public static final TurretUpgrade AMMO_STORAGE = new TUpgradeAmmoStorage();

    static {
        try {
            Field[] upgFields = TurretUpgradeRegistry.class.getFields();
            for( Field fld : upgFields ) {
                if( fld.getType().equals(TurretUpgrade.class) ) {
                    registerUpgrade((TurretUpgrade) fld.get(null));
                }
            }
        } catch( IllegalAccessException | ClassCastException ex ) {
            throw new RuntimeException("An unexpected critical error occurred during initializing the Turret Upgrades!", ex);
        }

        HEALTH_INCR_IV.addTurretApplicable(EntityTurretCrossbow.class);
    }

    /**
     * Registers a new Upgrade instance. See {@link TurretUpgrade#TurretUpgrade(String, String, String, TurretUpgrade)} for more information.
     * Note: You cannot register null, an upgrade with no or already existing name
     * @param upgrade An upgrade instance to be registered
     */
    public static void registerUpgrade(TurretUpgrade upgrade) {
        if( upgrade == null ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Cannot register a NULL upgrade!", new Throwable());
        } else if( upgrade.name == null || upgrade.name.isEmpty() || upgrade.modId == null || upgrade.modId.isEmpty() ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Cannot register an upgrade with no name!", new Throwable());
        } else if( NAME_TO_UPGRADE_MAP.containsKey(upgrade.getRegistrationName()) ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Cannot register an already existing upgrade name!", new Throwable());
        } else if( NAME_TO_UPGRADE_MAP.containsValue(upgrade) ) {
            TurretMod.MOD_LOG.log(Level.ERROR, "Cannot register an already existing upgrade!", new Throwable());
        } else {
            NAME_TO_UPGRADE_MAP.put(upgrade.getRegistrationName(), upgrade);
            REG_SORTED_UPGRADE_LIST.add(upgrade);
        }
    }

    /**
     * Gets the registered upgrade from its registration name (mod-ID + ':' + upgrade-name).
     * If it can't find the name for whatever reason, it returns null.
     * @param registrationName The registration name of the upgrade to look up
     * @return The upgrade if it could be found under that name, null otherwise
     */
    public static TurretUpgrade getUpgrade(String registrationName) {
        return NAME_TO_UPGRADE_MAP.get(registrationName);
    }

    public static List<TurretUpgrade> getAllUpgradesSorted() {
        return new ArrayList<>(REG_SORTED_UPGRADE_LIST);
    }
}