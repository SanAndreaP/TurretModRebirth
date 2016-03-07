/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public abstract class EntityTurret
        extends EntityLiving
{
    private boolean isInitialized = false;

    // data watcher IDs
    private static final int DW_AMMO = 20; /* INT */
    private static final int DW_AMMO_TYPE = 21; /* ITEM_STACK */
    private static final int DW_EXPERIENCE = 22; /* INT */
    private static final int DW_OWNER_UUID = 23; /* STRING */
    private static final int DW_OWNER_NAME = 24; /* STRING */
    private static final int DW_SHOOT_TICKS = 26; /* INT */
    private static final int DW_FREQUENCY = 27; /* BYTE */
    private static final int DW_BOOLEANS = 28; /* BYTE */

    private Entity entityToAttack;

    public EntityTurret(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void faceEntity(Entity entity, float yawSpeed, float pitchSpeed) {
        if( entity == null || entity.boundingBox == null ) {
            return;
        }

        double deltaX = entity.posX - this.posX;
        double deltaZ = entity.posZ - this.posZ;
        double deltaY;

        if( entity instanceof EntityLivingBase ) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            deltaY = this.posY + this.getEyeHeight() - (livingBase.posY + livingBase.getEyeHeight());
        } else {
            deltaY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D - (this.posY + this.getEyeHeight());
        }

        double distVecXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        float yawRotation = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
        float pitchRotation = (float) -(Math.atan2(deltaY, distVecXZ) * 180.0D / Math.PI);
        this.rotationPitch = -this.updateRotation(this.rotationPitch, pitchRotation);
        this.rotationYawHead = this.updateRotation(this.rotationYawHead, yawRotation);
    }

    protected float updateRotation(float prevRotation, float newRotation) {
        float var4 = MathHelper.wrapAngleTo180_float(newRotation - prevRotation);
        return prevRotation + var4;
    }

    @Override
    public void onLivingUpdate() {
        this.rotationYaw = 0.0F;

        if( this.newPosRotationIncrements > 0 ) {
//            double newX = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
//            double newY = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
//            double newZ = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;

            this.rotationPitch = (float) (this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / (float) this.newPosRotationIncrements);
            this.newPosRotationIncrements--;
//            this.setPosition(newX, newY, newZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if( this.worldObj.isRemote ) {
//            this.motionX *= 0.98D;
//            this.motionY *= 0.98D;
//            this.motionZ *= 0.98D;
        }

//        if( Math.abs(this.motionX) < 0.005D ) {
//            this.motionX = 0.0D;
//        }
//
//        if( Math.abs(this.motionY) < 0.005D ) {
//            this.motionY = 0.0D;
//        }
//
//        if( Math.abs(this.motionZ) < 0.005D ) {
//            this.motionZ = 0.0D;
//        }

        this.worldObj.theProfiler.startSection("ai");

        if( this.isMovementBlocked() ) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if( !this.worldObj.isRemote ) {
            this.worldObj.theProfiler.startSection("oldAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
        }

        if( this.entityToAttack == null ) {
            List entities = this.worldObj.getEntitiesWithinAABB(IMob.class, this.boundingBox.expand(16.0D, 16.0D, 16.0D));
            for( Object e : entities ) {
                Entity entity = (Entity) e;
                if( !(entity.isDead || entity.isEntityInvulnerable()) ) {
                    this.entityToAttack = entity;
                    break;
                }
            }

//            this.entityToAttack = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 16.0D);
        } else if( !this.worldObj.isRemote && this.ticksExisted % 20 == 0 ) {
            EntityTurretProjectile projectile = new EntityTurretProjectile(this.worldObj, this, this.entityToAttack);
            this.worldObj.spawnEntityInWorld(projectile);
        }

        if( this.entityToAttack != null && (this.entityToAttack.isDead || this.entityToAttack.isEntityInvulnerable() || this.getDistanceToEntity(this.entityToAttack) > 16.0D) ) {
            this.entityToAttack = null;
        }

        this.worldObj.theProfiler.endSection();

//        if( this.isActive() && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) && !this.tgtHandler.hasTarget(this) ) {
//            this.rotationYawHead += 1.0F;
//            this.rotationPitch = 0.0F;
//        }
    }

    @Override
    protected void updateEntityActionState() {
        ++this.entityAge;
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;

//        if( !this.isActive() ) {
//            return;
//        }

    }

    public void onRenderTick(float partTickTime) {
//        if( this.isInitialized ) {
            if( this.entityToAttack != null ) {
                this.faceEntity(this.entityToAttack, 10.0F, this.getVerticalFaceSpeed());
            } else if( this.worldObj.isRemote && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) ) {
                this.rotationYawHead += 1.0F;
                this.rotationPitch = 0.0F;
            }
//        }
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 5;
    }

    /**turrets are machines, they aren't affected by potions*/
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        return false;
    }

    /**turrets are immobile, leave empty*/
    @Override
    public void knockBack(Entity entity, float unknown, double motionXAmount, double motionZAmount) { }

    /**turrets are immobile, leave empty*/
    @Override
    public void moveEntity(double motionX, double motionY, double motionZ) { }

    /**turrets are immobile, leave empty*/
    @Override
    public void moveEntityWithHeading(float strafe, float forward) { }

    public abstract ResourceLocation getStandardTexture();

    public abstract ResourceLocation getGlowTexture();
}
