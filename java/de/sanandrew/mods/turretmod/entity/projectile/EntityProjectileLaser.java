/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

public class EntityProjectileLaser
        extends EntityTurretProjectile
{
    private int prevMaxHurtResistantTime;

    @SuppressWarnings("unused")
    public EntityProjectileLaser(World world) {
        super(world);
    }

    public EntityProjectileLaser(World world, Entity shooter, Entity target) {
        super(world, shooter, target);
    }

    @SuppressWarnings("unused")
    public EntityProjectileLaser(World world, Entity shooter, Vec3d shootingVec) {
        super(world, shooter, shootingVec);
    }

    @Override
    public float getArc() {
        return 0.0F;
    }

    @Override
    public float getInitialSpeedMultiplier() {
        return 20.0F;
    }

    @Override
    public float getDamage() {
        return 1.5F;
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
    public SoundEvent getRicochetSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    public DamageSource getProjDamageSource(Entity hitEntity) {
        DamageSource dmg = new EntityDamageSourceIndirect("thrown", this, this.shooterCache == null ? this : this.shooterCache);
        if( !(this.shooterCache instanceof EntityTurret && ((EntityTurret) this.shooterCache).getUpgradeProcessor().hasUpgrade(UpgradeRegistry.UPG_ENDER_LENS)) ) {
            dmg.setFireDamage();
        }
        return dmg;
    }

    @Override
    public boolean onPreHit(Entity e, DamageSource dmgSource, MutableFloat dmg) {
        if( super.onPreHit(e, dmgSource, dmg) ) {
            if( e instanceof EntityLivingBase ) {
                EntityLivingBase elb = ((EntityLivingBase) e);

                if( !(this.shooterCache instanceof EntityTurret && ((EntityTurret) this.shooterCache).getUpgradeProcessor().hasUpgrade(UpgradeRegistry.UPG_ENDER_LENS)) ) {
                    if( elb.isImmuneToFire() ) {
                        return false;
                    }
                } else {
                    if( !elb.isImmuneToFire() ) {
                        dmg.setValue(dmg.floatValue() * 1.25F);
                    }
                }

                this.prevMaxHurtResistantTime = elb.maxHurtResistantTime;
                elb.maxHurtResistantTime = 10;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPostHit(Entity e, DamageSource dmgSource) {
        super.onPostHit(e, dmgSource);

        if( e instanceof EntityLivingBase ) {
            ((EntityLivingBase) e).maxHurtResistantTime = this.prevMaxHurtResistantTime;
            e.setFire(2);
        }
    }
}
