/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgradeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TurretMod.MOD_ID, version = TurretMod.VERSION, name = "Turret Mod Rebirth")
public class TurretMod
{
    public static final String MOD_ID = "sapturretmod";
    public static final String VERSION = "4.0.0-alpha.1";
    public static final Logger MOD_LOG = LogManager.getLogger(MOD_ID);
    public static final String MOD_CHANNEL = "SapTurretModNWCH";

    private static final String MOD_PROXY_CLIENT = "de.sanandrew.mods.turretmod.client.util.ClientProxy";
    private static final String MOD_PROXY_COMMON = "de.sanandrew.mods.turretmod.util.CommonProxy";
    private static final String PARTICLE_PROXY_CLIENT = "de.sanandrew.mods.turretmod.client.util.ClientParticleProxy";
    private static final String PARTICLE_PROXY_COMMON = "de.sanandrew.mods.turretmod.util.ParticleProxy";

    @Instance(TurretMod.MOD_ID)
    public static TurretMod instance;
    @SidedProxy(modId = TurretMod.MOD_ID, clientSide = TurretMod.MOD_PROXY_CLIENT, serverSide = TurretMod.MOD_PROXY_COMMON)
    public static CommonProxy proxy;
    @SidedProxy(modId = TurretMod.MOD_ID, clientSide = TurretMod.PARTICLE_PROXY_CLIENT, serverSide = TurretMod.PARTICLE_PROXY_COMMON)
    public static ParticleProxy particleProxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TmrBlocks.initialize();
        TmrItems.initialize();

        PacketManager.initialize();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        TmrEntities.registerEntities();

        TurretRegistry.initialize();
        TurretUpgradeList.initialize();

        proxy.init();
    }
}
