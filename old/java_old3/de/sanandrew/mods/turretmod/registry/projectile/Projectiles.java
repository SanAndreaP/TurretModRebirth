/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;

public class Projectiles
{
    public static final IProjectile CB_BOLT       = new CrossbowBolt();
    public static final IProjectile HARPOON       = new Harpoon();
    public static final IProjectile CRYO_BALL_I   = new CryoBall(CryoBall.ID1);
    public static final IProjectile CRYO_BALL_II  = new CryoBall(CryoBall.ID2);
    public static final IProjectile CRYO_BALL_III = new CryoBall(CryoBall.ID3);
    public static final IProjectile PEBBLE        = new ShotgunPebble();
    public static final IProjectile BULLET        = new Bullet();
    public static final IProjectile MG_PEBBLE     = new MinigunPebble();
    public static final IProjectile LASER_NORMAL  = new Laser(Laser.ID1);
    public static final IProjectile LASER_BLURAY  = new Laser(Laser.ID2);
    public static final IProjectile FLAME_NORMAL  = new Flame(Flame.ID1);
    public static final IProjectile FLAME_PURIFY  = new Flame(Flame.ID2);

    public static void initialize(IProjectileRegistry registry) {
        registry.registerAll(CB_BOLT, HARPOON, CRYO_BALL_I, CRYO_BALL_II, CRYO_BALL_III, PEBBLE, BULLET, MG_PEBBLE, LASER_NORMAL, LASER_BLURAY,
                             FLAME_NORMAL, FLAME_PURIFY);
    }
}
