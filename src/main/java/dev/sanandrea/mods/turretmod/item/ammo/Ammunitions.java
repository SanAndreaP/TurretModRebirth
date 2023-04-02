/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.ammo;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunition;
import dev.sanandrea.mods.turretmod.api.ammo.IAmmunitionRegistry;
import dev.sanandrea.mods.turretmod.item.ammo.delegate.CrossbowBolt;
import dev.sanandrea.mods.turretmod.item.ammo.delegate.TippedCrossbowBolt;
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
