/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class TmrConfiguration
{
    public static final String CAT_SERVER = "server";

    private static Configuration config;

    public static int glSecondaryTextureUnit = 7;
    public static boolean renderUpgrades = true;
    public static boolean calcForcefieldIntf = true;
    public static boolean useShaders = true;
    public static boolean opCanEditAll = true;
    public static boolean playerCanEditAll = false;

    public static String[] electrolyteAdditRecipes = new String[0];

    public static void initConfiguration(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1.2.0", true);
        MinecraftForge.EVENT_BUS.register(new TmrConfiguration());
        syncConfig();
    }

    public static void syncConfig() {
        String desc;

        desc = "The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING";
        glSecondaryTextureUnit = config.getInt("glSecondaryTextureUnit", Configuration.CATEGORY_CLIENT, glSecondaryTextureUnit, Integer.MIN_VALUE, Integer.MAX_VALUE, desc);

        desc = "Render the upgrades on the turret_placer. Disable this for more performance";
        renderUpgrades = config.getBoolean("renderUpgrades", Configuration.CATEGORY_CLIENT, renderUpgrades, desc);

        desc = "Calculate Interceptions of adjacent forcefields. Disable this to gain a performance boost, but be aware it might clutter the screen if many forcefields are operating.";
        calcForcefieldIntf = config.getBoolean("calcForcefieldIntf", Configuration.CATEGORY_CLIENT, calcForcefieldIntf, desc);

        desc = "Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!";
        useShaders = config.getBoolean("useShaders", Configuration.CATEGORY_CLIENT, useShaders, desc);

        desc = "Whether or not an Operator can manipulate anyones turrets. When disabled, OPs can only edit their own turrets and are treated like everyone else\n" +
                "(the playerCanEditAll option is checked instead).\n" +
                "Ignored in singleplayer.";
        opCanEditAll = config.getBoolean("opCanEditAll", CAT_SERVER, opCanEditAll, desc);

        desc = "Whether or not any player can manipulate anyones turrets. When disabled, players can only edit their own turrets.\n" +
                "Ignored in singleplayer.";
        playerCanEditAll = config.getBoolean("playerCanEditAll", CAT_SERVER, playerCanEditAll, desc);

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
        if( eventArgs.getModID().equals(TmrConstants.ID) ) {
            syncConfig();
        }
    }

    public static final class ConfTurret
    {
        public static final String CAT_TURRET = "turret_values";

        public static float crossbowMaxHealth = 20.0F;
        public static int crossbowMaxAmmoCapacity = 256;
        public static float crossbowProjDamage = 2.0F;
        public static float crossbowProjKnockbackH = 0.01F;
        public static float crossbowProjKnockbackV = 0.2F;
    }
}
