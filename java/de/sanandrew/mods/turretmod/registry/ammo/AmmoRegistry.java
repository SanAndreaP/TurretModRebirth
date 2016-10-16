/*
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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AmmoRegistry
{
    public static final AmmoRegistry INSTANCE = new AmmoRegistry();

    private final Map<UUID, TurretAmmo> ammoTypesFromUUID;
    private final Multimap<Class<? extends EntityTurret>, TurretAmmo> ammoTypesFromTurret;
    private final Map<UUID, List<TurretAmmo>> ammoGroupsFromUUID;
    private final List<TurretAmmo> ammoTypes;

    public static final TurretAmmo NULL_TYPE = new TurretAmmo() {
        @Override
        public String getName() {
            return "";
        }

        @Override
        public UUID getId() {
            return UuidUtils.EMPTY_UUID;
        }

        @Override
        public UUID getTypeId() {
            return UuidUtils.EMPTY_UUID;
        }

        @Override
        public UUID getGroupId() {
            return UuidUtils.EMPTY_UUID;
        }

        @Override
        public String getInfoName() {
            return "";
        }

        @Override
        public float getInfoDamage() {
            return 0;
        }

        @Override
        public UUID getRecipeId() {
            return UuidUtils.EMPTY_UUID;
        }

        @Override
        public int getAmmoCapacity() {
            return 0;
        }

        @Override
        public Class<? extends IProjectile> getEntityClass() {
            return null;
        }

        @Override
        public IProjectile getEntity(EntityTurret turret) {
            return null;
        }

        @Override
        public Class<? extends EntityTurret> getTurret() {
            return null;
        }

        @Override
        public ResourceLocation getModel() {
            return null;
        }

        @Override
        public ItemStack getStoringAmmoItem() {
            return null;
        }
    };

    private AmmoRegistry() {
        this.ammoTypesFromUUID = new HashMap<>();
        this.ammoTypesFromTurret = ArrayListMultimap.create();
        this.ammoGroupsFromUUID = new HashMap<>();
        this.ammoTypes = new ArrayList<>();
    }

    public List<TurretAmmo> getRegisteredTypes() {
        return new ArrayList<>(this.ammoTypes);
    }

    public TurretAmmo[] getTypes(UUID groupId) {
        List<TurretAmmo> ammoList = MiscUtils.defIfNull(this.ammoGroupsFromUUID.get(groupId), new ArrayList<>(0));
        return ammoList.toArray(new TurretAmmo[ammoList.size()]);
    }

    public TurretAmmo getType(UUID typeId) {
        return MiscUtils.defIfNull(this.ammoTypesFromUUID.get(typeId), NULL_TYPE);
    }

    @Nonnull
    public TurretAmmo getType(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if( nbt != null ) {
            if( nbt.hasKey("ammoType") ) {
                String typeUUID = nbt.getString("ammoType");
                try {
                    return this.getType(UUID.fromString(typeUUID));
                } catch( IllegalArgumentException ex ) {
                    return NULL_TYPE;
                }
            }
        }

        return NULL_TYPE;
    }

    public List<TurretAmmo> getTypesForTurret(Class<? extends EntityTurret> turret) {
        return new ArrayList<>(this.ammoTypesFromTurret.get(turret));
    }

    public boolean registerAmmoType(TurretAmmo type) {
        if( type == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Cannot register NULL as Ammo-Type!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has an empty/NULL name! Cannot register the Void.", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getId() == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no UUID! How am I supposed to differentiate all the cartridges?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( this.ammoTypesFromUUID.containsKey(type.getId()) ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("The UUID of Ammo-Type %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getAmmoCapacity() < 1 ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s provides less than 1 round! At least give it SOMETHING...", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getEntityClass() == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no projectile entity! Turrets can't shoot emptiness, can they!?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getTurret() == null ) {
            TurretModRebirth.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no turret! Ammo is pretty useless without something to shoot it with.", type.getName()), new InvalidParameterException());
            return false;
        }

        this.ammoTypesFromUUID.put(type.getId(), type);
        this.ammoTypesFromTurret.put(type.getTurret(), type);
        this.ammoTypes.add(type);

        List<TurretAmmo> groupList = this.ammoGroupsFromUUID.get(type.getGroupId());
        if( groupList == null ) {
            this.ammoGroupsFromUUID.put(type.getGroupId(), groupList = new ArrayList<>());
        }
        groupList.add(type);

        return true;
    }

    public boolean areAmmoItemsEqual(ItemStack firstStack, ItemStack secondStack) {
        if(firstStack != null && secondStack != null && firstStack.getItem() == ItemRegistry.ammo && secondStack.getItem() == ItemRegistry.ammo) {
            TurretAmmo firstType = this.getType(firstStack);
            TurretAmmo secondType = this.getType(secondStack);
            return firstType != NULL_TYPE && secondType != NULL_TYPE && firstType.getTypeId().equals(secondType.getTypeId());
        } else {
            return firstStack == secondStack;
        }
    }

    public void initialize() {
        this.registerAmmoType(new TurretAmmoArrow.Single());
        this.registerAmmoType(new TurretAmmoArrow.Quiver());
        this.registerAmmoType(new TurretAmmoShotgunShell.Single());
        this.registerAmmoType(new TurretAmmoShotgunShell.Multi());
        this.registerAmmoType(new TurretAmmoBullet.Single());
        this.registerAmmoType(new TurretAmmoBullet.Multi());
        this.registerAmmoType(new TurretAmmoCryoCell.SingleMK1());
        this.registerAmmoType(new TurretAmmoCryoCell.MultiMK1());
        this.registerAmmoType(new TurretAmmoCryoCell.SingleMK2());
        this.registerAmmoType(new TurretAmmoCryoCell.MultiMK2());
        this.registerAmmoType(new TurretAmmoCryoCell.SingleMK3());
        this.registerAmmoType(new TurretAmmoCryoCell.MultiMK3());
        this.registerAmmoType(new TurretAmmoMinigunShell.Single());
        this.registerAmmoType(new TurretAmmoMinigunShell.Multi());
        this.registerAmmoType(new TurretAmmoFluxCell.Single());
        this.registerAmmoType(new TurretAmmoFluxCell.Multi());
    }
}
