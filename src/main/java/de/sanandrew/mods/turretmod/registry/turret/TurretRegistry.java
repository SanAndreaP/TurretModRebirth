/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInfo;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.util.CommonProxy;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.Level;

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

    private final Map<UUID, ITurretInfo> infoFromUUID;
    private final Map<Class<? extends EntityTurret>, ITurretInfo> infoFromClass;
    private final List<ITurretInfo> infos;

    private TurretRegistry() {
        this.infoFromUUID = new HashMap<>();
        this.infoFromClass = new HashMap<>();
        this.infos = new ArrayList<>();
    }

    @Override
    public List<ITurretInfo> getRegisteredInfos() {
        return new ArrayList<>(this.infos);
    }

    @Override
    public ITurretInfo getInfo(UUID uuid) {
        return this.infoFromUUID.get(uuid);
    }

    @Override
    public ITurretInfo getInfo(Class<? extends EntityTurret> clazz) {
        return this.infoFromClass.get(clazz);
    }

    @Override
    public boolean registerTurretInfo(ITurretInfo type) {
        return this.registerTurretInfo(type, false);
    }

    boolean registerTurretInfo(ITurretInfo type, boolean registerEntity) {
        if( type == null ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot register NULL as Turret-Info!", new InvalidParameterException());
            return false;
        }

        if( type.getName() == null || type.getName().isEmpty() ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Turret-Info %s has an empty/NULL name! Cannot register the Void.", type.getClass().getName()), new InvalidParameterException());
            return false;
        }

        if( this.infoFromUUID.containsKey(type.getUUID()) ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("The UUID of Turret-Info %s is already registered! Use another UUID. JUST DO IT!", type.getName()), new InvalidParameterException());
            return false;
        }

        if( type.getTurretClass() == null ) {
            TmrConstants.LOG.log(Level.ERROR, String.format("Turret-Info %s has no turret_placer! wat?", type.getName()), new InvalidParameterException());
            return false;
        }

        this.infoFromUUID.put(type.getUUID(), type);
        this.infoFromClass.put(type.getTurretClass(), type);
        this.infos.add(type);

        if( registerEntity ) {
            String name = type.getName();
            EntityRegistry.registerModEntity(new ResourceLocation(TmrConstants.ID, name), type.getTurretClass(), TmrConstants.ID + '.' + name, CommonProxy.entityCount++, TurretModRebirth.instance, 128, 1, false);
        }

        return true;
    }
}
