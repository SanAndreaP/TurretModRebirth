/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network.packet;

import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.util.TurretMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.network.INetHandler;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class PacketSpawnParticle
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler handler) throws IOException {
        try( ObjectInputStream ois = new ObjectInputStream(stream) ) {
            Tuple data = (Tuple) ois.readObject();

            double posX = (double) data.getValue(0);
            double posY = (double) data.getValue(1);
            double posZ = (double) data.getValue(2);
            short particleId = (short) data.getValue(3);
            Tuple particleData = (boolean) data.getValue(4) ? Tuple.from(Arrays.copyOfRange(data.toArray(), 5, data.getSize())) : null;

            TurretMod.particleProxy.spawnParticleClt(posX, posY, posZ, particleId, particleData);
        } catch( ClassNotFoundException | ClassCastException ex ) {
            TurretMod.MOD_LOG.log(Level.WARN, "Cannot deserialize particle data! It seems to be corrupt!", ex);
        }
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple data) throws IOException {
        try( ObjectOutputStream oos = new ObjectOutputStream(stream) ) {
            oos.writeObject(data);
        } catch( IOException ex ) {
            TurretMod.MOD_LOG.log(Level.WARN, "Cannot serialize particle data! It seems to be corrupt!", ex);
        }
    }
}
