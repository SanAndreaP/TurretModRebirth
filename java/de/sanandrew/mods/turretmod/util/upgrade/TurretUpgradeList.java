/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.upgrade;

import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.api.registry.TurretUpgradeRegistry;
import de.sanandrew.mods.turretmod.entity.turret.techi.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthI;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthIII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeHealth.TUpgradeHealthIV;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeReloadTime.TUpgradeReloadTimeI;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeReloadTime.TUpgradeReloadTimeII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeUpgStorage.TUpgradeUpgStorageI;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeUpgStorage.TUpgradeUpgStorageII;
import de.sanandrew.mods.turretmod.util.upgrade.TUpgradeUpgStorage.TUpgradeUpgStorageIII;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public final class TurretUpgradeList
{
    public static final TurretUpgrade HEALTH_INCR_I   = new TUpgradeHealthI();
    public static final TurretUpgrade HEALTH_INCR_II  = new TUpgradeHealthII(HEALTH_INCR_I);
    public static final TurretUpgrade HEALTH_INCR_III = new TUpgradeHealthIII(HEALTH_INCR_II);
    public static final TUpgradeHealthIV HEALTH_INCR_IV  = new TUpgradeHealthIV(HEALTH_INCR_III);
    public static final TurretUpgrade RELOAD_TIME_I = new TUpgradeReloadTimeI();
    public static final TurretUpgrade RELOAD_TIME_II = new TUpgradeReloadTimeII(RELOAD_TIME_I);
    public static final TurretUpgrade UPG_STORAGE_I = new TUpgradeUpgStorageI();
    public static final TurretUpgrade UPG_STORAGE_II = new TUpgradeUpgStorageII(UPG_STORAGE_I);
    public static final TurretUpgrade UPG_STORAGE_III = new TUpgradeUpgStorageIII(UPG_STORAGE_II);
    public static final TurretUpgrade AMMO_STORAGE = new TUpgradeAmmoStorage();

    static {
        try {
            Field[] upgFields = TurretUpgradeList.class.getFields();
            for( Field fld : upgFields ) {
                if( TurretUpgrade.class.isAssignableFrom(fld.getType()) ) {
                    TurretUpgradeRegistry.registerUpgrade((TurretUpgrade) fld.get(null));
                }
            }
        } catch( IllegalAccessException | ClassCastException ex ) {
            throw new RuntimeException("An unexpected critical error occurred during initializing the Turret Upgrades!", ex);
        }

        HEALTH_INCR_IV.addTurretApplicable(EntityTurretCrossbow.class);
    }
}
