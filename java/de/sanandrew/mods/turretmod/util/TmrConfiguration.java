/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public final class TmrConfiguration
{
    public static final String CAT_CLIENT = "Client";

    private static Configuration config;

    public static int glSecondaryTextureUnit = 7;
    public static boolean renderUpgrades = true;
    public static boolean calcForcefieldIntf = true;
    public static boolean useShaders = true;

    public static void initConfiguration(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1.0.1", true);
        MinecraftForge.EVENT_BUS.register(new TmrConfiguration());
        syncConfig();
    }

    public static void syncConfig() {
        String desc;

        desc = "The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING";
        glSecondaryTextureUnit = config.getInt("glSecondaryTextureUnit", CAT_CLIENT, glSecondaryTextureUnit, Integer.MIN_VALUE, Integer.MAX_VALUE, desc);

        desc = "Render the upgrades on the turret. Disable this for more performance";
        renderUpgrades = config.getBoolean("renderUpgrades", CAT_CLIENT, renderUpgrades, desc);

        desc = "Calculate Interceptions of adjacent forcefields. Disable this to gain a performance boost, but be aware it might clutter the screen if many forcefields are operating.";
        calcForcefieldIntf = config.getBoolean("calcForcefieldIntf", CAT_CLIENT, calcForcefieldIntf, desc);

        desc = "Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!";
        useShaders = config.getBoolean("useShaders", CAT_CLIENT, useShaders, desc);

        if( config.hasChanged() ) {
            config.save();
        }
    }

    public static ConfigCategory getCategory(String category) {
        return config.getCategory(category);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if( eventArgs.getModID().equals(TurretModRebirth.ID) ) {
            syncConfig();
        }
    }
}
