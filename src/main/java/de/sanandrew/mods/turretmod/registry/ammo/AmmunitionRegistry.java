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
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
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

public final class AmmunitionRegistry
        implements IAmmunitionRegistry
{
    public static final AmmunitionRegistry INSTANCE = new AmmunitionRegistry();

    private final Map<UUID, IAmmunition> ammoTypesFromUUID;
    private final Multimap<ITurret, IAmmunition> ammoTypesFromTurret;
    private final Map<UUID, List<IAmmunition>> ammoGroupsFromUUID;
    private final List<IAmmunition> ammoTypes;

    public static final IAmmunition NULL_TYPE = new IAmmunition<EntityArrow>() {
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
        public EntityArrow getEntity(ITurretInst turretInst) {
            return null;
        }

        @Override
        public ITurret getTurret() {
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

    private AmmunitionRegistry() {
        this.ammoTypesFromUUID = new HashMap<>();
        this.ammoTypesFromTurret = ArrayListMultimap.create();
        this.ammoGroupsFromUUID = new HashMap<>();
        this.ammoTypes = new ArrayList<>();
    }

    @Override
    public List<IAmmunition> getRegisteredTypes() {
        return new ArrayList<>(this.ammoTypes);
    }

    @Override
    public List<UUID> getGroups() {
        return new ArrayList<>(this.ammoGroupsFromUUID.keySet());
    }

    @Override
    public IAmmunition[] getTypes(UUID groupId) {
        List<IAmmunition> ammoList = MiscUtils.defIfNull(this.ammoGroupsFromUUID.get(groupId), new ArrayList<>(0));
        return ammoList.toArray(new IAmmunition[ammoList.size()]);
    }

    @Override
    public IAmmunition getType(UUID typeId) {
        return MiscUtils.defIfNull(this.ammoTypesFromUUID.get(typeId), NULL_TYPE);
    }

    @Override
    @Nonnull
    public IAmmunition getType(@Nonnull ItemStack stack) {
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
    public List<IAmmunition> getTypesForTurret(ITurret turret) {
        return new ArrayList<>(this.ammoTypesFromTurret.get(turret));
    }

    @Override
    @SuppressWarnings("unused")
    public boolean registerAmmoType(IAmmunition<?> type) {
        return registerAmmoType(type, false);
    }

    boolean registerAmmoType(IAmmunition<?> type, boolean registerEntity) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Ammo-Type!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo-Type %s has an empty/NULL name! Cannot register the Void.", type.getName()), new InvalidParameterException());
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

        List<IAmmunition> groupList = this.ammoGroupsFromUUID.computeIfAbsent(type.getGroupId(), k -> new ArrayList<>());
        groupList.add(type);

        return true;
    }

    @Override
    public boolean areAmmoItemsEqual(@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack) {
        if( firstStack.getItem() == ItemRegistry.turret_ammo && secondStack.getItem() == ItemRegistry.turret_ammo ) {
            IAmmunition firstType = this.getType(firstStack);
            IAmmunition secondType = this.getType(secondStack);
            return firstType != NULL_TYPE && secondType != NULL_TYPE && firstType.getTypeId().equals(secondType.getTypeId());
        } else {
            return firstStack == secondStack;
        }
    }
}
