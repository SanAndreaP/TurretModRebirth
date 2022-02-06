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
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncPlayerListPacket
        extends SimpleMessage
{
    private final Map<UUID, PlayerList.PlayerData> players;

    public SyncPlayerListPacket() {
        this.players = PlayerList.getPlayerMap();
    }

    public SyncPlayerListPacket(PacketBuffer packetBuffer) {
        int size = packetBuffer.readVarInt();
        this.players = new HashMap<>(size);

        for( int i = 0; i < size; i++ ) {
            this.players.put(packetBuffer.readUUID(), new PlayerList.PlayerData(packetBuffer.readComponent()));
        }
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(this.players.size());
        for( Map.Entry<UUID, PlayerList.PlayerData> player : this.players.entrySet() ) {
            packetBuffer.writeUUID(player.getKey());
            packetBuffer.writeComponent(player.getValue().name);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        TurretModRebirth.PROXY.fillPlayerListClient(this.players);
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
