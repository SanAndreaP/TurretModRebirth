/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.network.packet.PacketRemoteOpenGui;
import de.sanandrew.mods.turretmod.network.packet.PacketTargetList;
import de.sanandrew.mods.turretmod.network.packet.PacketTargetListRequest;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.entity.player.EntityPlayerMP;

public final class PacketManager
{
    public static final short TURRET_TARGET_SYNC = 0;
    public static final short TURRET_TARGET_SYNC_REQUEST = 1;
    public static final short OPEN_CLIENT_GUI = 2;

    public static void initialize() {
        NetworkManager.registerModHandler(TurretMod.MOD_ID, TurretMod.MOD_CHANNEL);

        NetworkManager.registerModPacketCls(TurretMod.MOD_ID, TURRET_TARGET_SYNC, PacketTargetList.class);
        NetworkManager.registerModPacketCls(TurretMod.MOD_ID, TURRET_TARGET_SYNC_REQUEST, PacketTargetListRequest.class);
        NetworkManager.registerModPacketCls(TurretMod.MOD_ID, OPEN_CLIENT_GUI, PacketRemoteOpenGui.class);
    }

    public static void sendToServer(short packet, Tuple data) {
        NetworkManager.sendToServer(TurretMod.MOD_ID, packet, data);
    }

    public static void sendToAll(short packed, Tuple data) {
        NetworkManager.sendToAll(TurretMod.MOD_ID, packed, data);
    }

    public static void sendToPlayer(short packed, EntityPlayerMP player, Tuple data) {
        NetworkManager.sendToPlayer(TurretMod.MOD_ID, packed, player, data);
    }

    public static void sendToAllInDimension(short packed, int dimensionId, Tuple data) {
        NetworkManager.sendToAllInDimension(TurretMod.MOD_ID, packed, dimensionId, data);
    }

    public static void sendToAllAround(short packed, int dimensionId, double x, double y, double z, double range, Tuple data) {
        NetworkManager.sendToAllAround(TurretMod.MOD_ID, packed, dimensionId, x, y, z, range, data);
    }
}
