package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.SimpleMessage;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncTurretTargetsPacket
        extends SimpleMessage
{
    private final int turretId;

    private final Map<ResourceLocation, Boolean> creatureTargets;
    private final Map<UUID, Boolean>             playerTargets;

    private final boolean isCreatureDenyList;
    private final boolean isPlayerDenyList;

    public SyncTurretTargetsPacket(TargetProcessor tgtProc) {
        this.turretId = tgtProc.getTurret().get().getId();
        this.creatureTargets = tgtProc.grabUpdatedCreatures();
        this.playerTargets = tgtProc.grabUpdatedPlayers();
        this.isCreatureDenyList = tgtProc.isEntityDenyList();
        this.isPlayerDenyList = tgtProc.isPlayerDenyList();
    }

    public SyncTurretTargetsPacket(PacketBuffer buffer) {
        this.turretId = buffer.readInt();
        this.creatureTargets = new HashMap<>();
        for( int i = 0, max = buffer.readInt(); i < max; i++ ) {
            this.creatureTargets.put(buffer.readResourceLocation(), buffer.readBoolean());
        }
        this.playerTargets = new HashMap<>();
        for( int i = 0, max = buffer.readInt(); i < max; i++ ) {
            this.playerTargets.put(buffer.readUUID(), buffer.readBoolean());
        }
        this.isCreatureDenyList = buffer.readBoolean();
        this.isPlayerDenyList = buffer.readBoolean();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.turretId);
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
        buffer.writeBoolean(this.isCreatureDenyList);
        buffer.writeBoolean(this.isPlayerDenyList);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        PlayerEntity player = TurretModRebirth.PROXY.getNetworkPlayer(supplier);
        if( player != null ) {
            Entity e = player.level.getEntity(this.turretId);
            if( e instanceof ITurretEntity ) {
                ((TargetProcessor) ((ITurretEntity) e).getTargetProcessor()).updateClientTargets(this.creatureTargets, this.playerTargets,
                                                                                                 this.isCreatureDenyList, this.isPlayerDenyList);
            }
        }
    }

    @Override
    public boolean handleOnMainThread() {
        return true;
    }
}
