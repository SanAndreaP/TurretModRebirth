/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

public class SyncTurretStatePacket
        extends SimpleMessage
{
    private final int turretId;
    private final int       entityToAttackId;
    private final int       currAmmoCount;
    @Nonnull
    private final ItemStack ammoStack;
    private final boolean   isShooting;
    private final byte[]    delegateData;

    public SyncTurretStatePacket(ITurretEntity turret) {
        this.turretId = turret.get().getId();
        ITargetProcessor tgtProc = turret.getTargetProcessor();
        if( tgtProc.hasTarget() ) {
            this.entityToAttackId = tgtProc.getTarget().getId();
        } else {
            this.entityToAttackId = -1;
        }
        this.currAmmoCount = tgtProc.getAmmoCount();
        this.ammoStack = tgtProc.getAmmoStack();
        this.isShooting = tgtProc.isShooting();

        byte[] dData = new byte[0];
        try( ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos) ) {
            turret.getDelegate().writeSyncData(turret, oos);
            oos.flush();
            dData = bos.toByteArray();
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot sync turret instance", e);
        }
        this.delegateData = dData;
    }

    public SyncTurretStatePacket(PacketBuffer buffer) {
        this.turretId = buffer.readInt();
        this.entityToAttackId = buffer.readInt();
        this.currAmmoCount = buffer.readInt();
        this.isShooting = buffer.readBoolean();
        this.ammoStack = buffer.readItem();

        this.delegateData = new byte[buffer.readInt()];
        if( this.delegateData.length > 0 ) {
            buffer.readBytes(this.delegateData);
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.turretId);
        buffer.writeInt(this.entityToAttackId);
        buffer.writeInt(this.currAmmoCount);
        buffer.writeBoolean(this.isShooting);
        buffer.writeItem(this.ammoStack);

        buffer.writeInt(this.delegateData.length);
        buffer.writeBytes(this.delegateData);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        PlayerEntity player = TurretModRebirth.PROXY.getNetworkPlayer(supplier);
        if( player != null ) {
            Entity e = player.level.getEntity(this.turretId);
            if( e instanceof ITurretEntity ) {
                ITurretEntity turret = (ITurretEntity) e;
                ((TargetProcessor) turret.getTargetProcessor()).updateClientState(this.entityToAttackId, this.currAmmoCount, this.ammoStack, this.isShooting);

                if( this.delegateData.length > 0 ) {
                    try( ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(this.delegateData)) ) {
                        turret.getDelegate().readSyncData(turret, ois);
                    } catch( IOException ex ) {
                        TmrConstants.LOG.log(Level.ERROR, "Cannot sync turret instance", ex);
                    }
                }
            }
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
