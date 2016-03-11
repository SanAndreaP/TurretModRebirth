package de.sanandrew.mods.turretmod.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
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
    public static void initialize(SimpleNetworkWrapper network) {
        Utilities.registerMessage(network, PacketUpdateTargets.class, 0, Side.CLIENT);
        Utilities.registerMessage(network, PacketUpdateTargets.class, 0, Side.SERVER);
    }
}
