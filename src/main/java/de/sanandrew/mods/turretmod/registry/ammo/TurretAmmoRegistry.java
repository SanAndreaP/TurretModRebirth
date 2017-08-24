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
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmo;
import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TurretAmmoRegistry
        implements ITurretAmmoRegistry
{
    public static final TurretAmmoRegistry INSTANCE = new TurretAmmoRegistry();

    private final Map<UUID, ITurretAmmo> ammoTypesFromUUID;
    private final Multimap<Class<? extends EntityTurret>, ITurretAmmo> ammoTypesFromTurret;
    private final Map<UUID, List<ITurretAmmo>> ammoGroupsFromUUID;
    private final List<ITurretAmmo> ammoTypes;

    public static final ITurretAmmo NULL_TYPE = new ITurretAmmo<EntityArrow>() {
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
        public Class<EntityArrow> getEntityClass() {
            return null;
        }

        @Override
        public EntityArrow getEntity(EntityTurret turret) {
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
        @Nonnull
        public ItemStack getStoringAmmoItem() {
            return ItemStack.EMPTY;
        }
    };

    private TurretAmmoRegistry() {
        this.ammoTypesFromUUID = new HashMap<>();
        this.ammoTypesFromTurret = ArrayListMultimap.create();
        this.ammoGroupsFromUUID = new HashMap<>();
        this.ammoTypes = new ArrayList<>();
    }

    @Override
    public List<ITurretAmmo> getRegisteredTypes() {
        return new ArrayList<>(this.ammoTypes);
    }

    @Override
    public List<UUID> getRegisteredGroups() {
        return new ArrayList<>(this.ammoGroupsFromUUID.keySet());
    }

    @Override
    public ITurretAmmo[] getTypes(UUID groupId) {
        List<ITurretAmmo> ammoList = MiscUtils.defIfNull(this.ammoGroupsFromUUID.get(groupId), new ArrayList<>(0));
        return ammoList.toArray(new ITurretAmmo[ammoList.size()]);
    }

    @Override
    public ITurretAmmo getType(UUID typeId) {
        return MiscUtils.defIfNull(this.ammoTypesFromUUID.get(typeId), NULL_TYPE);
    }

    @Override
    @Nonnull
    public ITurretAmmo getType(@Nonnull ItemStack stack) {
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

    @Override
    public List<ITurretAmmo> getTypesForTurret(Class<? extends EntityTurret> turret) {
        return new ArrayList<>(this.ammoTypesFromTurret.get(turret));
    }

    @Override
    @SuppressWarnings("unused")
    public boolean registerAmmoType(ITurretAmmo<?> type) {
        return registerAmmoType(type, false);
    }

    boolean registerAmmoType(ITurretAmmo<?> type, boolean registerEntity) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Ammo-Type!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo-Type %s has an empty/NULL name! Cannot register the Void.", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getId() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no UUID! How am I supposed to differentiate all the cartridges?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( this.ammoTypesFromUUID.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of Ammo-Type %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getAmmoCapacity() < 1 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo-Type %s provides less than 1 round! At least give it SOMETHING...", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getEntityClass() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no projectile entity! Turrets can't shoot emptiness, can they!?", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getTurret() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo-Type %s has no turret_placer! Ammo is pretty useless without something to shoot it with.", type.getName()), new InvalidParameterException());
            return false;
        }

        this.ammoTypesFromUUID.put(type.getId(), type);
        this.ammoTypesFromTurret.put(type.getTurret(), type);
        this.ammoTypes.add(type);

        if( registerEntity ) {
            String name = "turret_proj_".concat(type.getName());
            EntityRegistry.registerModEntity(new ResourceLocation(TmrConstants.ID, name), type.getEntityClass(), TmrConstants.ID + '.' + name, CommonProxy.entityCount++, TurretModRebirth.instance, 128, 1, true);
        }

        List<ITurretAmmo> groupList = this.ammoGroupsFromUUID.computeIfAbsent(type.getGroupId(), k -> new ArrayList<>());
        groupList.add(type);

        return true;
    }

    @Override
    public boolean areAmmoItemsEqual(@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack) {
        if( firstStack.getItem() == ItemRegistry.turret_ammo && secondStack.getItem() == ItemRegistry.turret_ammo ) {
            ITurretAmmo firstType = this.getType(firstStack);
            ITurretAmmo secondType = this.getType(secondStack);
            return firstType != NULL_TYPE && secondType != NULL_TYPE && firstType.getTypeId().equals(secondType.getTypeId());
        } else {
            return firstStack == secondStack;
        }
    }
}
