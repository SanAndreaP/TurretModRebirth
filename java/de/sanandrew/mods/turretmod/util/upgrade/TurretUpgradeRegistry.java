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
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.*;

public class TurretUpgradeRegistry
{
    private static final Map<String, TurretUpgrade> NAME_TO_UPGRADE_MAP = new HashMap<>();
    public static final EventBus EVENT_BUS = new EventBus("TurretUpgradesEvtBus");

    public static final TurretUpgrade HEALTH_INCR_I   = new TUpgradeHealth("healthUpgradeI", "upgrades/health_i",
                                                                           UUID.fromString("84bf0c8f-a5e8-429f-a7ed-dee503ca4505"), 1);
    public static final TurretUpgrade HEALTH_INCR_II  = new TUpgradeHealth("healthUpgradeII", "upgrades/health_ii",
                                                                           UUID.fromString("704fa08b-f49a-4a69-86e9-ffad639868c9"), 2, HEALTH_INCR_I);
    public static final TurretUpgrade HEALTH_INCR_III = new TUpgradeHealth("healthUpgradeIII", "upgrades/health_iii",
                                                                           UUID.fromString("e1c7bbb8-ace8-413d-b7ea-d95c7ff5285f"), 3, HEALTH_INCR_II);
    public static final TurretUpgrade HEALTH_INCR_IV  = new TUpgradeHealth("healthUpgradeIV", "upgrades/health_iv",
                                                                           UUID.fromString("e4d37b7f-81dd-439e-b359-539e76f01b71"), 4, HEALTH_INCR_III);
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
