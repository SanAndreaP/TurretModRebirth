/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCryolator;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretFlamethrower;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretLaser;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretMinigun;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretShotgun;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraftforge.fml.common.registry.EntityRegistry;
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
        return this.registerTurretInfo(type, false);
    }

    private boolean registerTurretInfo(TurretInfo type, boolean registerEntity) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Turret-Info!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Turret-Info %s has an empty/NULL name! Cannot register the Void.", type.getClass().getName()), new InvalidParameterException());
            return false;
        }

        if( type.getUUID() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Turret-Info %s has no UUID! How am I supposed to differentiate all the turrets?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( this.infoFromUUID.containsKey(type.getUUID()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of Turret-Info %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getTurretClass() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Turret-Info %s has no turret_placer! wat?", type.getName()), new InvalidParameterException());
            return false;
        }

        this.infoFromUUID.put(type.getUUID(), type);
        this.infoFromClass.put(type.getTurretClass(), type);
        this.infos.add(type);

        if( registerEntity ) {
            EntityRegistry.registerModEntity(type.getTurretClass(), type.getName(), CommonProxy.entityCount++, TurretModRebirth.instance, 128, 1, false);
        }

        return true;
    }

    public void initialize() {
        this.registerTurretInfo(EntityTurretCrossbow.TINFO, true);
        this.registerTurretInfo(EntityTurretShotgun.TINFO, true);
        this.registerTurretInfo(EntityTurretCryolator.TINFO, true);
        this.registerTurretInfo(EntityTurretRevolver.TINFO, true);
        this.registerTurretInfo(EntityTurretMinigun.TINFO, true);
        this.registerTurretInfo(EntityTurretLaser.TINFO, true);
        this.registerTurretInfo(EntityTurretFlamethrower.TINFO, true);
    }
}
