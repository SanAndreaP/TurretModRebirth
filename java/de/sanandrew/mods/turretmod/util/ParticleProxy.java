/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.network.PacketManager;
import org.apache.commons.lang3.ArrayUtils;

public class ParticleProxy
{
    public static final short ITEM_TRANSMITTER = 0;

    public void spawnParticle(double posX, double posY, double posZ, int dimension, short particleId, Tuple additionalData) {
        Object[] obj = new Object[] {posX, posY, posZ, particleId, additionalData != null};
        if( additionalData != null ) {
            obj = ArrayUtils.addAll(obj, additionalData.toArray());
        }

        PacketManager.sendToAllAround(PacketManager.SPAWN_PARTICLE, dimension, posX, posY, posZ, 128.0D, Tuple.from(obj));
    }

    public void spawnParticleClt(double posX, double posY, double posZ, short particleId, Tuple data) { }
}
