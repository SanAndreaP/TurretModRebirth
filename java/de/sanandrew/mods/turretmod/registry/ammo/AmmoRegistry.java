/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AmmoRegistry
{
    public static final AmmoRegistry INSTANCE = new AmmoRegistry();

    private Map<UUID, TurretAmmo> ammoTypes;

    private AmmoRegistry() {
        this.ammoTypes = new HashMap<>();
    }

    public List<TurretAmmo> getRegisteredTypes() {
        return new ArrayList<>(this.ammoTypes.values());
    }

    public TurretAmmo getType(UUID typeId) {
        return this.ammoTypes.get(typeId);
    }

    public boolean registerAmmoType(Class<? extends TurretAmmo> typeCls) {
        if( typeCls == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot register NULL as Ammo-Type!", new InvalidParameterException());
            return false;
        }

        try {
            TurretAmmo type = typeCls.getConstructor().newInstance();

            if( type.getName() == null || type.getName().isEmpty() ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has an empty/NULL name! Cannot register the Void.", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( type.getUUID() == null ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no UUID! How am I supposed to differentiate all the cartridges?", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( this.ammoTypes.containsKey(type.getUUID()) ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("The UUID of Ammo-Type %s is already registered! Use another UUID. JUST DO IT!", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( type.getAmmoCapacity() < 1 ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s provides less than 1 round! At least give it SOMETHING...", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( type.getEntity() == null ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no projectile entity! Turrets can't shoot emptiness, can they!?", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            if( type.getTurret() == null ) {
                TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no turret! Ammo is pretty useless without something to shoot it with.", typeCls.getName()), new InvalidParameterException());
                return false;
            }

            this.ammoTypes.put(type.getUUID(), type);

            return true;
        } catch( NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Cannot instanciate Ammo-Type %s! It should have a public standard constructor with no parameters.", typeCls.getName()), e);
            return false;
        }
    }

    public void initialize() {
        this.registerAmmoType(TurretAmmoArrow.Single.class);
        this.registerAmmoType(TurretAmmoArrow.Quiver.class);
    }
}
