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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class UpdateTurretStatePacket
        extends SimpleMessage
{
    private final int turretId;
    private final int       entityToAttackId;
    private final int       currAmmoCount;
    @Nonnull
    private final ItemStack ammoStack;
    private final boolean   isShooting;
    private final byte[]    delegateData;

    private final Map<ResourceLocation, Boolean> creatureTargets;
    private final Map<UUID, Boolean>             playerTargets;

    public UpdateTurretStatePacket(ITurretEntity turret) {
        this.turretId = turret.get().getId();
        TargetProcessor tgtProc = (TargetProcessor) turret.getTargetProcessor();
        if( tgtProc.hasTarget() ) {
            this.entityToAttackId = tgtProc.getTarget().getId();
        } else {
            this.entityToAttackId = -1;
        }
        this.currAmmoCount = tgtProc.getAmmoCount();
        this.ammoStack = tgtProc.getAmmoStack();
        this.isShooting = tgtProc.isShooting();
        this.creatureTargets = tgtProc.grabUpdatedCreatures();
        this.playerTargets = tgtProc.grabUpdatedPlayers();

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

    public UpdateTurretStatePacket(PacketBuffer buffer) {
        this.turretId = buffer.readInt();
        this.entityToAttackId = buffer.readInt();
        this.currAmmoCount = buffer.readInt();
        this.isShooting = buffer.readBoolean();
        this.ammoStack = buffer.readItem();

        this.creatureTargets = new HashMap<>();
        for( int i = 0, max = buffer.readInt(); i < max; i++ ) {
            this.creatureTargets.put(buffer.readResourceLocation(), buffer.readBoolean());
        }
        this.playerTargets = new HashMap<>();
        for( int i = 0, max = buffer.readInt(); i < max; i++ ) {
            this.playerTargets.put(buffer.readUUID(), buffer.readBoolean());
        }

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

        buffer.writeInt(this.creatureTargets.size());
        for( Map.Entry<ResourceLocation, Boolean> e : this.creatureTargets.entrySet() ) {
            buffer.writeResourceLocation(e.getKey());
            buffer.writeBoolean(e.getValue());
        }
        buffer.writeInt(this.playerTargets.size());
        for( Map.Entry<UUID, Boolean> e : this.playerTargets.entrySet() ) {
            buffer.writeUUID(e.getKey());
            buffer.writeBoolean(e.getValue());
        }

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
                ((TargetProcessor) turret.getTargetProcessor()).updateClientState(this.entityToAttackId, this.currAmmoCount, this.ammoStack, this.isShooting,
                                                                                  this.creatureTargets, this.playerTargets);

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
