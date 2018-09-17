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
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketUpdateTargets
        extends AbstractMessage<PacketUpdateTargets>
{
    private List<Class<? extends Entity>> entityTargets;
    private UUID[] playerTargets;
    private boolean isBlacklistEntity;
    private boolean isBlacklistPlayer;
    private int turretID;

    @SuppressWarnings("unused")
    public PacketUpdateTargets() {}

    public PacketUpdateTargets(ITargetProcessor processor) {
        this.entityTargets = processor.getEnabledEntityTargets();
        this.playerTargets = processor.getEnabledPlayerTargets();
        this.isBlacklistEntity = processor.isEntityBlacklist();
        this.isBlacklistPlayer = processor.isPlayerBlacklist();
        this.turretID = processor.getTurret().get().getEntityId();
    }

    @Override
    public void handleClientMessage(PacketUpdateTargets packet, EntityPlayer player) {
        this.handleServerMessage(packet, player);
    }

    @Override
    public void handleServerMessage(PacketUpdateTargets packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretID);
        if( e instanceof ITurretInst) {
            ITargetProcessor processor = ((ITurretInst) e).getTargetProcessor();
            processor.updateEntityTargets(packet.entityTargets);
            processor.updatePlayerTargets(packet.playerTargets);
            processor.setEntityBlacklist(packet.isBlacklistEntity);
            processor.setPlayerBlacklist(packet.isBlacklistPlayer);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromBytes(ByteBuf buf) {
        this.turretID = buf.readInt();
        this.entityTargets = new ArrayList<>();
        for( int i = 0, max = buf.readInt(); i < max; i++ ) {
            try {
                this.entityTargets.add((Class<? extends Entity>) Class.forName(ByteBufUtils.readUTF8String(buf)));
            } catch( ClassNotFoundException ignored ) { }
        }
        this.playerTargets = new UUID[buf.readInt()];
        for( int i = 0; i < this.playerTargets.length; i++ ) {
            try {
                this.playerTargets[i] = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            } catch( IllegalArgumentException ex ) {
                this.playerTargets[i] = null;
            }
        }
        this.isBlacklistEntity = buf.readBoolean();
        this.isBlacklistPlayer = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretID);
        buf.writeInt(this.entityTargets.size());
        for( Class entityTarget : this.entityTargets ) {
            ByteBufUtils.writeUTF8String(buf, entityTarget.getName());
        }
        buf.writeInt(this.playerTargets.length);
        for( UUID playerTarget : this.playerTargets ) {
            ByteBufUtils.writeUTF8String(buf, playerTarget.toString());
        }
        buf.writeBoolean(this.isBlacklistEntity);
        buf.writeBoolean(this.isBlacklistPlayer);
    }
}
