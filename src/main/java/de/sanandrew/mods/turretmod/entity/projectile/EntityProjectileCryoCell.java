/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

public class EntityProjectileCryoCell
        extends EntityTurretProjectile
{
    private int level = 0;
    private int duration = 0;

    @SuppressWarnings("unused")
    public EntityProjectileCryoCell(World world) {
        super(world);
    }

    public EntityProjectileCryoCell(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    @SuppressWarnings("unused")
    public EntityProjectileCryoCell(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    public EntityProjectileCryoCell setLevelAndDuration(int lvl, int duration) {
        this.level = lvl;
        this.duration = duration;
        return this;
    }

    @Override
    public float getArc() {
        return 0.05F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if( this.world.isRemote ) {
            TurretModRebirth.proxy.spawnParticle(EnumParticle.CRYO_PARTICLE, this.posX, this.posY, this.posZ, new Tuple(this.motionX, this.motionY, this.motionZ));
        }
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 1.5F;
    }

    @Override
    public float getDamage() {
        return 0.0F;
    }

    @Override
    public float getKnockbackStrengthH() {
        return 0.0F;
    }

    @Override
    public float getKnockbackStrengthV() {
        return 0.0F;
    }

    @Override
    public boolean onPreHit(Entity e, DamageSource dmgSource, MutableFloat dmg) {
        if( !this.world.isRemote && e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.duration, this.level));
            if( e instanceof EntityCreature && this.shooterCache instanceof ITurretInst) {
                TmrUtils.INSTANCE.setEntityTarget((EntityCreature) e, (ITurretInst) this.shooterCache);
            }
            this.playSound(this.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.setDead();
            return false;
        }

        return super.onPreHit(e, dmgSource, dmg);
    }

    @Override
    public SoundEvent getRicochetSound() {
        return Sounds.RICOCHET_SPLASH;
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmgSource) {
        super.onPostHit(e, dmgSource);

        if( e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).hurtResistantTime = 0;
        }
    }
}
