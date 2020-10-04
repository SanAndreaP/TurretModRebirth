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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Arrays;
import java.util.UUID;

public class PacketUpdateTargets
        extends AbstractMessage<PacketUpdateTargets>
{
    private PacketSubtype    type;
    private boolean            toggle;
    private ResourceLocation[] entityIds;
    private UUID[]             playerIds;
    private int                turretId;

    public static PacketUpdateTargets updateTarget(ITurretInst turretInst, ResourceLocation entityId, boolean enabled) {
        PacketUpdateTargets p = new PacketUpdateTargets(turretInst, PacketSubtype.ENTITY_ID, enabled);
        p.entityIds = new ResourceLocation[] { entityId };

        return p;
    }

    public static PacketUpdateTargets updateTarget(ITurretInst turretInst, UUID playerId, boolean enabled) {
        PacketUpdateTargets p = new PacketUpdateTargets(turretInst, PacketSubtype.PLAYER_ID, enabled);
        p.playerIds = new UUID[] { playerId };

        return p;
    }

    public static PacketUpdateTargets updateTargets(ITurretInst turretInst, ResourceLocation[] entityIds, boolean enabled) {
        PacketUpdateTargets p = new PacketUpdateTargets(turretInst, PacketSubtype.ENTITY_ID, enabled);
        p.entityIds = entityIds;

        return p;
    }

    public static PacketUpdateTargets updateTargets(ITurretInst turretInst, UUID[] playerIds, boolean enabled) {
        PacketUpdateTargets p = new PacketUpdateTargets(turretInst, PacketSubtype.PLAYER_ID, enabled);
        p.playerIds = playerIds;

        return p;
    }

    public static PacketUpdateTargets updateEntityBlacklist(ITurretInst turretInst, boolean isBlacklist) {
        return new PacketUpdateTargets(turretInst, PacketSubtype.BLACKLIST_ENTITY, isBlacklist);
    }

    public static PacketUpdateTargets updatePlayerBlacklist(ITurretInst turretInst, boolean isBlacklist) {
        return new PacketUpdateTargets(turretInst, PacketSubtype.BLACKLIST_PLAYER, isBlacklist);
    }

    @SuppressWarnings("unused")
    public PacketUpdateTargets() {
        this.type = PacketSubtype.UNKNOWN;
    }

    private PacketUpdateTargets(ITurretInst turretInst, PacketSubtype type, boolean toggle) {
        this.type = type;
        this.toggle = toggle;
        this.turretId = turretInst.get().getEntityId();
    }

    @Override
    public void handleClientMessage(PacketUpdateTargets packet, EntityPlayer player) {
        this.handleServerMessage(packet, player);
    }

    @Override
    public void handleServerMessage(PacketUpdateTargets packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretId);
        if( e instanceof ITurretInst ) {
            ITargetProcessor processor = ((ITurretInst) e).getTargetProcessor();
            switch( packet.type ) {
                case ENTITY_ID:
                    Arrays.stream(packet.entityIds).forEach(id -> processor.updateEntityTarget(id, packet.toggle));
                    break;
                case PLAYER_ID:
                    Arrays.stream(packet.playerIds).forEach(id -> processor.updatePlayerTarget(id, packet.toggle));
                    break;
                case BLACKLIST_ENTITY:
                    processor.setEntityBlacklist(packet.toggle);
                    break;
                case BLACKLIST_PLAYER:
                    processor.setPlayerBlacklist(packet.toggle);
                    break;
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.type = PacketSubtype.getType(buf.readByte());

        switch( this.type ) {
            case ENTITY_ID:
                this.entityIds = new ResourceLocation[buf.readInt()];
                for( int i = 0; i < this.entityIds.length; i++ ) { this.entityIds[i] = new ResourceLocation(ByteBufUtils.readUTF8String(buf)); }
                this.toggle = buf.readBoolean();
                break;
            case PLAYER_ID:
                this.playerIds = new UUID[buf.readInt()];
                for( int i = 0; i < this.playerIds.length; i++ ) { this.playerIds[i] = UUID.fromString(ByteBufUtils.readUTF8String(buf)); }
                this.toggle = buf.readBoolean();
                break;
            case BLACKLIST_ENTITY:
            case BLACKLIST_PLAYER:
                this.toggle = buf.readBoolean();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeByte(this.type.id);
        switch( this.type ) {
            case ENTITY_ID:
                buf.writeInt(this.entityIds.length);
                Arrays.stream(this.entityIds).forEach(e -> ByteBufUtils.writeUTF8String(buf, e.toString()));
                buf.writeBoolean(this.toggle);
                break;
            case PLAYER_ID:
                buf.writeInt(this.playerIds.length);
                Arrays.stream(this.playerIds).forEach(e -> ByteBufUtils.writeUTF8String(buf, e.toString()));
                buf.writeBoolean(this.toggle);
                break;
            case BLACKLIST_ENTITY:
            case BLACKLIST_PLAYER:
                buf.writeBoolean(this.toggle);
                break;
        }
    }

    enum PacketSubtype
    {
        UNKNOWN,
        ENTITY_ID,
        PLAYER_ID,
        BLACKLIST_ENTITY,
        BLACKLIST_PLAYER;

        private static final PacketSubtype[] VALUES = values();

        final byte id = (byte) this.ordinal();

        static PacketSubtype getType(int id) {
            if( id >= 0 && id < VALUES.length ) {
                return VALUES[id];
            }

            return UNKNOWN;
        }
    }
}
