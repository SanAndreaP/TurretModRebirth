/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

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
import java.util.*;

public final class AmmunitionRegistry
        implements IAmmunitionRegistry
{
    public static final AmmunitionRegistry INSTANCE = new AmmunitionRegistry();

    private final Map<ResourceLocation, IAmmunition> ammoTypes;
    private final Map<ITurret, UModList<IAmmunition>> ammoTypesFromTurret;

    private final Collection<IAmmunition> uAmmoTypes;

    /** used for the lexicon only! **/
    private final Map<ITurret, UModSet<IAmmunitionGroup>> ammoGroupsFromTurret;
    private final Map<ResourceLocation, UModList<IAmmunition>> ammoTypesFromGroup;
    private final Map<ResourceLocation, IAmmunitionGroup> ammoGroups;
    private boolean finalizedForLexicon = false;

    public static final IAmmunition NULL_TYPE = new IAmmunition() {
        @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Nonnull @Override public IAmmunitionGroup getGroup() { return Ammunitions.Groups.UNKNOWN; }
        @Override public float getDamageInfo() { return 0; }
        @Override public int getAmmoCapacity() { return 0; }
        @Override public ITurretProjectile getProjectile(ITurretInst turretInst) { return null; }
        @Override public boolean isValid() { return false; }
    };

    private AmmunitionRegistry() {
        this.ammoTypes = new HashMap<>();
        this.ammoTypesFromTurret = new HashMap<>();
        this.ammoGroupsFromTurret = new HashMap<>();
        this.ammoTypesFromGroup = new HashMap<>();
        this.ammoGroups = new HashMap<>();

        this.uAmmoTypes = Collections.unmodifiableCollection(this.ammoTypes.values());
    }

    @Override
    @Nonnull
    public Collection<IAmmunition> getTypes() {
        return this.uAmmoTypes;
    }

    @Override
    @Nonnull
    public IAmmunition getType(ResourceLocation id) {
        return this.ammoTypes.getOrDefault(id, NULL_TYPE);
    }

    @Override
    @Nonnull
    public IAmmunition getType(@Nonnull ItemStack item) {
        if( ItemStackUtils.isValid(item) && item.getItem() instanceof ItemAmmo ) {
            return ((ItemAmmo) item.getItem()).ammo;
        }

        return NULL_TYPE;
    }

    @Override
    @Nonnull
    public Collection<IAmmunition> getTypes(ITurret turret) {
        return this.ammoTypesFromTurret.get(turret).umList;
    }

    @Override
    public void register(IAmmunition type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Ammo-Type!", new InvalidParameterException());
            return;
        }

        if( this.ammoTypes.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The ammo ID %s is already registered!", type.getId()), new InvalidParameterException());
            return;
        }

        if( type.getAmmoCapacity() < 1 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo ID %s provides less than 1 round!", type.getId()), new InvalidParameterException());
            return;
        }

        IAmmunitionGroup group = type.getGroup();
        if( group.getTurret() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo ID %s has no turret associated!", type.getId()), new InvalidParameterException());
            return;
        }

        this.ammoTypes.put(type.getId(), type);
        this.ammoTypesFromTurret.computeIfAbsent(group.getTurret(), (t) -> new UModList<>()).mList.add(type);

        ItemRegistry.TURRET_AMMO.put(type.getId(), new ItemAmmo(type));
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id) {
        if( !this.getType(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get turret ammo item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_AMMO.get(id), 1);
    }

    private void finalizeForLexicon() {
        if( !this.finalizedForLexicon ) {
            this.finalizedForLexicon = true;
            this.uAmmoTypes.forEach(type -> {
                IAmmunitionGroup g = type.getGroup();
                this.ammoGroupsFromTurret.computeIfAbsent(g.getTurret(), t -> new UModSet<>()).mSet.add(g);
                this.ammoGroups.putIfAbsent(g.getId(), g);
                this.ammoTypesFromGroup.computeIfAbsent(g.getId(), t -> new UModList<>()).mList.add(type);
            });
        }
    }

    @Nonnull
    public Collection<IAmmunition> getTypes(IAmmunitionGroup group) {
        this.finalizeForLexicon();
        return this.ammoTypesFromGroup.get(group.getId()).umList;
    }

    @Nonnull
    public Collection<IAmmunitionGroup> getGroups(ITurret turret) {
        this.finalizeForLexicon();
        return this.ammoGroupsFromTurret.get(turret).umSet;
    }

    @Nonnull
    public Collection<IAmmunitionGroup> getGroups() {
        this.finalizeForLexicon();
        return this.ammoGroups.values();
    }

    private static class UModList<T>
    {
        final ArrayList<T> mList = new ArrayList<>();
        final Collection<T> umList = Collections.unmodifiableList(this.mList);
    }

    private static class UModSet<T>
    {
        final HashSet<T> mSet = new HashSet<>();
        final Collection<T> umSet = Collections.unmodifiableSet(this.mSet);
    }
}
