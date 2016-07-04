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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import org.apache.logging.log4j.Level;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TurretRegistry
{
    public static final TurretRegistry INSTANCE = new TurretRegistry();

    private final Map<UUID, TurretInfo> infoFromUUID;
    private final Map<Class<? extends EntityTurret>, TurretInfo> infoFromClass;
    private final List<TurretInfo> infos;

    private TurretRegistry() {
        this.infoFromUUID = new HashMap<>();
        this.infoFromClass = new HashMap<>();
        this.infos = new ArrayList<>();
    }

    public List<TurretInfo> getRegisteredInfos() {
        return new ArrayList<>(this.infos);
    }

    public TurretInfo getInfo(UUID uuid) {
        return this.infoFromUUID.get(uuid);
    }

    public TurretInfo getInfo(Class<? extends EntityTurret> clazz) {
        return this.infoFromClass.get(clazz);
    }

    public boolean registerTurretInfo(TurretInfo type) {
        if( type == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot register NULL as Turret-Info!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Turret-Info %s has an empty/NULL name! Cannot register the Void.", type.getClass().getName()), new InvalidParameterException());
            return false;
        }

        if( type.getUUID() == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Turret-Info %s has no UUID! How am I supposed to differentiate all the turrets?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( this.infoFromUUID.containsKey(type.getUUID()) ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("The UUID of Turret-Info %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getTurretClass() == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Turret-Info %s has no turret! wat?", type.getName()), new InvalidParameterException());
            return false;
        }

        this.infoFromUUID.put(type.getUUID(), type);
        this.infoFromClass.put(type.getTurretClass(), type);
        this.infos.add(type);

        return true;
    }

    public void initialize() {
        this.registerTurretInfo(EntityTurretCrossbow.TINFO);
        this.registerTurretInfo(EntityTurretShotgun.TINFO);
        this.registerTurretInfo(EntityTurretCryolator.TINFO);
        this.registerTurretInfo(EntityTurretRevolver.TINFO);
        this.registerTurretInfo(EntityTurretMinigun.TINFO);
    }
}
