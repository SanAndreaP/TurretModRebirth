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
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PacketUpdateTurretState
        extends AbstractMessage<PacketUpdateTurretState>
{
    private int turretId;
    private int entityToAttackId;
    private int currAmmoCap;
    @Nonnull
    private ItemStack ammoStack;
    private boolean isShooting;
    private byte[] delegateData;

    @SuppressWarnings("unused")
    public PacketUpdateTurretState() { }

    public PacketUpdateTurretState(ITurretInst turret) {
        this.turretId = turret.getEntity().getEntityId();
        ITargetProcessor tgtProc = turret.getTargetProcessor();
        if( tgtProc.hasTarget() ) {
            this.entityToAttackId = tgtProc.getTarget().getEntityId();
        } else {
            this.entityToAttackId = -1;
        }
        this.currAmmoCap = tgtProc.getAmmoCount();
        this.ammoStack = tgtProc.getAmmoStack();
        this.isShooting = tgtProc.isShooting();

        try( ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos) ) {
            turret.getTurret().writeSyncData(turret, oos);
            oos.close();
            this.delegateData = bos.toByteArray();
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot sync turret instance", e);
        }
    }

    @Override
    public void handleClientMessage(PacketUpdateTurretState packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretId);
        if( e instanceof ITurretInst ) {
            ITurretInst turret = (ITurretInst) e;
            ((TargetProcessor) turret.getTargetProcessor()).updateClientState(packet.entityToAttackId, packet.currAmmoCap, packet.ammoStack, packet.isShooting);

            if( packet.delegateData.length > 0 ) {
                try( ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.delegateData)) ) {
                    turret.getTurret().readSyncData(turret, ois);
                } catch( IOException ex ) {
                    TmrConstants.LOG.log(Level.ERROR, "Cannot sync turret instance", ex);
                }
            }
        }
    }

    @Override
    public void handleServerMessage(PacketUpdateTurretState packet, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.entityToAttackId = buf.readInt();
        this.currAmmoCap = buf.readInt();
        this.isShooting = buf.readBoolean();
        this.ammoStack = ByteBufUtils.readItemStack(buf);
        int lng = buf.readInt();
        this.delegateData = new byte[lng];
        if( lng > 0 ) {
            buf.readBytes(this.delegateData);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeInt(this.entityToAttackId);
        buf.writeInt(this.currAmmoCap);
        buf.writeBoolean(this.isShooting);
        ByteBufUtils.writeItemStack(buf, this.ammoStack);
        buf.writeInt(this.delegateData.length);
        buf.writeBytes(this.delegateData);
    }
}
