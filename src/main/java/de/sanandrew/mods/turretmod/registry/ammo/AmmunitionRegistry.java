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
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AmmunitionRegistry
        implements IAmmunitionRegistry
{
    public static final AmmunitionRegistry INSTANCE = new AmmunitionRegistry();

    private final Map<ResourceLocation, IAmmunition> ammoTypes;
    private final Multimap<ITurret, IAmmunition> ammoTypesFromTurret;
    private final Multimap<ITurret, IAmmunitionGroup> ammoGroupsFromTurret;
    private final Multimap<ResourceLocation, IAmmunition> ammoTypesFromGroup;
    private final Map<ResourceLocation, IAmmunitionGroup> ammoGroups;

    private static final IAmmunition NULL_TYPE = new IAmmunition() {
        @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Nonnull @Override public IAmmunitionGroup getGroup() { return Ammunitions.Groups.UNKNOWN; }
        @Override public float getDamageInfo() { return 0; }
        @Override public int getAmmoCapacity() { return 0; }
        @Override public ITurretProjectile getProjectile(ITurretInst turretInst) { return null; }
        @Override public boolean isValid() { return false; }
    };

    private AmmunitionRegistry() {
        this.ammoTypes = new HashMap<>();
        this.ammoTypesFromTurret = ArrayListMultimap.create();
        this.ammoGroupsFromTurret = ArrayListMultimap.create();
        this.ammoTypesFromGroup = ArrayListMultimap.create();
        this.ammoGroups = new HashMap<>();
    }

    @Override
    public List<IAmmunition> getTypes() {
        return new ArrayList<>(this.ammoTypes.values());
    }

    @Override
    public List<IAmmunition> getTypes(IAmmunitionGroup group) {
        return new ArrayList<>(this.ammoTypesFromGroup.get(group.getId()));
    }

    @Override
    public IAmmunition getType(ResourceLocation typeId) {
        return this.ammoTypes.getOrDefault(typeId, NULL_TYPE);
    }

    @Override
    @Nonnull
    public IAmmunition getType(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemAmmo ) {
            return ((ItemAmmo) stack.getItem()).ammo;
        }

        return NULL_TYPE;
    }

    @Override
    public List<IAmmunition> getTypesForTurret(ITurret turret) {
        return new ArrayList<>(this.ammoTypesFromTurret.get(turret));
    }

    @Override
    public List<IAmmunitionGroup> getGroupsForTurret(ITurret turret) {
        return new ArrayList<>(this.ammoGroupsFromTurret.get(turret));
    }

    @Override
    public boolean register(IAmmunition type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Ammo-Type!", new InvalidParameterException());
            return false;
        }

        if( this.ammoTypes.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The ammo ID %s is already registered!", type.getId()), new InvalidParameterException());
            return false;
        }

        if( type.getAmmoCapacity() < 1 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo ID %s provides less than 1 round!", type.getId()), new InvalidParameterException());
            return false;
        }

        IAmmunitionGroup group = type.getGroup();
        if( group.getTurret() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo ID %s has no turret associated!", type.getId()), new InvalidParameterException());
            return false;
        }

        this.ammoTypes.put(type.getId(), type);
        this.ammoTypesFromTurret.put(group.getTurret(), type);
        if( !this.ammoGroupsFromTurret.containsEntry(group.getTurret(), group) ) {
            this.ammoGroupsFromTurret.put(group.getTurret(), group);
        }
        this.ammoGroups.putIfAbsent(group.getId(), group);
        this.ammoTypesFromGroup.put(group.getId(), type);

        ItemRegistry.TURRET_AMMO.put(type.getId(), new ItemAmmo(type));

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getAmmoItem(ResourceLocation id) {
        return this.getAmmoItem(this.ammoTypes.getOrDefault(id, NULL_TYPE));
    }

    @Override
    @Nonnull
    public ItemStack getAmmoItem(IAmmunition type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret ammo item with NULL type!");
        }

        return new ItemStack(ItemRegistry.TURRET_AMMO.get(type.getId()), 1);
    }

    @Override
    public List<IAmmunitionGroup> getGroups() {
        return new ArrayList<>(this.ammoGroups.values());
    }

    @Override
    @SuppressWarnings("ObjectEquality")
    public boolean areAmmoItemsEqual(@Nonnull ItemStack firstStack, @Nonnull ItemStack secondStack) {
        IAmmunition firstType = this.getType(firstStack);
        IAmmunition secondType = this.getType(secondStack);
        return firstType != NULL_TYPE && secondType != NULL_TYPE && firstType.getId().equals(secondType.getId());
    }
}
