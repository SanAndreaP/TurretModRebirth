/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class TmrConfiguration
{
    public static final String CAT_CLIENT = "Client";

    private static Configuration config;

    public static int glSecondaryTextureUnit = 7;
    public static boolean renderUpgrades = true;
    public static boolean calcForcefieldIntf = true;
    public static boolean useShaders = true;

    public static String[] electrolyteAdditRecipes = new String[0];

    public static void initConfiguration(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1.1.0", true);
        MinecraftForge.EVENT_BUS.register(new TmrConfiguration());
        syncConfig();
    }

    public static void syncConfig() {
        String desc;

        desc = "The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING";
        glSecondaryTextureUnit = config.getInt("glSecondaryTextureUnit", CAT_CLIENT, glSecondaryTextureUnit, Integer.MIN_VALUE, Integer.MAX_VALUE, desc);

        desc = "Render the upgrades on the turret_placer. Disable this for more performance";
        renderUpgrades = config.getBoolean("renderUpgrades", CAT_CLIENT, renderUpgrades, desc);

        desc = "Calculate Interceptions of adjacent forcefields. Disable this to gain a performance boost, but be aware it might clutter the screen if many forcefields are operating.";
        calcForcefieldIntf = config.getBoolean("calcForcefieldIntf", CAT_CLIENT, calcForcefieldIntf, desc);

        desc = "Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!";
        useShaders = config.getBoolean("useShaders", CAT_CLIENT, useShaders, desc);

        desc = "A list of items and values for the electrolyte generator to be able to use.\n" +
                "An example of an entry would be: <minecraft:stick>, 2.0, 500, <minecraft:apple>, <minecraft:diamond>\n" +
                "where <minecraft:stick> is the item used\n" +
                "2.0 the efficiency as a floating point number\n" +
                "500 the ticks it takes to decay\n" +
                "<minecraft:apple> the \"trash\" result and\n" +
                "<minecraft:diamond> the \"treasure\" result";
        electrolyteAdditRecipes = config.getStringList("electrolyteItems", Configuration.CATEGORY_GENERAL, electrolyteAdditRecipes, desc);

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
