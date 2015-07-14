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
import de.sanandrew.core.manpack.util.javatuples.Pair;
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
import java.util.Map;
import java.util.Map.Entry;

public class PacketSendMultiTargetFlag
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        if( iNetHandler instanceof NetHandlerPlayServer ) {
            EntityTurretBase turret = (EntityTurretBase) ((NetHandlerPlayServer) iNetHandler).playerEntity.worldObj.getEntityByID(stream.readInt());
            try {
                for( int i = 0, count = stream.readInt(); i < count; i++ ) {
                    @SuppressWarnings("unchecked")
                    Class<? extends EntityLiving> entityCls = (Class<? extends EntityLiving>) EntityList.stringToClassMapping.get(stream.readUTF());
                    turret.getTargetHandler().toggleTarget(entityCls, stream.readBoolean());
                }
            } catch( ClassCastException ex ) {
                TurretMod.MOD_LOG.log(Level.WARN, "Cannot apply multi-target list! An entry is invalid!");
                throw new IOException(ex);
            }
        }
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple data) throws IOException {
        stream.writeInt((Integer) data.getValue(0));
        try {
            @SuppressWarnings("unchecked")
            Map<Class<? extends EntityLiving>, Boolean> newTargetStg = (Map<Class<? extends EntityLiving>, Boolean>) data.getValue(1);
            stream.writeInt(newTargetStg.size());
            for( Entry<Class<? extends EntityLiving>, Boolean> newTgts : newTargetStg.entrySet() ) {
                stream.writeUTF((String) EntityList.classToStringMapping.get(newTgts.getKey()));
                stream.writeBoolean(newTgts.getValue());
            }
        } catch( ClassCastException ex ) {
            TurretMod.MOD_LOG.log(Level.WARN, "Cannot send multi-target list to server! An entry is invalid!");
            throw new IOException(ex);
        }
    }

    public static void sendToServer(EntityTurretBase turret, Map<Class<? extends EntityLiving>, Boolean> newTargetStg) {
        PacketManager.sendToServer(PacketManager.SEND_MULTI_TARGET_FLAG, Pair.with(turret.getEntityId(), newTargetStg));
    }
}
