/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.api.registry;

import com.google.common.eventbus.EventBus;
import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretModApiProps;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurretUpgradeRegistry
{
    private static final Map<String, TurretUpgrade> NAME_TO_UPGRADE_MAP = new HashMap<>();
    private static final List<TurretUpgrade> REG_SORTED_UPGRADE_LIST = new ArrayList<>();
    public static final EventBus EVENT_BUS = new EventBus("TurretUpgradesEvtBus");

    /**
     * Registers a new Upgrade instance. See {@link TurretUpgrade} for more information.
     * Note: You cannot register null, an upgrade with no or already existing name
     * @param upgrade An upgrade instance to be registered
     */
    public static void registerUpgrade(TurretUpgrade upgrade) {
        if( upgrade == null ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register a NULL upgrade!", new Throwable());
        } else {
            String regName = getRegistrationName(upgrade);
            if( upgrade.getName() == null || upgrade.getName().isEmpty() || upgrade.getModId() == null || upgrade.getModId().isEmpty() ) {
                TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an upgrade with no name!", new Throwable());
            } else if( NAME_TO_UPGRADE_MAP.containsKey(regName) ) {
                TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an already existing upgrade name!", new Throwable());
            } else if( NAME_TO_UPGRADE_MAP.containsValue(upgrade) ) {
                TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an already existing upgrade!", new Throwable());
            } else {
                NAME_TO_UPGRADE_MAP.put(regName, upgrade);
                REG_SORTED_UPGRADE_LIST.add(upgrade);
            }
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

    public static boolean isApplicableToCls(TurretUpgrade upgrade, Class<? extends Turret> turretCls) {
        List<Class<? extends Turret>> applicable = upgrade.getApplicableTurrets();
        return applicable.size() == 0 || applicable.contains(turretCls);
    }

    public static String getUnlocName(TurretUpgrade upgrade) {
        return String.format("turretUpg.%s.%s.name", upgrade.getModId(), upgrade.getName());
    }

    public static String getRegistrationName(TurretUpgrade upgrade) {
        return String.format("%s:%s", upgrade.getModId(), upgrade.getName());
    }

    public static String getItemTextureLoc(TurretUpgrade upgrade) {
        return String.format("%s:%s", upgrade.getModId(), upgrade.getIconTexture());
    }
}
