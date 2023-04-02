/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.entity.turret;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.turret.ITurret;
import dev.sanandrea.mods.turretmod.api.turret.ITurretRegistry;
import dev.sanandrea.mods.turretmod.entity.turret.delegate.Crossbow;
import net.minecraft.util.ResourceLocation;

public final class Turrets
{
    public static final ITurret CROSSBOW = new Crossbow(new ResourceLocation(TmrConstants.ID, "crossbow_turret"));

    public static void register(ITurretRegistry registry) {
        registry.registerAll(CROSSBOW);
    }
}
