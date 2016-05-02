/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import de.sanandrew.mods.turretmod.util.PlayerList;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketSyncPlayerList
        extends AbstractMessage<PacketSyncPlayerList>
{
    private Map<UUID, String> players;

    public PacketSyncPlayerList() { }

    public PacketSyncPlayerList(PlayerList pList) {
        this.players = pList.getPlayerMap();
    }

    @Override
    public void handleClientMessage(PacketSyncPlayerList packet, EntityPlayer player) {
        PlayerList.INSTANCE.putPlayersClient(packet.players);
    }

    @Override
    public void handleServerMessage(PacketSyncPlayerList packet, EntityPlayer player) { }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        this.players = new HashMap<>(size);

        for( int i = 0; i < size; i++ ) {
            this.players.put(UUID.fromString(ByteBufUtils.readUTF8String(buf)), ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.players.size());
        for( Map.Entry<UUID, String> player : this.players.entrySet() ) {
            ByteBufUtils.writeUTF8String(buf, player.getKey().toString());
            ByteBufUtils.writeUTF8String(buf, player.getValue());
        }
    }
}
