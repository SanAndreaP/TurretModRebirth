/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.util;

import de.sanandrew.core.manpack.util.client.helpers.SAPClientUtils;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.client.effect.ParticleItemTransmitterAction;
import de.sanandrew.mods.turretmod.client.util.ParticleProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ClientParticleProxy
        extends ParticleProxy
{
    @Override
    public void spawnParticleClt(double posX, double posY, double posZ, short particleId, Tuple data) {
        World worldObj = Minecraft.getMinecraft().theWorld;

        switch( particleId ) {
            case ITEM_TRANSMITTER:
                double destX = (double) data.getValue(0);
                double destY = (double) data.getValue(1);
                double destZ = (double) data.getValue(2);

                Vec3 dirVec = Vec3.createVectorHelper(destX - posX, destY - posY, destZ - posZ);
                Vec3 dirVecNorm = dirVec.normalize();

                double dist = Math.abs(dirVec.lengthVector());
                for( double d = 0; d < dist; d += 0.25D ) {
                    SAPClientUtils.spawnParticle(new ParticleItemTransmitterAction(worldObj, posX + dirVecNorm.xCoord * d, posY + dirVecNorm.yCoord * d, posZ + dirVecNorm.zCoord * d));
                }

                break;
        }
    }
}
