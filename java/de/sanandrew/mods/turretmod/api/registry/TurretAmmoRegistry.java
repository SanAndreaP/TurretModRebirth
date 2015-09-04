/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.registry;

import de.sanandrew.mods.turretmod.api.TurretAmmo;
import de.sanandrew.mods.turretmod.api.TurretModApiProps;
import org.apache.logging.log4j.Level;

import java.util.*;

public final class TurretAmmoRegistry
{
    private static final List<TurretAmmo> AMMO_TYPES = new ArrayList<>();
    private static final Map<UUID, TurretAmmo> UUID_TO_AMMO_TYPE_MAP = new HashMap<>();
    private static final Map<TurretAmmo, UUID> AMMO_TYPE_TO_UUID_MAP = new HashMap<>();

    public static void registerAmmoType(UUID uniqueId, TurretAmmo ammoType) {
        if( ammoType == null ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register a NULL ammo type!", new Throwable());
        } if( UUID_TO_AMMO_TYPE_MAP.containsKey(uniqueId) ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an already existing ammo type UUID!", new Throwable());
        } else if( UUID_TO_AMMO_TYPE_MAP.containsValue(ammoType) ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an already existing ammo type!", new Throwable());
        } else {
            AMMO_TYPES.add(ammoType);
            UUID_TO_AMMO_TYPE_MAP.put(uniqueId, ammoType);
            AMMO_TYPE_TO_UUID_MAP.put(ammoType, uniqueId);
        }
    }

    public static TurretAmmo getType(UUID uniqueId) {
        return UUID_TO_AMMO_TYPE_MAP.get(uniqueId);
    }

    public static TurretAmmo getType(String uniqueIdStr) {
        return getType(UUID.fromString(uniqueIdStr));
    }

    public static UUID getTypeId(TurretAmmo ammoType) {
        return AMMO_TYPE_TO_UUID_MAP.get(ammoType);
    }

    public static ArrayList<TurretAmmo> getTypes() {
        return new ArrayList<>(AMMO_TYPES);
    }
}
