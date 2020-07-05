/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;

@SuppressWarnings("WeakerAccess")
public final class Ammunitions
{
    public static final IAmmunition BOLT            = new Bolt();
    public static final IAmmunition TIPPED_BOLT     = new TippedBolt();
    public static final IAmmunition HARPOON         = new Harpoon();
    public static final IAmmunition SGSHELL         = new ShotgunShell();
    public static final IAmmunition CRYOCELL_MK1    = new CryoCell.Mk1();
    public static final IAmmunition CRYOCELL_MK2    = new CryoCell.Mk2();
    public static final IAmmunition CRYOCELL_MK3    = new CryoCell.Mk3();
    public static final IAmmunition BULLET          = new Bullet();
    public static final IAmmunition MGSHELL         = new MinigunShell();
    public static final IAmmunition ELECTROLYTECELL = new ElectrolyteCell();
    public static final IAmmunition FLUXCELL        = new FluxCell();
    public static final IAmmunition FUELTANK        = new FuelTank();

    public static void initialize(IAmmunitionRegistry registry) {
        registry.registerAll(BOLT, TIPPED_BOLT, HARPOON, SGSHELL, CRYOCELL_MK1, CRYOCELL_MK2, CRYOCELL_MK3, BULLET, MGSHELL, ELECTROLYTECELL, FLUXCELL, FUELTANK);
    }
}
