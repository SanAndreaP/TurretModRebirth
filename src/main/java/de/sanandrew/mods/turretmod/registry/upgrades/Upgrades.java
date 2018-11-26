/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.UpgradeShieldColorizer;
import de.sanandrew.mods.turretmod.registry.upgrades.shield.UpgradeShieldPersonal;
import de.sanandrew.mods.turretmod.registry.upgrades.smarttargeting.SmartTargeting;

@SuppressWarnings("WeakerAccess")
public class Upgrades
{
    public static final IUpgrade UPG_STORAGE_I = new SimpleUpgrade("upgstorage.1");
    public static final IUpgrade UPG_STORAGE_II = new SimpleUpgrade("upgstorage.2", UPG_STORAGE_I);
    public static final IUpgrade UPG_STORAGE_III = new SimpleUpgrade("upgstorage.3", UPG_STORAGE_II);
    public static final IUpgrade AMMO_STORAGE = new AmmoStorage();
    public static final IUpgrade HEALTH_I = new Health.MK1();
    public static final IUpgrade HEALTH_II = new Health.MK2();
    public static final IUpgrade HEALTH_III = new Health.MK3();
    public static final IUpgrade HEALTH_IV = new Health.MK4();
    public static final IUpgrade RELOAD_I = new ReloadTime.MK1();
    public static final IUpgrade RELOAD_II = new ReloadTime.MK2();
    public static final IUpgrade SMART_TGT = new SmartTargeting();
    public static final IUpgrade ECONOMY_I = new AmmoUsage.MK1();
    public static final IUpgrade ECONOMY_II = new AmmoUsage.MK2();
    public static final IUpgrade ECONOMY_INF = new AmmoUsage.MKInf();
    public static final IUpgrade ENDER_MEDIUM = new SimpleUpgrade("endermedium", Turrets.LASER);
    public static final IUpgrade FUEL_PURIFY = new SimpleUpgrade("fuelpurifier", Turrets.FLAMETHROWER);
    public static final IUpgrade SHIELD_PERSONAL = new UpgradeShieldPersonal();
    public static final IUpgrade SHIELD_PROJECTILE = new SimpleUpgrade("shield.projectile", Turrets.FORCEFIELD);
    public static final IUpgrade SHIELD_EXPLOSIVE =  new SimpleUpgrade("shield.explosive", Turrets.FORCEFIELD);
    public static final IUpgrade SHIELD_STRENGTH_I = new SimpleUpgrade("shield.strength.1", Turrets.FORCEFIELD);
    public static final IUpgrade SHIELD_STRENGTH_II = new SimpleUpgrade("shield.strength.2", SHIELD_STRENGTH_I, Turrets.FORCEFIELD);
    public static final IUpgrade SHIELD_COLORIZER =  new UpgradeShieldColorizer();
    public static final IUpgrade ENDER_TOXIN_I = new SimpleUpgrade("endertoxin.1");
    public static final IUpgrade ENDER_TOXIN_II =  new SimpleUpgrade("endertoxin.2", ENDER_TOXIN_I);

    public static void initialize(IUpgradeRegistry registry) {
        registry.registerAll(UPG_STORAGE_I, UPG_STORAGE_II, UPG_STORAGE_III, AMMO_STORAGE, HEALTH_I, HEALTH_II, HEALTH_III, HEALTH_IV,
                             RELOAD_I, RELOAD_II, SMART_TGT, ECONOMY_I, ECONOMY_II, ECONOMY_INF, ENDER_MEDIUM, FUEL_PURIFY,
                             SHIELD_PERSONAL, SHIELD_PROJECTILE, SHIELD_EXPLOSIVE, SHIELD_STRENGTH_I, SHIELD_STRENGTH_II,
                             SHIELD_COLORIZER, ENDER_TOXIN_I, ENDER_TOXIN_II);
    }
}
