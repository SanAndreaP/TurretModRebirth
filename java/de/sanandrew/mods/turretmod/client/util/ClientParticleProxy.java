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
import de.sanandrew.mods.turretmod.client.particle.ParticleItemTransmitterAction;
import de.sanandrew.mods.turretmod.util.ParticleProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientParticleProxy
        extends ParticleProxy
{
    @Override
    public void spawnParticleClt(double posX, double posY, double posZ, short particleId, Tuple data) {
        World worldObj = Minecraft.getMinecraft().theWorld;

        switch( particleId ) {
            case ITEM_TRANSMITTER:
                SAPClientUtils.spawnParticle(new ParticleItemTransmitterAction(worldObj, posX + 0.5D, posY + 1, posZ + 0.5D));
                SAPClientUtils.spawnParticle(new ParticleItemTransmitterAction(worldObj, posX + 0.2D, posY + 1, posZ + 0.2D));
                SAPClientUtils.spawnParticle(new ParticleItemTransmitterAction(worldObj, posX + 0.2D, posY + 1, posZ + 0.8D));
                SAPClientUtils.spawnParticle(new ParticleItemTransmitterAction(worldObj, posX + 0.8D, posY + 1, posZ + 0.2D));
                SAPClientUtils.spawnParticle(new ParticleItemTransmitterAction(worldObj, posX + 0.8D, posY + 1, posZ + 0.8D));
                break;
        }
    }
}
