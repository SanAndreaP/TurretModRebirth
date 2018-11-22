/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.TurretForcefield;

public final class Turrets
{
    public static final ITurret CROSSBOW = new TurretCrossbow();
    public static final ITurret SHOTGUN = new TurretShotgun();
    public static final ITurret CRYOLATOR = new TurretCryolator();
    public static final ITurret REVOLVER = new TurretRevolver();
    public static final ITurret MINIGUN = new TurretMinigun();
    public static final ITurret FORCEFIELD = new TurretForcefield();
    public static final ITurret LASER = new TurretLaser();
    public static final ITurret FLAMETHROWER = new TurretFlamethrower();

    public static void initialize(ITurretRegistry registry) {
        registry.register(CROSSBOW);
        registry.register(SHOTGUN);
        registry.register(CRYOLATOR);
        registry.register(REVOLVER);
        registry.register(MINIGUN);
        registry.register(FORCEFIELD);
        registry.register(LASER);
        registry.register(FLAMETHROWER);
    }
}
