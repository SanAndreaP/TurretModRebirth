/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.turret.ITurretInfo;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;

public final class Turrets
{
    public static final ITurretInfo CROSSBOW = new TurretInfoCrossbow();
    public static final ITurretInfo SHOTGUN = new TurretInfoShotgun();
    public static final ITurretInfo CRYOLATOR = new TurretInfoCryolator();
    public static final ITurretInfo REVOLVER = new TurretInfoRevolver();
    public static final ITurretInfo MINIGUN = new TurretInfoMinigun();
    public static final ITurretInfo LASER = new TurretInfoLaser();
    public static final ITurretInfo FLAMETHROWER = new TurretInfoFlamethrower();

    public static void initialize(ITurretRegistry registry) {
        TurretRegistry turretRegistry = (TurretRegistry) registry;

        turretRegistry.registerTurretInfo(CROSSBOW, true);
        turretRegistry.registerTurretInfo(SHOTGUN, true);
        turretRegistry.registerTurretInfo(CRYOLATOR, true);
        turretRegistry.registerTurretInfo(REVOLVER, true);
        turretRegistry.registerTurretInfo(MINIGUN, true);
        turretRegistry.registerTurretInfo(LASER, true);
        turretRegistry.registerTurretInfo(FLAMETHROWER, true);
    }
}
