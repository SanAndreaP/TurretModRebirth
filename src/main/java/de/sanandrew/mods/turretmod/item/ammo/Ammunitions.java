/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.item.ammo;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.delegate.CrossbowBolt;
import de.sanandrew.mods.turretmod.item.ammo.delegate.TippedCrossbowBolt;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("WeakerAccess")
public final class Ammunitions
{
    public static final IAmmunition BOLT            = new CrossbowBolt(new ResourceLocation(TmrConstants.ID, "crossbow_bolt"));
    public static final IAmmunition TIPPED_BOLT     = new TippedCrossbowBolt(new ResourceLocation(TmrConstants.ID, "tipped_crossbow_bolt"));
//    public static final IAmmunition HARPOON         = new Harpoon();
//    public static final IAmmunition SGSHELL         = new ShotgunShell();
//    public static final IAmmunition CRYOCELL_MK1    = new CryoCell.Mk1();
//    public static final IAmmunition CRYOCELL_MK2    = new CryoCell.Mk2();
//    public static final IAmmunition CRYOCELL_MK3    = new CryoCell.Mk3();
//    public static final IAmmunition BULLET          = new Bullet();
//    public static final IAmmunition MGSHELL         = new MinigunShell();
//    public static final IAmmunition ELECTROLYTECELL = new ElectrolyteCell();
//    public static final IAmmunition FLUXCELL        = new FluxCell();
//    public static final IAmmunition FUELTANK        = new FuelTank();

    public static void register(IAmmunitionRegistry registry) {
        registry.registerAll(BOLT, TIPPED_BOLT);
    }
}
