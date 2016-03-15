/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import scala.collection.mutable.MultiMap;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AmmoRegistry
{
    public static final AmmoRegistry INSTANCE = new AmmoRegistry();

    private final Map<UUID, TurretAmmo> ammoTypesFromUUID;
    private final Multimap<Class<? extends EntityTurret>, TurretAmmo> ammoTypesFromTurret;

    private AmmoRegistry() {
        this.ammoTypesFromUUID = new HashMap<>();
        this.ammoTypesFromTurret = ArrayListMultimap.create();
    }

    public List<TurretAmmo> getRegisteredTypes() {
        return new ArrayList<>(this.ammoTypesFromUUID.values());
    }

    public TurretAmmo getType(UUID typeId) {
        return this.ammoTypesFromUUID.get(typeId);
    }

    public List<TurretAmmo> getTypesForTurret(Class<? extends EntityTurret> turret) {
        return new ArrayList<>(this.ammoTypesFromTurret.get(turret));
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

            if( this.ammoTypesFromUUID.containsKey(type.getUUID()) ) {
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

            this.ammoTypesFromUUID.put(type.getUUID(), type);
            this.ammoTypesFromTurret.put(type.getTurret(), type);

            return true;
        } catch( NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Cannot instanciate Ammo-Type %s! It should have a public standard constructor with no parameters.", typeCls.getName()), e);
            return false;
        }
    }

    public boolean areAmmoItemsEqual(ItemStack firstStack, ItemStack secondStack) {
        if(firstStack != null && secondStack != null && firstStack.getItem() == ItemRegistry.ammo && secondStack.getItem() == ItemRegistry.ammo) {
            TurretAmmo firstType = ItemRegistry.ammo.getAmmoType(firstStack);
            TurretAmmo secondType = ItemRegistry.ammo.getAmmoType(secondStack);
            return firstType != null && secondType != null && firstType.getTypeUUID().equals(secondType.getTypeUUID());
//            Item firstItem = firstStack.getItem();
//            Item secondItem = secondStack.getItem();
//            return firstItem != null && secondItem != null
//                    ? (firstItem == secondItem
//                    && (comparator.compare(firstStack.getTagCompound(), secondStack.getTagCompound()) == 0
//                    && (firstStack.getItemDamage() == OreDictionary.WILDCARD_VALUE
//                    || secondStack.getItemDamage() == OreDictionary.WILDCARD_VALUE
//                    || firstStack.getItemDamage() == secondStack.getItemDamage())
//            )
//            )
//                    : firstItem == secondItem;
        } else {
            return firstStack == secondStack;
        }
    }

    public void initialize() {
        this.registerAmmoType(TurretAmmoArrow.Single.class);
        this.registerAmmoType(TurretAmmoArrow.Quiver.class);
    }
}
