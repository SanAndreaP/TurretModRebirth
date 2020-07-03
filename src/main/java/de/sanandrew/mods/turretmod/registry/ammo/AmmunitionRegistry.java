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
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.item.ItemAmmo;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.*;

public final class AmmunitionRegistry
        implements IAmmunitionRegistry
{
    public static final AmmunitionRegistry INSTANCE = new AmmunitionRegistry();

    private final Map<ResourceLocation, IAmmunition>  ammoTypes;
    private final Map<ITurret, UModList<IAmmunition>> ammoTypesFromTurret;
    private final Collection<IAmmunition>             uAmmoTypes;

    private static final IAmmunition NULL_TYPE = new IAmmunition()
    {
        private final ResourceLocation id = new ResourceLocation("null");

        @Nonnull @Override public ResourceLocation getId()                               { return this.id; }
        @Nonnull @Override public ITurret          getTurret()                           { return TurretRegistry.INSTANCE.getDefaultObject(); }
        @Nonnull @Override public Range<Float>     getDamageInfo()                       { return Range.is(0.0F); }
        @Override          public int              getAmmoCapacity()                     { return 0; }
        @Override          public IProjectile      getProjectile(ITurretInst turretInst) { return null; }
        @Override          public boolean          isValid()                             { return false; }
    };

    private AmmunitionRegistry() {
        this.ammoTypes = new HashMap<>();
        this.ammoTypesFromTurret = new HashMap<>();

        this.uAmmoTypes = Collections.unmodifiableCollection(this.ammoTypes.values());
    }

    @Override
    @Nonnull
    public Collection<IAmmunition> getObjects() {
        return this.uAmmoTypes;
    }

    @Override
    @Nonnull
    public IAmmunition getObject(ResourceLocation id) {
        return this.ammoTypes.getOrDefault(id, NULL_TYPE);
    }

    @Override
    @Nonnull
    public IAmmunition getObject(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemAmmo ) {
            return ((ItemAmmo) stack.getItem()).ammo;
        }

        return NULL_TYPE;
    }

    @Override
    @Nonnull
    public Collection<IAmmunition> getObjects(ITurret turret) {
        return this.ammoTypesFromTurret.get(turret).umList;
    }

    @Override
    public void register(@Nonnull IAmmunition obj) {
        if( this.ammoTypes.containsKey(obj.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The ammo ID %s is already registered!", obj.getId()), new InvalidParameterException());
            return;
        }

        if( obj.getAmmoCapacity() < 1 ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Ammo ID %s provides less than 1 round!", obj.getId()), new InvalidParameterException());
            return;
        }

        this.ammoTypes.put(obj.getId(), obj);
        this.ammoTypesFromTurret.computeIfAbsent(obj.getTurret(), (t) -> new UModList<>()).mList.add(obj);

        ItemRegistry.TURRET_AMMO.put(obj.getId(), new ItemAmmo(obj));
    }

    @Override
    @Nonnull
    public IAmmunition getDefaultObject() {
        return NULL_TYPE;
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id) {
        if( !this.getObject(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get turret ammo item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_AMMO.get(id), 1);
    }

    private static class UModList<T>
    {
        private final ArrayList<T>  mList  = new ArrayList<>();
        private final Collection<T> umList = Collections.unmodifiableList(this.mList);
    }
}
