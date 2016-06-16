/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityProjectileCryoCell
        extends EntityTurretProjectile
{
    private int level = 0;
    private int duration = 0;

    public EntityProjectileCryoCell(World world) {
        super(world);
    }

    public EntityProjectileCryoCell(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    public EntityProjectileCryoCell(World world, Entity shooter, Vec3 shootingVec) {
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

        if( this.worldObj.isRemote ) {
            TurretModRebirth.proxy.spawnParticle(EnumParticle.CRYO_PARTICLE, this.posX, this.posY, this.posZ, Triplet.with(this.motionX, this.motionY, this.motionZ));
        }
    }

    @Override
    protected void processHit(MovingObjectPosition hitObj) {
        super.processHit(hitObj);

//        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
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
    public boolean onPreHit(Entity e, DamageSource dmgSource, float dmg) {
        if( !this.worldObj.isRemote && e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), this.duration, this.level));
            this.setDead();
            return false;
        }

        return super.onPreHit(e, dmgSource, dmg);
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmg) {
        super.onPostHit(e, dmg);

        if( e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).hurtResistantTime = 0;
        }
    }
}
