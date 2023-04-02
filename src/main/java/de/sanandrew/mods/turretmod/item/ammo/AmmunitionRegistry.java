/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.ammo;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.DeferredRegister;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AmmunitionRegistry
        implements IAmmunitionRegistry
{
    public static final AmmunitionRegistry INSTANCE = new AmmunitionRegistry();

    private static final String NBT_SUBTYPE = "Subtype";

    private final Map<ResourceLocation, IAmmunition>  ammoTypes;
    private final Map<ITurret, UModList<IAmmunition>> ammoTypeFromTurret;
    private final Collection<IAmmunition>             ammoTypeCollection;

    private static final IAmmunition EMPTY = new IAmmunition()
    {
        private final ResourceLocation id = new ResourceLocation("null");

        @Nonnull @Override public ResourceLocation getId()                             { return this.id; }
        @Nonnull @Override public ITurret          getApplicableTurret()               { return TurretRegistry.INSTANCE.getDefault(); }
        @Override          public int              getCapacity()                       { return 0; }
        @Override          public IProjectile      getProjectile(ITurretEntity turret) { return null; }
        @Override          public boolean          isValid()                           { return false; }
        @Override          public Range<Float>     getDamageInfo()                     { return null; }
    };

    private AmmunitionRegistry() {
        this.ammoTypes = new HashMap<>();
        this.ammoTypeFromTurret = new HashMap<>();

        this.ammoTypeCollection = Collections.unmodifiableCollection(this.ammoTypes.values());
    }

    @Override
    public void registerItems(DeferredRegister<Item> register, final String modId) {
        ItemRegistry.TURRET_AMMO.entrySet().stream().filter(e -> e.getKey().getNamespace().equals(modId))
                                .forEach(e -> register.register(e.getKey().getPath(), e::getValue));
    }

    @Override
    @Nonnull
    public Collection<IAmmunition> getAll() {
        return this.ammoTypeCollection;
    }

    @Override
    @Nonnull
    public IAmmunition get(ResourceLocation id) {
        return this.ammoTypes.getOrDefault(id, EMPTY);
    }

    @Override
    @Nonnull
    public IAmmunition get(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof AmmoItem ) {
            return ((AmmoItem) stack.getItem()).getAmmo();
        }

        return EMPTY;
    }

    @Override
    @Nonnull
    public Collection<IAmmunition> getAll(ITurret turret) {
        return this.ammoTypeFromTurret.get(turret).umList;
    }

    @Override
    public void register(@Nonnull IAmmunition obj) {
        if( this.ammoTypes.containsKey(obj.getId()) ) {
            String msg = String.format("The ammo ID %s is already registered!", obj.getId());
            TmrConstants.LOG.log(Level.ERROR, msg, new InvalidParameterException());
            return;
        }

        if( obj.getCapacity() < 1 ) {
            String msg = String.format("Ammo ID %s provides less than 1 round!", obj.getId());
            TmrConstants.LOG.log(Level.ERROR, msg, new InvalidParameterException());
            return;
        }

        this.ammoTypes.put(obj.getId(), obj);
        this.ammoTypeFromTurret.computeIfAbsent(obj.getApplicableTurret(), t -> new UModList<>()).mList.add(obj);

        ItemRegistry.TURRET_AMMO.put(obj.getId(), new AmmoItem(obj.getId()));
    }

    @Override
    public String getSubtype(ItemStack stack) {
        if( stack.getItem() instanceof AmmoItem ) {
            CompoundNBT tmrStack = stack.getTagElement(TmrConstants.ID);
            if( tmrStack != null && tmrStack.contains(NBT_SUBTYPE, Constants.NBT.TAG_STRING) ) {
                return tmrStack.getString(NBT_SUBTYPE);
            }
        }

        return null;
    }

    @Override
    public ItemStack setSubtype(ItemStack stack, String type) {
        if( !Strings.isNullOrEmpty(type) ) {
            Item item = stack.getItem();
            if( item instanceof AmmoItem && Arrays.asList(((AmmoItem) item).getAmmo().getSubtypes()).contains(type) ) {
                stack.getOrCreateTagElement(TmrConstants.ID).putString(NBT_SUBTYPE, type);
            }
        }

        return stack;
    }

    @Override
    @Nonnull
    public IAmmunition getDefault() {
        return EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack getItem(ResourceLocation id, int count) {
        return this.getItem(id, null, count);
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id, String subtype, int count) {
        if( !this.get(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get turret ammo item with invalid type!");
        }

        return setSubtype(new ItemStack(ItemRegistry.TURRET_AMMO.get(id), count), subtype);
    }

    private static class UModList<T>
    {
        private final ArrayList<T>  mList  = new ArrayList<>();
        private final Collection<T> umList = Collections.unmodifiableList(this.mList);
    }
}
