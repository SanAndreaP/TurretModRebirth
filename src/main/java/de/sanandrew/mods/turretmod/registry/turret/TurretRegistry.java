/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TurretRegistry
        implements ITurretRegistry
{
    public static final TurretRegistry INSTANCE = new TurretRegistry();
    public static final ITurret NULL_TYPE = new EmptyTurret();

    private final Map<ResourceLocation, ITurret> turretFromRL;
    private final Map<Class<? extends ITurret>, ITurret> turretFromClass;

    private final Collection<ITurret> turrets;

    private TurretRegistry() {
        this.turretFromRL = new HashMap<>();
        this.turretFromClass = new HashMap<>();

        this.turrets = Collections.unmodifiableCollection(turretFromRL.values());
    }

    @Override
    public Collection<ITurret> getTypes() {
        return this.turrets;
    }

    @Override
    public ITurret getType(ResourceLocation id) {
        return this.turretFromRL.getOrDefault(id, NULL_TYPE);
    }

    @Override
    public ITurret getType(Class<? extends ITurret> clazz) {
        return this.turretFromClass.getOrDefault(clazz, NULL_TYPE);
    }

    @Override
    public void register(ITurret type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as turret!", new InvalidParameterException());
            return;
        }

        if( this.turretFromRL.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The turret %s is already registered!", type.getId()), new InvalidParameterException());
            return;
        }

        this.turretFromRL.put(type.getId(), type);
        this.turretFromClass.put(type.getClass(), type);

        ItemRegistry.TURRET_PLACERS.put(type.getId(), new ItemTurret(type));
    }

    @Override
    @Nonnull
    public ItemStack getItem(ResourceLocation id) {
        if( !this.getType(id).isValid() ) {
            throw new IllegalArgumentException("Cannot get turret item with invalid type!");
        }

        return new ItemStack(ItemRegistry.TURRET_PLACERS.get(id), 1);
    }

    @Override
    @Nonnull
    public ItemStack getItem(ITurretInst turretInst) {
        ItemStack stack = this.getItem(turretInst.getTurret().getId());
        new ItemTurret.TurretStats(turretInst).updateData(stack);

        return stack;
    }

    @Override
    public ITurret getType(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemTurret ) {
            return ((ItemTurret) stack.getItem()).turret;
        }

        return TurretRegistry.NULL_TYPE;
    }

    private static class EmptyTurret
            implements ITurret
    {
        private static final AxisAlignedBB BB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        @Nonnull @Override public ResourceLocation getId() { return new ResourceLocation("null"); }
        @Override public ResourceLocation getStandardTexture(ITurretInst turretInst) { return null; }
        @Override public ResourceLocation getGlowTexture(ITurretInst turretInst) { return null; }
        @Override public SoundEvent getShootSound(ITurretInst turretInst) { return null; }
        @Override public AxisAlignedBB getRangeBB(ITurretInst turretInst) { return BB; }
        @Override public int getTier() { return 0; }
        @Override public float getHealth() { return 0; }
        @Override public int getAmmoCapacity() { return 0; }
        @Override public int getReloadTicks() { return 0; }
        @Override public boolean isValid() { return false; }
    }
}
