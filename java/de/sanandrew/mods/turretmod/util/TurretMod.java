/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import de.sanandrew.mods.turretmod.network.PacketManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TurretMod.MOD_ID, version = TurretMod.VERSION, name = "Turret Mod Rebirth")
public class TurretMod
{
    public static final String MOD_ID = "sapturretmod";
    public static final String VERSION = "4.0.0";
    public static final Logger MOD_LOG = LogManager.getLogger(MOD_ID);
    public static final String MOD_CHANNEL = "SapTurretModNWCH";

    private static final String MOD_PROXY_CLIENT = "de.sanandrew.mods.turretmod.client.util.ClientProxy";
    private static final String MOD_PROXY_COMMON = "de.sanandrew.mods.turretmod.util.CommonProxy";

    @Instance(TurretMod.MOD_ID)
    public static TurretMod instance;
    @SidedProxy(modId = TurretMod.MOD_ID, clientSide = TurretMod.MOD_PROXY_CLIENT, serverSide = TurretMod.MOD_PROXY_COMMON)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TmrItems.initialize();

        PacketManager.initialize();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        TmrEntities.registerEntities();

        TurretRegistry.initialize();

        proxy.init();
    }
}
