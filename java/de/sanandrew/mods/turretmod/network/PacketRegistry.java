package de.sanandrew.mods.turretmod.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.util.Utilities;

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
    }

    public static void sendToAllAround(IMessage message, int dim, double x, double y, double z, double range) {
        TurretModRebirth.network.sendToAllAround(message, new NetworkRegistry.TargetPoint(dim, x, y, z, range));
    }
}
