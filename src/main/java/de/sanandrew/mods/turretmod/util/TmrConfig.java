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
import de.sanandrew.mods.turretmod.entity.turret.TargetList;
import de.sanandrew.mods.turretmod.registry.projectile.Bullet;
import de.sanandrew.mods.turretmod.registry.projectile.CrossbowBolt;
import de.sanandrew.mods.turretmod.registry.projectile.CryoBall;
import de.sanandrew.mods.turretmod.registry.projectile.Flame;
import de.sanandrew.mods.turretmod.registry.projectile.Laser;
import de.sanandrew.mods.turretmod.registry.projectile.MinigunPebble;
import de.sanandrew.mods.turretmod.registry.projectile.ShotgunPebble;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
import de.sanandrew.mods.turretmod.registry.turret.TurretCryolator;
import de.sanandrew.mods.turretmod.registry.turret.TurretFlamethrower;
import de.sanandrew.mods.turretmod.registry.turret.TurretLaser;
import de.sanandrew.mods.turretmod.registry.turret.TurretMinigun;
import de.sanandrew.mods.turretmod.registry.turret.TurretRevolver;
import de.sanandrew.mods.turretmod.registry.turret.TurretShotgun;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.TurretForcefield;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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
    private static Configuration configTargets;
    public static Configuration configUpgrades;

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

    @Value(comment = "Whether or not progression based crafting is activated (not available yet)")
    public static boolean doProgression = true;

    public static final class Turrets
    {
        @Init
        public static void initialize() {
            ConfigUtils.loadCategory(configTurrets, TurretCrossbow.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretShotgun.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretCryolator.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretRevolver.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretMinigun.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretForcefield.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretLaser.class, null);
            ConfigUtils.loadCategory(configTurrets, TurretFlamethrower.class, null);
        }
    }

    public static final class Projectiles
    {
        @Init
        public static void initialize() {
            ConfigUtils.loadCategory(configProjectiles, CrossbowBolt.class, null);
            ConfigUtils.loadCategory(configProjectiles, ShotgunPebble.class, null);
            ConfigUtils.loadCategory(configProjectiles, CryoBall.class, null);
            ConfigUtils.loadCategory(configProjectiles, Bullet.class, null);
            ConfigUtils.loadCategory(configProjectiles, MinigunPebble.class, null);
            ConfigUtils.loadCategory(configProjectiles, Laser.class, null);
            ConfigUtils.loadCategory(configProjectiles, Flame.class, null);
        }
    }

    public static final class Targets
    {
        @Init
        public static void initialize() {
            ConfigUtils.loadCategory(configTargets, TargetList.class, null);
        }

        public static void reset() {
            Property p = configTargets.getCategory(TargetList.NAME).get("groundRegenerate");
            p.set(false);
            p.setDefaultValue(false);

            p = configTargets.getCategory(TargetList.NAME).get("groundEntities");
            p.set(TargetList.groundEntities);
            p.setDefaultValues(TargetList.groundEntities);

            configTargets.save();
        }
    }

    public static final class Upgrades
    {
        @Init
        public static void initialize() {
            ConfigUtils.loadCategory(configUpgrades, LevelStorage.class, null);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initConfiguration(FMLPreInitializationEvent event) {
        File modCfgDir = new File(event.getModConfigurationDirectory(), TmrConstants.ID);
        modCfgDir.mkdirs();
        configGeneral = ConfigUtils.loadConfigFile(new File(modCfgDir, "general.cfg"), VERSION, TmrConstants.NAME);
        configTurrets = ConfigUtils.loadConfigFile(new File(modCfgDir, "turrets.cfg"), VERSION, TmrConstants.NAME);
        configProjectiles = ConfigUtils.loadConfigFile(new File(modCfgDir, "projectiles.cfg"), VERSION, TmrConstants.NAME);
        configTargets = ConfigUtils.loadConfigFile(new File(modCfgDir, "targets.cfg"), VERSION, TmrConstants.NAME);
        configUpgrades = ConfigUtils.loadConfigFile(new File(modCfgDir, "upgrades.cfg"), VERSION, TmrConstants.NAME);
        syncConfig();
    }

    public static void syncConfig() {
        ConfigUtils.loadCategories(configGeneral, TmrConfig.class);
        ConfigUtils.loadCategories(configTurrets, Turrets.class);
        ConfigUtils.loadCategories(configProjectiles, Projectiles.class);
        ConfigUtils.loadCategories(configTargets, Targets.class);
        ConfigUtils.loadCategories(configUpgrades, Upgrades.class);

        if( configGeneral.hasChanged() ) {
            configGeneral.save();
        }
        if( configTurrets.hasChanged() ) {
            configTurrets.save();
        }
        if( configProjectiles.hasChanged() ) {
            configProjectiles.save();
        }
        if( configTargets.hasChanged() ) {
            configTargets.save();
        }
        if( configUpgrades.hasChanged() ) {
            configUpgrades.save();
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static Map<String, ConfigCategory[]> getCategoriesForGUI() {
        Map<String, ConfigCategory[]> cat = new HashMap<>();
        cat.put("general", configGeneral.getCategoryNames().stream().map(configGeneral::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));
        cat.put("turrets", configTurrets.getCategoryNames().stream().map(configTurrets::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));
        cat.put("projectiles", configProjectiles.getCategoryNames().stream().map(configProjectiles::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));
        cat.put("targets", configTargets.getCategoryNames().stream().map(configTargets::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));
        cat.put("upgrades", configUpgrades.getCategoryNames().stream().map(configUpgrades::getCategory).filter(c -> c.size() > 0).toArray(ConfigCategory[]::new));

        return cat;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if( eventArgs.getModID().equals(TmrConstants.ID) ) {
            syncConfig();
            TargetList.initializePostInit();
        }
    }
}
