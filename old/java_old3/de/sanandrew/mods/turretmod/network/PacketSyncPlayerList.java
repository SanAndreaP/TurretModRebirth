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
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncPlayerList
        extends SimpleMessage
{
    private final Map<UUID, ITextComponent> players;

    @SuppressWarnings("unused")
    public PacketSyncPlayerList() {
        this.players = PlayerList.INSTANCE.getPlayerMap();
    }

    public PacketSyncPlayerList(PacketBuffer packetBuffer) {
        int size = packetBuffer.readVarInt();
        this.players = new HashMap<>(size);

        for( int i = 0; i < size; i++ ) {
            this.players.put(packetBuffer.readUniqueId(), packetBuffer.readTextComponent());
        }
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(this.players.size());
        for( Map.Entry<UUID, ITextComponent> player : this.players.entrySet() ) {
            packetBuffer.writeUniqueId(player.getKey());
            packetBuffer.writeTextComponent(player.getValue());
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new PlayerList.SafePutBuilder(this.players).get());
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
