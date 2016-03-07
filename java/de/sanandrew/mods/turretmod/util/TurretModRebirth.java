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
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TurretModRebirth.ID, version = TurretModRebirth.VERSION, name = "Turret Mod Rebirth")
public class TurretModRebirth
{
    public static final String ID = "sapturretmod";
    public static final String VERSION = "4.0.0-alpha.1";
    public static final Logger LOG = LogManager.getLogger(ID);
//        public static final String MOD_CHANNEL = "SapTurretModNWCH";

        private static final String MOD_PROXY_CLIENT = "de.sanandrew.mods.turretmod.client.util.ClientProxy";
        private static final String MOD_PROXY_COMMON = "de.sanandrew.mods.turretmod.util.CommonProxy";

    @Mod.Instance(TurretModRebirth.ID)
    public static TurretModRebirth instance;
    @SidedProxy(modId = TurretModRebirth.ID, clientSide = TurretModRebirth.MOD_PROXY_CLIENT, serverSide = TurretModRebirth.MOD_PROXY_COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AmmoRegistry.INSTANCE.initialize();
        proxy.preInit(event);
        ItemRegistry.initialize();
//        TmrBlocks.initialize();
//        TmrItems.initialize();
//
//        PacketManager.initialize();
//
//        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
//        TmrEntities.registerEntities();
//
//        TurretRegistry.initialize();
//        TurretUpgradeList.initialize();
//
//        proxy.init();
        proxy.init(event);
    }
}
