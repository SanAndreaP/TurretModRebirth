/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network.packet;

import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.tileentity.TileEntityItemTransmitter;
import de.sanandrew.mods.turretmod.util.TurretMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.network.INetHandler;

import java.io.IOException;

public class PacketSendTransmitterExpTime
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        TurretMod.proxy.processTransmitterExpTime(stream);
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple data) throws IOException {
        stream.writeInt((Integer) data.getValue(0)); //xCoord
        stream.writeInt((Integer) data.getValue(1)); //yCoord
        stream.writeInt((Integer) data.getValue(2)); //zCoord
        stream.writeInt((Integer) data.getValue(3)); //timeout
    }

    public static void sendToAllAround(TileEntityItemTransmitter transmitter) {
        PacketManager.sendToAllAround(PacketManager.UPDATE_TRANSMITTER_EXP_TIME, transmitter.getWorldObj().provider.dimensionId, transmitter.xCoord,
                                      transmitter.yCoord, transmitter.zCoord, 16.0D, Quartet.with(transmitter.xCoord, transmitter.yCoord, transmitter.zCoord,
                                                                                                  transmitter.requestTimeout));
    }
}
