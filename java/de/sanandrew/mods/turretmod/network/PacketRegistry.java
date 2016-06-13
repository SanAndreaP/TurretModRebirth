package de.sanandrew.mods.turretmod.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
public class PacketRegistry
{
    public static void initialize() {
        Utilities.registerMessage(TurretModRebirth.network, PacketUpdateTargets.class, 0, Side.CLIENT);
        Utilities.registerMessage(TurretModRebirth.network, PacketUpdateTargets.class, 0, Side.SERVER);
        Utilities.registerMessage(TurretModRebirth.network, PacketUpdateTurretState.class, 1, Side.CLIENT);
        Utilities.registerMessage(TurretModRebirth.network, PacketPlayerTurretAction.class, 2, Side.SERVER);
        Utilities.registerMessage(TurretModRebirth.network, PacketSyncPlayerList.class, 3, Side.CLIENT);
        Utilities.registerMessage(TurretModRebirth.network, PacketSyncTileEntity.class, 4, Side.CLIENT);
        Utilities.registerMessage(TurretModRebirth.network, PacketInitAssemblyCrafting.class, 5, Side.SERVER);
        Utilities.registerMessage(TurretModRebirth.network, PacketAssemblyToggleAutomate.class, 6, Side.SERVER);
        Utilities.registerMessage(TurretModRebirth.network, PacketOpenGui.class, 7, Side.CLIENT);
        Utilities.registerMessage(TurretModRebirth.network, PacketOpenGui.class, 7, Side.SERVER);
        Utilities.registerMessage(TurretModRebirth.network, PacketUpdateUgradeSlot.class, 8, Side.CLIENT);
    }

    public static void sendToAllAround(IMessage message, int dim, double x, double y, double z, double range) {
        TurretModRebirth.network.sendToAllAround(message, new NetworkRegistry.TargetPoint(dim, x, y, z, range));
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
}
