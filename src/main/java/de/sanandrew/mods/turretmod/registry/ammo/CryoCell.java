/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public abstract class CryoCell
        implements IAmmunition
{
    @Nonnull
    @Override
    public ITurret getTurret() {
        return Turrets.CRYOLATOR;
    }

    @Override
    public int getAmmoCapacity() {
        return 1;
    }

    public static class Mk1
            extends CryoCell
    {
        private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo_cryo_cell_1");

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public IProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_I;
        }

        @Override
        public Range<Float> getDamageInfo() {
            return Range.is(Projectiles.CRYO_BALL_I.getDamage());
        }
    }

    public static class Mk2
            extends CryoCell
    {
        private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo_cryo_cell_2");

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public IProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_II;
        }

        @Override
        public Range<Float> getDamageInfo() {
            return Range.is(Projectiles.CRYO_BALL_II.getDamage());
        }
    }

    public static class Mk3
            extends CryoCell
    {
        private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "ammo_cryo_cell_3");

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public IProjectile getProjectile(ITurretInst turretInst) {
            return Projectiles.CRYO_BALL_III;
        }

        @Override
        public Range<Float> getDamageInfo() {
            return Range.is(Projectiles.CRYO_BALL_III.getDamage());
        }
    }
}
