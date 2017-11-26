/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class PacketSyncUpgradeInst
        extends AbstractMessage<PacketSyncUpgradeInst>
{
    private int turretId;
    private UUID upgradeId;
    private byte[] instData;

    @SuppressWarnings("unused")
    public PacketSyncUpgradeInst() { }

    public PacketSyncUpgradeInst(ITurretInst turret, UUID upgradeId) {
        this.turretId = turret.getEntity().getEntityId();
        this.upgradeId = upgradeId;
        IUpgradeInstance<?> upgInstance = turret.getUpgradeProcessor().getUpgradeInstance(upgradeId);
        if( upgInstance != null ) {
            try( ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos) ) {
                upgInstance.toBytes(oos);
                oos.close();
                this.instData = bos.toByteArray();
            } catch( IOException e ) {
                TmrConstants.LOG.log(Level.ERROR, "Cannot sync upgrade instance", e);
            }
        }
    }

    @Override
    public void handleClientMessage(PacketSyncUpgradeInst packet, EntityPlayer player) {
        this.handleServerMessage(packet, player);
    }

    @Override
    public void handleServerMessage(PacketSyncUpgradeInst packet, EntityPlayer player) {
        if( packet.instData.length > 0 ) {
            Entity e = player.world.getEntityByID(packet.turretId);
            if( e instanceof ITurretInst ) {
                IUpgradeProcessor processor = ((ITurretInst) e).getUpgradeProcessor();
                IUpgradeInstance<?> upgInstance = processor.getUpgradeInstance(packet.upgradeId);
                if( upgInstance != null ) {
                    try( ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.instData)) ) {
                        upgInstance.fromBytes(ois);
                    } catch( IOException ex ) {
                        TmrConstants.LOG.log(Level.ERROR, "Cannot sync upgrade instance", ex);
                    }
                } else {
                    TmrConstants.LOG.log(Level.ERROR, "Cannot sync upgrade instance from upgrade ID {0}; it has no default instance available!");
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.upgradeId = new UUID(buf.readLong(), buf.readLong());
        int lng = buf.readInt();
        this.instData = new byte[lng];
        if( lng > 0 ) {
            buf.readBytes(this.instData);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeLong(this.upgradeId.getMostSignificantBits());
        buf.writeLong(this.upgradeId.getLeastSignificantBits());
        buf.writeInt(this.instData.length);
        buf.writeBytes(this.instData);
    }
}
