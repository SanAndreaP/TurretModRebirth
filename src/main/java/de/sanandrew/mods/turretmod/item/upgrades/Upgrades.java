/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item.upgrades;

import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.AmmoStorage;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.Creative;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.Economy;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.Health;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.ReloadTime;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.SimpleUpgrade;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.Leveling;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.shield.PersonalShield;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.smarttargeting.SmartTargeting;

public class Upgrades
{
    public static final IUpgrade UPG_STORAGE_I      = new SimpleUpgrade("storage_1");
    public static final IUpgrade UPG_STORAGE_II     = new SimpleUpgrade("storage_2", UPG_STORAGE_I);
    public static final IUpgrade UPG_STORAGE_III    = new SimpleUpgrade("storage_3", UPG_STORAGE_II);
    public static final IUpgrade AMMO_STORAGE       = new AmmoStorage();
    public static final IUpgrade HEALTH_I           = new Health.MK1();
    public static final IUpgrade HEALTH_II          = new Health.MK2();
    public static final IUpgrade HEALTH_III         = new Health.MK3();
    public static final IUpgrade HEALTH_IV          = new Health.MK4();
    public static final IUpgrade RELOAD_I           = new ReloadTime.MK1();
    public static final IUpgrade RELOAD_II          = new ReloadTime.MK2();
    public static final IUpgrade SMART_TGT          = new SmartTargeting();
    public static final IUpgrade ECONOMY_I          = new Economy.MK1();
    public static final IUpgrade ECONOMY_II         = new Economy.MK2();
    public static final IUpgrade ECONOMY_INF        = new Economy.MKInf();
//    public static final IUpgrade ENDER_MEDIUM       = new SimpleUpgrade("ender_gain_medium", Turrets.LASER);
//    public static final IUpgrade FUEL_PURIFY        = new SimpleUpgrade("fuel_purifier", Turrets.FLAMETHROWER);
    public static final IUpgrade SHIELD_PERSONAL    = new PersonalShield();
//    public static final IUpgrade SHIELD_PROJECTILE  = new SimpleUpgrade("shield_projectile", Turrets.FORCEFIELD);
//    public static final IUpgrade SHIELD_EXPLOSIVE   = new SimpleUpgrade("shield_explosive", Turrets.FORCEFIELD);
//    public static final IUpgrade SHIELD_STRENGTH_I  = new SimpleUpgrade("shield_strength_1", Turrets.FORCEFIELD);
//    public static final IUpgrade SHIELD_STRENGTH_II = new SimpleUpgrade("shield_strength_2", SHIELD_STRENGTH_I, Turrets.FORCEFIELD);
//    public static final IUpgrade SHIELD_COLORIZER   = new UpgradeShieldColorizer();
    public static final IUpgrade ENDER_TOXIN_I      = new SimpleUpgrade("ender_toxin_1");
    public static final IUpgrade ENDER_TOXIN_II     = new SimpleUpgrade("ender_toxin_2", ENDER_TOXIN_I);
    public static final IUpgrade TURRET_SAFE        = new SimpleUpgrade("turret_safe");
    public static final IUpgrade LEVELING           = new Leveling();
    public static final IUpgrade REMOTE_ACCESS      = new SimpleUpgrade("remote_access");
    public static final IUpgrade CREATIVE           = new Creative();

    public static void register(IUpgradeRegistry registry) {
        registry.registerAll(UPG_STORAGE_I, UPG_STORAGE_II, UPG_STORAGE_III, AMMO_STORAGE, HEALTH_I, HEALTH_II, HEALTH_III, HEALTH_IV,
                             RELOAD_I, RELOAD_II, SMART_TGT, ECONOMY_I, ECONOMY_II, ECONOMY_INF, /*ENDER_MEDIUM, FUEL_PURIFY,*/
                             SHIELD_PERSONAL/*, SHIELD_PROJECTILE, SHIELD_EXPLOSIVE, SHIELD_STRENGTH_I, SHIELD_STRENGTH_II, SHIELD_COLORIZER*/,
                             ENDER_TOXIN_I, ENDER_TOXIN_II, TURRET_SAFE, LEVELING, REMOTE_ACCESS, CREATIVE);
    }
}
