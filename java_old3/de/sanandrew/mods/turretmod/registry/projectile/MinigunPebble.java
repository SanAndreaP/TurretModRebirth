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
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.Sounds;
import de.sanandrew.mods.turretmod.registry.turret.TurretMinigun;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Category("minigun pebble")
@SuppressWarnings("WeakerAccess")
public class MinigunPebble
        implements IProjectile
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "pebble.minigun");

    @Value(comment = "Base damage this projectile can deal to a target.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float damage = 0.9F;
    @Value(comment = "Multiplier applied to the speed with which this projectile travels.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float speed = 3.0F;
    @Value(comment = "How much this projectile curves down/up. negative values let it go up, whereas positive values go down.", range = @Range(minD = -10.0D, maxD = 10.0D))
    public static float arc = 0.001F;
    @Value(comment = "Horizontal knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackH = 0.0F;
    @Value(comment = "Vertical (y) knockback strength this projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float knockbackV = 0.0F;
    @Value(comment = "How much more inaccurate this projectile's trajectory vector becomes. Higher values result in less accuracy.", range = @Range(minD = 0.0D, maxD = 10.0D))
    public static double scatter = 0.01F;

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onCreate(@Nullable ITurretInst turret, @Nonnull IProjectileInst projectile) {
        if( turret != null ) {
            TurretMinigun.MyRAM ram = turret.getRAM(TurretMinigun.MyRAM::new);

            float shift = (ram.isLeftShot ? 45.0F : -45.0F) / 180.0F * (float) Math.PI;
            float rotXZ = -turret.get().rotationYawHead / 180.0F * (float) Math.PI;
            float rotY = -(turret.get().rotationPitch - 7.5F) / 180.0F * (float) Math.PI - 0.1F;

            Entity projEntity = projectile.get();
            projEntity.posX += Math.sin(rotXZ + shift) * 0.7F * Math.cos(rotY);
            projEntity.posY += Math.sin(rotY) * 0.6F;
            projEntity.posZ += Math.cos(rotXZ + shift) * 0.7F * Math.cos(rotY);

            projEntity.setPosition(projEntity.posX, projEntity.posY, projEntity.posZ);
        }
    }

    @Override
    public float getArc() {
        return arc;
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_BULLET;
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
}
