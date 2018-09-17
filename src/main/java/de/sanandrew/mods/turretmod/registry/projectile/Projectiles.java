/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;

public class Projectiles
{
    public static final ITurretProjectile CB_BOLT = new CrossbowBolt();
    public static final ITurretProjectile CRYO_BALL_I = new CryoBall(CryoBall.ID1, 0, 300);
    public static final ITurretProjectile CRYO_BALL_II = new CryoBall(CryoBall.ID2, 2, 250);
    public static final ITurretProjectile CRYO_BALL_III = new CryoBall(CryoBall.ID3, 4, 200);
    public static final ITurretProjectile PEBBLE = new Pebble();
    public static final ITurretProjectile BULLET = new Bullet();
    public static final ITurretProjectile MG_PEBBLE = new MinigunPebble();
    public static final ITurretProjectile LASER = new Laser();
}
