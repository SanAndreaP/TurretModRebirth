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
import de.sanandrew.mods.sanlib.lib.util.config.Init;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
@SuppressWarnings("WeakerAccess")
public final class TmrConfig
{
    public static final String VERSION = "2.0";

    private static Configuration configGeneral;
    private static Configuration configTurrets;
    private static Configuration configProjectiles;

    @Category(Configuration.CATEGORY_CLIENT)
    public static final class Client
    {
        @Value(comment = "Render the upgrades on the turret_placer. Disable this for more performance.")
        public static boolean renderUpgrades = true;
        @Value(comment = "Calculate Interceptions of adjacent forcefields. Disable this to gain a performance boost, but be aware it might clutter the screen if many forcefields are operating.")
        public static boolean calcForcefieldIntf = true;
    }

    @Category("server")
    public static final class Server
    {
        @Value(comment = "Whether or not an Operator can manipulate anyones turrets. When disabled, OPs can only edit their own turrets and are treated like everyone else\n(the playerCanEditAll option is checked instead).\nIgnored in singleplayer.")
        public static boolean opCanEditAll = true;
        @Value(comment = "Whether or not any player can manipulate anyones turrets. When disabled, players can only edit their own turrets.\nIgnored in singleplayer.")
        public static boolean playerCanEditAll = false;
    }

    public static final class Turrets
    {
        public static final String NAME = "turrets";

        @Init
        public static void initialize() {
            ConfigUtils.loadCategory(configTurrets, TurretCrossbow.class, NAME);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initConfiguration(FMLPreInitializationEvent event) {
        File modCfgDir = new File(event.getModConfigurationDirectory(), TmrConstants.ID);
        modCfgDir.mkdirs();
        configGeneral = ConfigUtils.loadConfigFile(new File(modCfgDir, "general.cfg"), VERSION, TmrConstants.NAME);
        configTurrets = ConfigUtils.loadConfigFile(new File(modCfgDir, "turrets.cfg"), VERSION, TmrConstants.NAME);
        configProjectiles = ConfigUtils.loadConfigFile(new File(modCfgDir, "projectiles.cfg"), VERSION, TmrConstants.NAME);
        syncConfig();
    }

    public static void syncConfig() {
        ConfigUtils.loadCategories(configGeneral, TmrConfig.class);
        ConfigUtils.loadCategories(configTurrets, Turrets.class);

        if( configGeneral.hasChanged() ) {
            configGeneral.save();
        }
        if( configTurrets.hasChanged() ) {
            configTurrets.save();
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static Map<String, ConfigCategory[]> getCategoriesForGUI() {
        Map<String, ConfigCategory[]> cat = new HashMap<>();
        cat.put("general", configGeneral.getCategoryNames().stream().map(configGeneral::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));
        cat.put("turrets", configTurrets.getCategoryNames().stream().map(configTurrets::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));

        return cat;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if( eventArgs.getModID().equals(TmrConstants.ID) ) {
            syncConfig();
        }
    }
}
