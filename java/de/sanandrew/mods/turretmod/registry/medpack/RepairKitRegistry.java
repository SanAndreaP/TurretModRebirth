package de.sanandrew.mods.turretmod.registry.medpack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public class RepairKitRegistry
{
    public static final RepairKitRegistry INSTANCE = new RepairKitRegistry();

    private final Map<UUID, TurretRepairKit> kitsFromUUID;

    private RepairKitRegistry() {
        this.kitsFromUUID = new HashMap<>();
    }

    public boolean registerMedpack(Class<? extends TurretRepairKit> typeCls) {
        if( typeCls == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot register NULL as Repair Kit!", new InvalidParameterException());
            return false;
        }

        try {
            TurretRepairKit type = typeCls.getConstructor().newInstance();

            if( type.getName() == null || type.getName().isEmpty() ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Repair Kit %s has an empty/NULL name! Cannot register the Void.", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( type.getUUID() == null ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Repair Kit %s has no UUID! How am I supposed to differentiate all the screws?", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( this.kitsFromUUID.containsKey(type.getUUID()) ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("The UUID of the Repair Kit %s is already registered! Use another UUID. JUST DO IT!", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            this.kitsFromUUID.put(type.getUUID(), type);

            return true;
        } catch( NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Cannot instanciate Repair Kit %s! It should have a public standard constructor with no parameters.", typeCls.getName()), e);
            return false;
        }
    }

    public List<TurretRepairKit> getRegisteredTypes() {
        return new ArrayList<>(this.kitsFromUUID.values());
    }

    public TurretRepairKit getRepairKit(UUID uuid) {
        return this.kitsFromUUID.get(uuid);
    }

    public void initialize() {

    }
}
