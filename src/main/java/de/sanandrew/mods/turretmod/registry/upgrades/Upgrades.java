/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.UpgradeShieldPersonal;
import de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting.UpgradeSmartTargeting;

import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class Upgrades
{
    public static final UUID UPG_STORAGE_I = UUID.fromString("1749478F-2A8E-4C56-BC03-6C76CB5DE921");
    public static final UUID UPG_STORAGE_II = UUID.fromString("DEFFE281-A2F5-488A-95C1-E9A3BB6E0DD1");
    public static final UUID UPG_STORAGE_III = UUID.fromString("50DB1AC3-1CCD-4CB0-AD5A-0777C548655D");
    public static final UUID AMMO_STORAGE = UUID.fromString("2C850D81-0C01-47EA-B3AD-86E4FF523521");
    public static final UUID HEALTH_I = UUID.fromString("13218AB7-3DA6-461D-9882-13482291164B");
    public static final UUID HEALTH_II = UUID.fromString("612A78CB-ED0C-4990-B1F3-041BE8171B1A");
    public static final UUID HEALTH_III = UUID.fromString("2239A7BB-DD38-4764-9FFC-6E04934F9B3C");
    public static final UUID HEALTH_IV = UUID.fromString("FF6CC60F-EEC7-40C5-92D8-A614DFA06777");
    public static final UUID RELOAD_I = UUID.fromString("4ED4E813-E2D8-43E9-B499-9911E214C5E9");
    public static final UUID RELOAD_II = UUID.fromString("80877F84-F03D-4ED8-A9D3-BAF6DF4F3BF1");
    public static final UUID SMART_TGT = UUID.fromString("12435AB9-5AA3-4DB9-9B76-7943BA71597A");
    public static final UUID ECONOMY_I = UUID.fromString("A8F29058-C8B7-400D-A7F4-4CEDE627A7E8");
    public static final UUID ECONOMY_II = UUID.fromString("2A76A2EB-0EA3-4EB0-9EC2-61E579361306");
    public static final UUID ECONOMY_INF = UUID.fromString("C3CF3EE9-8314-4766-A5E0-6033DB3EE9DB");
    public static final UUID ENDER_MEDIUM = UUID.fromString("0ED3D861-F11D-4F6B-B9FC-67E22C8EB538");
    public static final UUID FUEL_PURIFY = UUID.fromString("677FA826-DA2D-40E9-9D86-7FAD7DE398CC");
    public static final UUID SHIELD_PERSONAL = UUID.fromString("90F61412-4ECC-431B-A6AC-288F26C37608");
    public static final UUID SHIELD_PROJECTILE = UUID.fromString("AB5E19F9-C241-4F3C-B04E-6C276369B0CF");
    public static final UUID SHIELD_EXPLOSIVE = UUID.fromString("853DB6B1-EAEF-4175-B1EE-02F765D24D25");
    public static final UUID SHIELD_STRENGTH_I = UUID.fromString("C03BFDDA-1415-4519-BE59-61C568B6345E");
    public static final UUID SHIELD_STRENGTH_II = UUID.fromString("EF8BF1BB-437E-491D-AD6A-03F807987FAE");

    public static void initialize(IUpgradeRegistry registry) {
        registry.registerUpgrade(UPG_STORAGE_I, new UpgradeUpgStorage.UpgradeStorageMK1());
        registry.registerUpgrade(UPG_STORAGE_II, new UpgradeUpgStorage.UpgradeStorageMK2());
        registry.registerUpgrade(UPG_STORAGE_III, new UpgradeUpgStorage.UpgradeStorageMK3());
        registry.registerUpgrade(AMMO_STORAGE, new UpgradeAmmoStorage());
        registry.registerUpgrade(HEALTH_I, new UpgradeHealth.UpgradeHealthMK1());
        registry.registerUpgrade(HEALTH_II, new UpgradeHealth.UpgradeHealthMK2());
        registry.registerUpgrade(HEALTH_III, new UpgradeHealth.UpgradeHealthMK3());
        registry.registerUpgrade(HEALTH_IV, new UpgradeHealth.UpgradeHealthMK4());
        registry.registerUpgrade(RELOAD_I, new UpgradeReloadTime.UpgradeReloadTimeMK1());
        registry.registerUpgrade(RELOAD_II, new UpgradeReloadTime.UpgradeReloadTimeMK2());
        registry.registerUpgrade(SMART_TGT, new UpgradeSmartTargeting());
        registry.registerUpgrade(ECONOMY_I, new UpgradeAmmoUsage.UpgradeAmmoUseI());
        registry.registerUpgrade(ECONOMY_II, new UpgradeAmmoUsage.UpgradeAmmoUseII());
        registry.registerUpgrade(ECONOMY_INF, new UpgradeAmmoUsage.UpgradeAmmoUseInf());
        registry.registerUpgrade(ENDER_MEDIUM, new UpgradeBasic("ender_medium", Turrets.LASER));
        registry.registerUpgrade(FUEL_PURIFY, new UpgradeBasic("fuel_purifier", Turrets.FLAMETHROWER));
        registry.registerUpgrade(SHIELD_PERSONAL, new UpgradeShieldPersonal());
        registry.registerUpgrade(SHIELD_PROJECTILE, new UpgradeBasic("shield_projectile", Turrets.SHIELDGEN));
        registry.registerUpgrade(SHIELD_EXPLOSIVE, new UpgradeBasic("shield_explosive", Turrets.SHIELDGEN));
        registry.registerUpgrade(SHIELD_STRENGTH_I, new UpgradeBasic("shield_strength_i", Turrets.SHIELDGEN));
        registry.registerUpgrade(SHIELD_STRENGTH_II, new UpgradeBasic("shield_strength_ii", registry.getUpgrade(Upgrades.SHIELD_STRENGTH_I), Turrets.SHIELDGEN));
    }
}
