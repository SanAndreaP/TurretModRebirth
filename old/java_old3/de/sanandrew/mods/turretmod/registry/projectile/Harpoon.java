/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.projectile;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;

@Category("harpoon")
public class Harpoon
        implements IProjectile
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "harpoon");

    @Value(comment = "Base damage this projectile can deal to a target.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float damage = 6.0F;
    @Value(comment = "Multiplier applied to the speed with which this projectile travels.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float speed = 1.0F;
    @Value(comment = "How much this projectile curves down/up. negative values let it go up, whereas positive values go down.", range = @Range(minD = -10.0D, maxD = 10.0D))
    public static float arc = 0.4F;
    @Value(comment = "Horizontal knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackH = 0.01F;
    @Value(comment = "Vertical (y) knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackV = 0.1F;
    @Value(comment = "How much more inaccurate this projectile's trajectory vector becomes. Higher values result in less accuracy.", range = @Range(minD = 0.0D, maxD = 10.0D))
    public static double scatter = 0.1D;

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float getArc() {
        return arc;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public float getKnockbackHorizontal() {
        return knockbackH;
    }

    @Override
    public float getKnockbackVertical() {
        return knockbackV;
    }

    @Override
    public double getScatterValue() {
        return scatter;
    }

    @Override
    public float getSpeedMultiplierLiquid(float viscosity) {
        return 2.0F - viscosity;
    }
}
