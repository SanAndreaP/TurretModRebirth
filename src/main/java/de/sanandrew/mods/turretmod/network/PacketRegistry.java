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
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketRegistry
{
    public static void initialize() {
        registerMessage(TurretModRebirth.network, PacketUpdateTargets.class, 0, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketUpdateTargets.class, 0, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketUpdateTurretState.class, 1, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketPlayerTurretAction.class, 2, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketSyncPlayerList.class, 3, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketSyncTileEntity.class, 4, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketInitAssemblyCrafting.class, 5, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketAssemblyToggleAutomate.class, 6, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketOpenGui.class, 7, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketOpenGui.class, 7, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketUpdateUgradeSlot.class, 8, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketTurretNaming.class, 9, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketSyncUpgradeInst.class, 10, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketSyncUpgradeInst.class, 10, Side.SERVER);
        registerMessage(TurretModRebirth.network, PacketSyncTcuGuis.class, 11, Side.CLIENT);
        registerMessage(TurretModRebirth.network, PacketSyncAttackTarget.class, 12, Side.CLIENT);
    }

    public static void sendToAllAround(IMessage message, int dim, double x, double y, double z, double range) {
        TurretModRebirth.network.sendToAllAround(message, new NetworkRegistry.TargetPoint(dim, x, y, z, range));
    }

    public static void sendToAllAround(IMessage message, int dim, BlockPos pos, double range) {
        sendToAllAround(message, dim, pos.getX(), pos.getY(), pos.getZ(), range);
    }

    public static void sendToAll(IMessage message) {
        TurretModRebirth.network.sendToAll(message);
    }

    public static void sendToServer(IMessage message) {
        TurretModRebirth.network.sendToServer(message);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        TurretModRebirth.network.sendTo(message, player);
    }

    private static <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(SimpleNetworkWrapper network, Class<T> clazz, int id, Side side) {
        network.registerMessage(clazz, clazz, id, side);
    }
}
