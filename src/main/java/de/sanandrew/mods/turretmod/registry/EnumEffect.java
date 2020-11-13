/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.network.PacketEffect;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import net.minecraft.entity.Entity;

public enum EnumEffect
{
    ASSEMBLY_SPARK,
    SHOTGUN_SMOKE,
    CRYO_VAPOR,
    MINIGUN_SMOKE,
    LEVEL_UP,
    PROJECTILE_DEATH;

    public static final EnumEffect[] VALUES = values();

    public void addEffect(boolean remote, int dimension, double x, double y, double z) {
        this.addEffect(remote, dimension, x, y, z, null);
    }

    public void addEffect(boolean remote, int dimension, double x, double y, double z, Tuple data) {
        if( remote ) {
            TurretModRebirth.proxy.addEffect(this, x, y, z, data);
        } else {
            PacketRegistry.sendToAllAround(new PacketEffect(this, x, y, z, data), dimension, x, y, z, 64.0D);
        }
    }

    public void addEffect(Entity entity) {
        this.addEffect(entity, null);
    }

    public void addEffect(Entity entity, Tuple data) {
        this.addEffect(entity.world.isRemote, entity.dimension, entity.posX, entity.posY, entity.posZ, data);
    }
}
