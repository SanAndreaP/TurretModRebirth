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
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.TurretMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class PacketSendTargetFlag
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        if( iNetHandler instanceof NetHandlerPlayServer ) {
            EntityTurretBase turret = (EntityTurretBase) ((NetHandlerPlayServer) iNetHandler).playerEntity.worldObj.getEntityByID(stream.readInt());
            String entityName = stream.readUTF();
            try {
                @SuppressWarnings("unchecked")
                Class<? extends EntityLiving> entityCls = (Class<? extends EntityLiving>) EntityList.stringToClassMapping.get(entityName);
                turret.getTargetHandler().toggleTarget(entityCls, stream.readBoolean());
            } catch( ClassCastException ex ) {
                TurretMod.MOD_LOG.printf(Level.WARN, "Cannot apply target %s! This is an invalid entity name!", entityName);
                throw new IOException(ex);
            }
        }
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple data) throws IOException {
        stream.writeInt((Integer) data.getValue(0));
        stream.writeUTF((String) data.getValue(1));
        stream.writeBoolean((Boolean) data.getValue(2));
    }

    public static void sendToServer(EntityTurretBase turret, Class<? extends EntityLiving> entityCls, boolean flag) {
        String registeredEntityName = (String) EntityList.classToStringMapping.get(entityCls);
        PacketManager.sendToServer(PacketManager.SEND_TARGET_FLAG, Triplet.with(turret.getEntityId(), registeredEntityName, flag));
    }
}
