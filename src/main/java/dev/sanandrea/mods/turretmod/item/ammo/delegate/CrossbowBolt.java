/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.ammo.delegate;

import dev.sanandrea.mods.turretmod.api.ammo.Ammunition;
import dev.sanandrea.mods.turretmod.api.ammo.IProjectile;
import dev.sanandrea.mods.turretmod.api.turret.ITurret;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.entity.projectile.Projectiles;
import dev.sanandrea.mods.turretmod.entity.turret.Turrets;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class CrossbowBolt
        extends Ammunition
{
    public CrossbowBolt(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getCapacity() {
        return 1;
    }

    @Nonnull
    @Override
    public ITurret getApplicableTurret() {
        return Turrets.CROSSBOW;
    }

    @Override
    public IProjectile getProjectile(ITurretEntity turret) {
        return Projectiles.CB_BOLT;
    }

    @Override
    public Range<Float> getDamageInfo() {
        return Range.is(4.0F);
    }
}
