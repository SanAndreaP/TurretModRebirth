/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import com.google.common.eventbus.EventBus;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgrade;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurretUpgradeRegistry
{
    private static final Map<String, TurretUpgrade> NAME_TO_UPGRADE_MAP = new HashMap<>();
    public static final EventBus EVENT_BUS = new EventBus("TurretUpgradesEvtBus");

    public static final TurretUpgrade HEALTH_INCR_I = new TUpgradeHealth("healthUpgradeI", "upgrades/health_i");
    public static final TurretUpgrade COOLDOWN_TIME_DECR = new TurretUpgrade(TurretMod.MOD_ID, "coolTimeUpgradeI", "upgrades/cooltime_i");

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

    public static List<TurretUpgrade> getRegisteredUpgrades() {
        return new ArrayList<>(NAME_TO_UPGRADE_MAP.values());
    }
}
