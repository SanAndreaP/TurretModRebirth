/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.ITargetProcessor;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.entity.turret.TargetProcessor;
import dev.sanandrea.mods.turretmod.entity.turret.TurretEntity;
import dev.sanandrea.mods.turretmod.init.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncTurretStatePacket
        extends SimpleMessage
{
    public static final byte TARGET = 0b1;
    public static final byte AMMO = 0b10;
    public static final byte DELEGATE = 0b100;
    public static final byte OWNER = 0b1000;

    private final byte transferType;

    private final int turretId;
    private int       entityToAttackId;
    private int       currAmmoCount;
    @Nonnull
    private ItemStack ammoStack = ItemStack.EMPTY;
    private boolean   isShooting;
    private byte[]    delegateData;

    private UUID           ownerId;
    private ITextComponent ownerName;

    public SyncTurretStatePacket(ITurretEntity turret, byte transferType) {
        this.transferType = transferType;
        this.turretId = turret.get().getId();
        ITargetProcessor tgtProc = turret.getTargetProcessor();

        if( (this.transferType & TARGET) == TARGET ) {
            if( tgtProc.hasTarget() ) {
                this.entityToAttackId = tgtProc.getTarget().getId();
            } else {
                this.entityToAttackId = -1;
            }
            this.isShooting = tgtProc.isShooting();
        }
        if( (this.transferType & AMMO) == AMMO ) {
            this.currAmmoCount = tgtProc.getAmmoCount();
            this.ammoStack = tgtProc.getAmmoStack();
        }
        if( (this.transferType & DELEGATE) == DELEGATE ) {
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
        if( (this.transferType & OWNER) == OWNER ) {
            this.ownerId = turret.getOwnerId();
            this.ownerName = turret.getOwnerName();
        }
    }

    public SyncTurretStatePacket(PacketBuffer buffer) {
        this.transferType = buffer.readByte();
        this.turretId = buffer.readInt();
        if( (this.transferType & TARGET) == TARGET ) {
            this.entityToAttackId = buffer.readInt();
            this.isShooting = buffer.readBoolean();
        }
        if( (this.transferType & AMMO) == AMMO ) {
            this.currAmmoCount = buffer.readInt();
            this.ammoStack = buffer.readItem();
        }
        if( (this.transferType & DELEGATE) == DELEGATE ) {
            this.delegateData = new byte[buffer.readInt()];
            if( this.delegateData.length > 0 ) {
                buffer.readBytes(this.delegateData);
            }
        }
        if( (this.transferType & OWNER) == OWNER ) {
            this.ownerId = buffer.readUUID();
            this.ownerName = buffer.readComponent();
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeByte(this.transferType);
        buffer.writeInt(this.turretId);
        if( (this.transferType & TARGET) == TARGET ) {
            buffer.writeInt(this.entityToAttackId);
            buffer.writeBoolean(this.isShooting);
        }
        if( (this.transferType & AMMO) == AMMO ) {
            buffer.writeInt(this.currAmmoCount);
            buffer.writeItem(this.ammoStack);
        }
        if( (this.transferType & DELEGATE) == DELEGATE ) {
            buffer.writeInt(this.delegateData.length);
            buffer.writeBytes(this.delegateData);
        }
        if( (this.transferType & OWNER) == OWNER ) {
            buffer.writeUUID(this.ownerId);
            buffer.writeComponent(this.ownerName);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        PlayerEntity player = TurretModRebirth.PROXY.getNetworkPlayer(supplier);
        if( player != null ) {
            Entity e = player.level.getEntity(this.turretId);
            if( e instanceof ITurretEntity ) {
                ITurretEntity turret = (ITurretEntity) e;
                if( (this.transferType & TARGET) == TARGET ) {
                    getTargetProcessor(turret).updateClientTarget(this.entityToAttackId, this.isShooting);
                }
                if( (this.transferType & AMMO) == AMMO ) {
                    getTargetProcessor(turret).updateClientAmmo(this.currAmmoCount, this.ammoStack);
                }
                if( (this.transferType & DELEGATE) == DELEGATE && this.delegateData.length > 0 ) {
                    try( ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(this.delegateData)) ) {
                        turret.getDelegate().readSyncData(turret, ois);
                    } catch( IOException ex ) {
                        TmrConstants.LOG.log(Level.ERROR, "Cannot sync turret instance", ex);
                    }
                }
                if( (this.transferType & OWNER) == OWNER && e instanceof TurretEntity ) {
                    ((TurretEntity) e).syncOwner(this.ownerId, this.ownerName);
                }
            }
        }
    }

    private static TargetProcessor getTargetProcessor(ITurretEntity turret) {
        return (TargetProcessor) turret.getTargetProcessor();
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
