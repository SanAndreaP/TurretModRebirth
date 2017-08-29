/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
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

    private final Map<UUID, ITurret> turretFromUUID;
    private final Map<Class<? extends ITurret>, ITurret> turretFromClass;
    private final List<ITurret> turrets;

    private TurretRegistry() {
        this.turretFromUUID = new HashMap<>();
        this.turretFromClass = new HashMap<>();
        this.turrets = new ArrayList<>();
    }

    @Override
    public List<ITurret> getTurrets() {
        return new ArrayList<>(this.turrets);
    }

    @Override
    public ITurret getTurret(UUID uuid) {
        return MiscUtils.defIfNull(this.turretFromUUID.get(uuid), NULL_TURRET);
    }

    @Override
    public ITurret getTurret(Class<? extends ITurret> clazz) {
        return MiscUtils.defIfNull(this.turretFromClass.get(clazz), NULL_TURRET);
    }

    @Override
    public boolean registerTurret(ITurret type) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Turret-Info!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Turret-Info %s has an empty/NULL name! Cannot register the Void.", type.getClass().getName()), new InvalidParameterException());
            return false;
        }

        if( this.turretFromUUID.containsKey(type.getId()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of Turret-Info %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        this.turretFromUUID.put(type.getId(), type);
        this.turretFromClass.put(type.getClass(), type);
        this.turrets.add(type);

        return true;
    }

    private static class EmptyTurret
            implements ITurret
    {
        private static final AxisAlignedBB BB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        @Override
        public String getName() {
            return "empty";
        }

        @Nonnull
        @Override
        public UUID getId() {
            return UuidUtils.EMPTY_UUID;
        }

        @Override
        public float getInfoHealth() {
            return 0;
        }

        @Override
        public int getInfoBaseAmmoCapacity() {
            return 0;
        }

        @Override
        public String getInfoRange() {
            return "n/a";
        }

        @Override
        public ResourceLocation getItemModel() {
            return null;
        }

        @Override
        public UUID getRecipeId() {
            return null;
        }

        @Override
        public ResourceLocation getStandardTexture(ITurretInst inst) {
            return null;
        }

        @Override
        public ResourceLocation getGlowTexture(ITurretInst inst) {
            return null;
        }

        @Override
        public SoundEvent getShootSound(ITurretInst inst) {
            return null;
        }

        @Override
        public AxisAlignedBB getRangeBB(ITurretInst inst) {
            return BB;
        }
    }
}
