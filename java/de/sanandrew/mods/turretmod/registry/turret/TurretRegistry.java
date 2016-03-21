/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TurretRegistry
{
    public static final TurretRegistry INSTANCE = new TurretRegistry();

    private final Map<UUID, TurretInfo> uuidTurretInfoMap;
    private final Map<Class<? extends EntityTurret>, TurretInfo> classTurretInfoMap;

    private TurretRegistry() {
        this.uuidTurretInfoMap = new HashMap<>();
        this.classTurretInfoMap = new HashMap<>();
    }

    public List<TurretInfo> getRegisteredInfos() {
        return new ArrayList<>(this.uuidTurretInfoMap.values());
    }

    public TurretInfo getInfo(UUID uuid) {
        return this.uuidTurretInfoMap.get(uuid);
    }

    public TurretInfo getInfo(Class<? extends EntityTurret> clazz) {
        return this.classTurretInfoMap.get(clazz);
    }
}
