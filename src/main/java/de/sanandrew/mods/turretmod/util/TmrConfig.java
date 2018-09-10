/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.ConfigUtils;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public final class TmrConfig
{
    public static final String VERSION = "2.0";
    public static final String CAT_SERVER = "server";

    private static Configuration config;

    @Value(category = Configuration.CATEGORY_CLIENT, comment = "The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING!")
    public static int glSecondaryTextureUnit = 7;
    @Value(category = Configuration.CATEGORY_CLIENT, comment = "Render the upgrades on the turret_placer. Disable this for more performance.")
    public static boolean renderUpgrades = true;
    @Value(category = Configuration.CATEGORY_CLIENT, comment = "Calculate Interceptions of adjacent forcefields. Disable this to gain a performance boost, but be aware it might clutter the screen if many forcefields are operating.")
    public static boolean calcForcefieldIntf = true;
    @Value(category = Configuration.CATEGORY_CLIENT, comment = "Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!")
    public static boolean useShaders = true;
    @Value(category = CAT_SERVER, comment = "Whether or not an Operator can manipulate anyones turrets. When disabled, OPs can only edit their own turrets and are treated like everyone else\n(the playerCanEditAll option is checked instead).\nIgnored in singleplayer.")
    public static boolean opCanEditAll = true;
    @Value(category = CAT_SERVER, comment = "Whether or not any player can manipulate anyones turrets. When disabled, players can only edit their own turrets.\nIgnored in singleplayer.")
    public static boolean playerCanEditAll = false;

    @Value(category = Configuration.CATEGORY_GENERAL, comment = "A list of items and values for the electrolyte generator to be able to use.\nAn example of an entry would be: <minecraft:stick>, 2.0, 500, <minecraft:apple>, <minecraft:diamond>\nwhere <minecraft:stick> is the item used\n2.0 the efficiency as a floating point number\n500 the ticks it takes to decay\n<minecraft:apple> the \"trash\" result and\n<minecraft:diamond> the \"treasure\" result")
    public static String[] electrolyteAdditRecipes = new String[0];

    public static void initConfiguration(FMLPreInitializationEvent event) {
        config = ConfigUtils.loadConfigFile(event.getSuggestedConfigurationFile(), VERSION, TmrConstants.NAME);
        syncConfig();
    }

    public static void syncConfig() {
        ConfigUtils.loadCategories(config, TmrConfig.class);

        if( config.hasChanged() ) {
            config.save();
        }
    }

    public static ConfigCategory getCategory(String category) {
        return config.getCategory(category);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if( eventArgs.getModID().equals(TmrConstants.ID) ) {
            syncConfig();
        }
    }

    @Category(Turrets.NAME)
    public static final class Turrets
    {
        public static final String NAME = "Turrets";

        public static void init() {
            ConfigUtils.loadCategory(config, TurretCrossbow.class, NAME);
        }
    }
}
