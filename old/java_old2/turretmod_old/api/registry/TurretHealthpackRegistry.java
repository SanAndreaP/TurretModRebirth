/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.registry;

import de.sanandrew.mods.turretmod.api.TurretHealthpack;
import de.sanandrew.mods.turretmod.api.TurretModApiProps;
import org.apache.logging.log4j.Level;

import java.util.*;

public final class TurretHealthpackRegistry
{
    private static final List<TurretHealthpack> HPACK_TYPES = new ArrayList<>();
    private static final Map<UUID, TurretHealthpack> UUID_TO_HPACK_TYPE_MAP = new HashMap<>();
    private static final Map<TurretHealthpack, UUID> HPACK_TYPE_TO_UUID_MAP = new HashMap<>();

    public static void registerHealthpackType(UUID uniqueId, TurretHealthpack type) {
        if( type == null ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register a NULL healthpack type!", new Throwable());
        } if( UUID_TO_HPACK_TYPE_MAP.containsKey(uniqueId) ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an already existing healthpack type UUID!", new Throwable());
        } else if( UUID_TO_HPACK_TYPE_MAP.containsValue(type) ) {
            TurretModApiProps.API_LOG.log(Level.ERROR, "Cannot register an already existing healthpack type!", new Throwable());
        } else {
            HPACK_TYPES.add(type);
            UUID_TO_HPACK_TYPE_MAP.put(uniqueId, type);
            HPACK_TYPE_TO_UUID_MAP.put(type, uniqueId);
        }
    }

    public static TurretHealthpack getType(UUID uniqueId) {
        return UUID_TO_HPACK_TYPE_MAP.get(uniqueId);
    }

    public static TurretHealthpack getType(String uniqueIdStr) {
        return getType(UUID.fromString(uniqueIdStr));
    }

    public static UUID getTypeId(TurretHealthpack type) {
        return HPACK_TYPE_TO_UUID_MAP.get(type);
    }

    public static ArrayList<TurretHealthpack> getTypes() {
        return new ArrayList<>(HPACK_TYPES);
    }
}
