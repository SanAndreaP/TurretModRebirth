/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;

public class Projectiles
{
    public static final ITurretProjectile CB_BOLT = new CrossbowBolt();
    public static final ITurretProjectile CRYO_BALL_I = new CryoBall(CryoBall.ID1);
    public static final ITurretProjectile CRYO_BALL_II = new CryoBall(CryoBall.ID2);
    public static final ITurretProjectile CRYO_BALL_III = new CryoBall(CryoBall.ID3);
    public static final ITurretProjectile PEBBLE = new ShotgunPebble();
    public static final ITurretProjectile BULLET = new Bullet();
    public static final ITurretProjectile MG_PEBBLE = new MinigunPebble();
    public static final ITurretProjectile LASER_NORMAL = new Laser(Laser.ID1);
    public static final ITurretProjectile LASER_BLURAY = new Laser(Laser.ID2);
    public static final ITurretProjectile FLAME_NORMAL = new Flame(Flame.ID1);
    public static final ITurretProjectile FLAME_PURIFY = new Flame(Flame.ID2);

    public static void initialize(IProjectileRegistry registry) {
        registry.registerProjectile(CB_BOLT);
        registry.registerProjectile(CRYO_BALL_I);
        registry.registerProjectile(CRYO_BALL_II);
        registry.registerProjectile(CRYO_BALL_III);
        registry.registerProjectile(PEBBLE);
        registry.registerProjectile(BULLET);
        registry.registerProjectile(MG_PEBBLE);
        registry.registerProjectile(LASER_NORMAL);
        registry.registerProjectile(LASER_BLURAY);
        registry.registerProjectile(FLAME_NORMAL);
        registry.registerProjectile(FLAME_PURIFY);
    }
}
