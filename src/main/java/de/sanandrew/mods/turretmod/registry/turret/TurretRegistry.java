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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TurretRegistry
        implements ITurretRegistry
{
    public static final TurretRegistry INSTANCE = new TurretRegistry();
    public static final ITurret NULL_TURRET = new EmptyTurret();

    @Deprecated
    private final Map<UUID, ITurret> turretFromUUID;
    private final Map<ResourceLocation, ITurret> turretFromRL;
    private final Map<Class<? extends ITurret>, ITurret> turretFromClass;
    private final List<ITurret> turrets;

    private TurretRegistry() {
        this.turretFromUUID = new HashMap<>();
        this.turretFromRL = new HashMap<>();
        this.turretFromClass = new HashMap<>();
        this.turrets = new ArrayList<>();
    }

    @Override
    public List<ITurret> getTurrets() {
        return new ArrayList<>(this.turrets);
    }

    @Override
    @Deprecated
    public ITurret getTurret(UUID uuid) {
        return MiscUtils.defIfNull(this.turretFromUUID.get(uuid), NULL_TURRET);
    }

    @Override
    public ITurret getTurret(ResourceLocation location) {
        return MiscUtils.defIfNull(this.turretFromRL.get(location), NULL_TURRET);
    }

    @Override
    public ITurret getTurret(Class<? extends ITurret> clazz) {
        return MiscUtils.defIfNull(this.turretFromClass.get(clazz), NULL_TURRET);
    }

    @Override
    public boolean registerTurret(ITurret type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as turret!", new InvalidParameterException());
            return false;
        }

        if( this.turretFromUUID.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of turret %s is already registered!", type.getRegistryId()), new InvalidParameterException());
            return false;
        }

        if( this.turretFromRL.containsKey(type.getRegistryId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The turret %s is already registered!", type.getRegistryId()), new InvalidParameterException());
            return false;
        }

        this.turretFromUUID.put(type.getId(), type);
        this.turretFromRL.put(type.getRegistryId(), type);
        this.turretFromClass.put(type.getClass(), type);
        this.turrets.add(type);

        ItemRegistry.TURRET_PLACERS.put(type.getRegistryId(), new ItemTurret(type));

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getTurretItem(ITurret type) {
        if( type == null ) {
            throw new IllegalArgumentException("Cannot get turret_placer item with NULL type!");
        }

        return new ItemStack(ItemRegistry.TURRET_PLACERS.get(type.getRegistryId()), 1);
    }

    @Override
    @Nonnull
    public ItemStack getTurretItem(ITurretInst turretInst) {
        ItemStack stack = this.getTurretItem(turretInst.getTurret());
        NBTTagCompound nbt = stack.getOrCreateSubCompound("TurretStats");
        EntityLiving turretL = turretInst.get();
        nbt.setFloat("TurretHealth", turretL.getHealth());
        if( turretL.hasCustomName() ) {
            nbt.setString("TurretName", turretL.getCustomNameTag());
        }

        return stack;
    }

    @Override
    public ITurret getTurret(@Nonnull ItemStack stack) {
        if( ItemStackUtils.isItem(stack, ItemRegistry.TURRET_PLACER) ) {
            NBTTagCompound nbt = stack.getTagCompound();
            if( nbt != null && nbt.hasKey("turretUUID") ) {
                String id = nbt.getString("turretUUID");
                if( UuidUtils.isStringUuid(id) ) {
                    return TurretRegistry.INSTANCE.getTurret(UUID.fromString(id));
                }
            }
        } else if( ItemStackUtils.isValid(stack) && stack.getItem() instanceof ItemTurret ) {
            return ((ItemTurret) stack.getItem()).getTurret();
        }

        return TurretRegistry.NULL_TURRET;
    }

    private static class EmptyTurret
            implements ITurret
    {
        private static final AxisAlignedBB BB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        @Nonnull @Override public UUID getId() { return UuidUtils.EMPTY_UUID; }

        @Nonnull @Override public ResourceLocation getRegistryId() { return new ResourceLocation("null"); }

        @Override public ResourceLocation getStandardTexture(ITurretInst turretInst) { return null; }

        @Override public ResourceLocation getGlowTexture(ITurretInst turretInst) { return null; }

        @Override public SoundEvent getShootSound(ITurretInst turretInst) { return null; }

        @Override public AxisAlignedBB getRangeBB(ITurretInst turretInst) { return BB; }

        @Override public int getTier() { return 0; }

        @Override public float getHealth() { return 0; }

        @Override public int getAmmoCapacity() { return 0; }

        @Override public int getReloadTicks() { return 0; }
    }
}
