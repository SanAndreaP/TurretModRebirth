/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.api.TurretProjectile;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.util.ReflectionManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public abstract class EntityTurretProjectile
        extends EntityArrow
        implements TurretProjectile<EntityTurretProjectile>
{
    protected int xTile = -1;
    protected int yTile = -1;
    protected int zTile = -1;
    protected Block inTile = Blocks.air;
    protected int inData = 0;
    protected boolean inGround = false;
    protected int ticksInGround;
    protected int ticksInAir = 0;
    protected float knockbackStrength = 1.25F;

    public int ammoType = 0;

    public Entity targetedEntity;
    public boolean hasNoTarget;
//    public boolean isMoving;
    public boolean isPickupable = true;

    public DamageSource defaultDmgSrc = null;

    public EntityTurretProjectile(World par1World) {
        super(par1World);
        this.renderDistanceWeight = 10.0D;
    }

    public EntityTurretProjectile(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.renderDistanceWeight = 10.0D;
    }

    public void setTarget(EntityLivingBase shooter, Entity target, float par4, float par5) {
        this.shootingEntity = shooter;
        this.targetedEntity = target;

        this.posY = shooter.posY + shooter.getEyeHeight() - 0.1D;
        double deltaX = target.posX - shooter.posX;
        double deltaY = target.posY + target.getEyeHeight() - 0.7D - this.posY;
        double deltaZ = target.posZ - shooter.posZ;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

        if( distanceXZ >= 0.0D ) {
            float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
            float pitch = (float) (-(Math.atan2(deltaY, distanceXZ) * 180.0D / Math.PI));
            this.setLocationAndAngles(shooter.posX, this.posY, shooter.posZ, yaw, pitch);
            this.yOffset = 0.0F;
            float curveAngle = (float) distanceXZ * this.getCurveCorrector();
            this.setHeading(deltaX, deltaY + curveAngle, deltaZ, par4, par5);
        }
    }

    public void setHeading(double d1, double d2, double d3, float rngMulti1, float rngMulti2) {
        this.setThrowableHeading(d1, d2, d3, rngMulti1, rngMulti2);
        this.motionX *= this.getSpeedVal();
        this.motionY *= this.getSpeedVal();
        this.motionZ *= this.getSpeedVal();
    }

    public float getCurveCorrector() {
        return 0.20F;
    }

    public boolean isArrow() {
        return true;
    }

    public boolean shouldDieOnImpact() {
        return true;
    }

    public void processHit(MovingObjectPosition moving) {
        EntityLivingBase entity = (EntityLivingBase) moving.entityHit;

        if( !this.worldObj.isRemote && this.isArrow() ) {
            entity.setArrowCountInEntity(entity.getArrowCountInEntity() + 1);
        }

        ReflectionManager.setRecentlyHit(entity, 0);

        float motionNormal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        moving.entityHit.motionX *= this.knockbackStrength * 0.6000000238418579D / motionNormal;
        moving.entityHit.motionZ *= this.knockbackStrength * 0.6000000238418579D / motionNormal;

        if( moving.entityHit.onGround ) {
            moving.entityHit.motionY = this.knockbackStrength / 3.0F;
            moving.entityHit.isAirBorne = true;
        }

        if( this.shootingEntity instanceof EntityLivingBase ) {
            Enchantment.thorns.func_151367_b((EntityLivingBase) this.shootingEntity, entity, 1);

            if( moving.entityHit != this.shootingEntity && moving.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP ) {
                ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
            }
        }
    }

    public void onEntityHit(EntityLivingBase living) {
    }

    public boolean shouldFlyThroughOnEntityHit() {
        return false;
    }

    public String getHitSound() {
        return "random.bowhit";
    }

    public void spawnTail() {
    }

    public float getGravityVal() {
        return 0.04F;
    }

    public float getSpeedVal() {
        return 1.0F;
    }

    public float getMotionMulti() {
        return 0.99F;
    }

    @Override
    @SuppressWarnings({"SuspiciousNameCombination", "unchecked", "NonReproducibleMathCall"})
    public void onUpdate() {
        if( this.ticksExisted == 1 ) {
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;

            return;
        }

//        if( !this.isMoving && !this.worldObj.isRemote ) {
//            return;
//        }

        this.onEntityUpdate();

        if( !this.worldObj.isRemote && this.shootingEntity != null && (this.shootingEntity instanceof EntityTurretBase)
            && this.getDistanceToEntity(this.shootingEntity) > 128.0D )
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.setDead();
            return;
        }

        if( this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F ) {
            float motionNormal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, motionNormal) * 180.0D / Math.PI);
        }

        Block collidedBlock = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);

        if( collidedBlock.getMaterial() != Material.air ) {
            collidedBlock.setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB colBlockBB = collidedBlock.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if( colBlockBB != null && colBlockBB.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)) ) {
                this.inGround = true;
            }
        }

        if( this.arrowShake > 0 ) {
            --this.arrowShake;
        }

        if( this.inGround ) {
            Block groundBlock = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
            int groundBlockMeta = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            if( groundBlock == this.inTile && groundBlockMeta == this.inData ) {
                if( this.shouldDieOnGround() ) {
                    this.setDead();
                }

                ++this.ticksInGround;

                if( this.ticksInGround == 1200 ) {
                    this.setDead();
                }
            } else {
                this.inGround = false;
                this.motionX *= (this.rand.nextFloat() * 0.2F);
                if( this.isArrow() ) {
                    this.motionY *= (this.rand.nextFloat() * 0.2F);
                } else {
                    this.motionY = -this.getSpeedVal();
                }
                this.motionZ *= (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir;
            Vec3 posVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 motionVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition currHitObj = this.worldObj.func_147447_a(posVec, motionVec, false, true, false);//raytrace
            posVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            motionVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if( currHitObj != null ) {
                motionVec = Vec3.createVectorHelper(currHitObj.hitVec.xCoord, currHitObj.hitVec.yCoord, currHitObj.hitVec.zCoord);
            }

            Entity hitEntity = null;
            List<Entity> collidedEntities;
            AxisAlignedBB entityColBB = this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D);
            if( this.shootingEntity instanceof EntityTurretBase ) {
                collidedEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, entityColBB, ((EntityTurretBase) this.shootingEntity).getParentTargetSelector());
            } else {
                collidedEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, entityColBB);
            }

            Double minDistIntercept = null;

            EntityDragonPart dragonPart = null;

            for( Entity collidedEntity : collidedEntities ) {
                if( collidedEntity instanceof EntityDragonPart ) {
                    IEntityMultiPart multiEntity = ((EntityDragonPart) collidedEntity).entityDragonObj;
                    if( multiEntity instanceof EntityDragon ) {
                        dragonPart = (EntityDragonPart) collidedEntity;
                        collidedEntity = (EntityDragon) multiEntity;
                    }
                }


                boolean isShootEntityValid = !(this.shootingEntity == null || this.shootingEntity == this);
                boolean isDispensed = this.hasNoTarget && !isShootEntityValid;
                boolean isShootingEntity = isShootEntityValid && collidedEntity == this.shootingEntity;
                boolean isNotRider = (isShootEntityValid && this.shootingEntity.riddenByEntity != collidedEntity) || isDispensed;
                boolean isHostRidden = isShootEntityValid && this.hasNoTarget && this.shootingEntity.riddenByEntity != null && this.shootingEntity.riddenByEntity instanceof EntityPlayer;
//                boolean isTargetValid = isHostRidden;/* || this.shouldTargetOneType()*/;
//                				? this.targetedEntity != null && collidedEntity != null && this.targetedEntity.getClass().isAssignableFrom(collidedEntity.getClass())
//                				: this.shootingEntity instanceof AEntityTurretBase && collidedEntity instanceof EntityLiving
//                						&& ((AEntityTurretBase)this.shootingEntity).get((EntityLiving) collidedEntity)
//                						);
                boolean cannotBeHit = (collidedEntity instanceof EntityLiving) && ((EntityLiving) collidedEntity).hurtResistantTime > ((EntityLiving) collidedEntity).maxHurtResistantTime / 2.0F || collidedEntity.isDead;


                if( (collidedEntity.canBeCollidedWith() || (collidedEntity instanceof EntityDragon))
                        && !isShootingEntity && isNotRider && /*(isDispensed || isHostRidden) &&*/ (!cannotBeHit || !shouldFlyThroughOnEntityHit()) ) {
                    float expandBB = 0.3F;
                    AxisAlignedBB var12 = collidedEntity.boundingBox.expand(expandBB, expandBB, expandBB);
                    MovingObjectPosition interceptObj = var12.calculateIntercept(posVec, motionVec);

                    if( interceptObj != null ) {
                        double distIntercept = posVec.distanceTo(interceptObj.hitVec);

                        if( minDistIntercept == null || distIntercept < minDistIntercept ) {
                            hitEntity = collidedEntity;
                            minDistIntercept = distIntercept;
                        }
                    }
                }
            }

            if( hitEntity != null ) {
                currHitObj = new MovingObjectPosition(hitEntity);
            }

            if( currHitObj != null ) {
                if( currHitObj.entityHit != null ) {

                    DamageSource dmgSrc = this.getDamageSource(currHitObj.entityHit);

                    if( currHitObj.entityHit instanceof EntityDragon && dragonPart != null ) {
                        if( ((EntityDragon) currHitObj.entityHit).attackEntityFromPart(dragonPart, dmgSrc, (float) this.getDamage()) ) {
                            processHit(currHitObj);
                            onEntityHit((EntityDragon) currHitObj.entityHit);
                            this.playSound(getHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                            if( this.shouldDieOnImpact() ) {
                                this.setDead();
                            }
                        }
                    } else if( currHitObj.entityHit.attackEntityFrom(dmgSrc, (float) this.getDamage()) ) {
                        if( currHitObj.entityHit instanceof EntityLiving ) {
                            EntityLiving living = (EntityLiving) currHitObj.entityHit;
                            if( this.shootingEntity != null && this.shootingEntity instanceof EntityTurretBase ) {
                                EntityTurretBase turret = (EntityTurretBase) this.shootingEntity;
//                        		if (turret.getDistanceToEntity(living) <= 16.0F) {
                                living.setRevengeTarget(turret);
                                living.setAttackTarget(turret);
                                living.setLastAttacker(turret);
                                ReflectionManager.setCurrentTarget(living, turret, 10 + this.rand.nextInt(20));
//	                        		ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, living, turret, "currentTarget", "field_70776_bF");
//                        		} else if( living instanceof EntityCreature && living instanceof IMob ) {
//                        			PathEntity path = this.worldObj.getEntityPathToXYZ(living, (int)turret.posX, (int)turret.posY, (int)turret.posZ, (float) turret.wdtRange* 2.0F, true, false, false, true);
//                        			living.getNavigator().setPath(path, 0.35F);
//                        			((EntityCreature)living).setPathToEntity(path);
//                        		}
                            }
                            processHit(currHitObj);
                            onEntityHit(living);
                        }

                        this.playSound(getHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                        if( !(currHitObj.entityHit instanceof EntityEnderman) ) {
                            if( this.shouldDieOnImpact() ) {
                                this.setDead();
                            }
                        }
                    } else if(
//                            this.shouldTargetOneType() ? (this.targetedEntity != null
//                    		&& this.targetedEntity.getClass().isAssignableFrom(currHitObj.entityHit.getClass()))
//                    	:
                            this.shootingEntity != null && currHitObj.entityHit != null && this.shootingEntity instanceof EntityTurretBase
                                    && ((EntityTurretBase) this.shootingEntity).getParentTargetSelector().isEntityApplicable(currHitObj.entityHit) ) {
                        this.processFailedHit(currHitObj.entityHit);
                    }
                } else {
                    this.xTile = currHitObj.blockX;
                    this.yTile = currHitObj.blockY;
                    this.zTile = currHitObj.blockZ;
                    this.inTile = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = ((float) (currHitObj.hitVec.xCoord - this.posX));
                    this.motionY = ((float) (currHitObj.hitVec.yCoord - this.posY));
                    this.motionZ = ((float) (currHitObj.hitVec.zCoord - this.posZ));
                    float motionVecNormal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / motionVecNormal * 0.05D;
                    this.posY -= this.motionY / motionVecNormal * 0.05D;
                    this.posZ -= this.motionZ / motionVecNormal * 0.05D;
                    this.playSound(getHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    if( this.isArrow() ) {
                        this.arrowShake = 7;
                    }

                    this.setIsCritical(false);

                    if( this.inTile != Blocks.air ) {
                        this.inTile.onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
                    }
                }
            }

            for( int i = 0; i < 4; ++i ) {
                spawnTail();
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float motionXZNormal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.rotationPitch = (float) (Math.atan2(this.motionY, motionXZNormal) * 180.0D / Math.PI);

            while( this.rotationPitch - this.prevRotationPitch < -180.0F ) {
                this.prevRotationPitch -= 360.0F;
            }

            while( this.rotationPitch - this.prevRotationPitch >= 180.0F ) {
                this.prevRotationPitch += 360.0F;
            }

            while( this.rotationYaw - this.prevRotationYaw < -180.0F ) {
                this.prevRotationYaw -= 360.0F;
            }

            while( this.rotationYaw - this.prevRotationYaw >= 180.0F ) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float motionMulti = getMotionMulti();
            float gravityVal = getGravityVal();

            if( this.isInWater() ) {
                for( int i = 0; i < 4; ++i ) {
                    float motReduction = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * motReduction, this.posY - this.motionY * motReduction,
                                                this.posZ - this.motionZ * motReduction, this.motionX, this.motionY, this.motionZ
                    );
                }

                motionMulti = 0.8F;
            }

            this.motionX *= motionMulti;
            this.motionY *= motionMulti;
            this.motionZ *= motionMulti;
            this.motionY -= gravityVal;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();//collide with block
        }
    }

    public void processFailedHit(Entity hit) {
        if( this.isArrow() ) {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
        }
    }

    public boolean shouldTargetOneType() {
        return true;
    }

    @Override
    public EntityTurretProjectile getEntity() {
        return this;
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        if( sound != null && !sound.isEmpty() ) {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("xTile", (short) this.xTile);
        nbt.setShort("yTile", (short) this.yTile);
        nbt.setShort("zTile", (short) this.zTile);
        nbt.setByte("inTile", (byte) Block.getIdFromBlock(this.inTile));
        nbt.setByte("inData", (byte) this.inData);
        nbt.setByte("shake", (byte) this.arrowShake);
        nbt.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        nbt.setByte("pickup", (byte) this.canBePickedUp);
        nbt.setBoolean("dispensed", this.hasNoTarget);
//        nbt.setBoolean("move", this.isMoving);
        nbt.setBoolean("isPickupable", this.isPickupable);
        nbt.setInteger("ammoType", this.ammoType);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.xTile = nbt.getShort("xTile");
        this.yTile = nbt.getShort("yTile");
        this.zTile = nbt.getShort("zTile");
        this.inTile = Block.getBlockById(nbt.getByte("inTile") & 255);
        this.inData = nbt.getByte("inData") & 255;
        this.arrowShake = nbt.getByte("shake") & 255;
        this.inGround = nbt.getByte("inGround") == 1;
        this.hasNoTarget = nbt.getBoolean("dispensed");
//        this.isMoving = nbt.getBoolean("move");
        this.isPickupable = nbt.getBoolean("isPickupable");
        this.ammoType = nbt.getInteger("ammoType");
    }

    public ItemStack getPickupItem() {
        return null;
    }

    public boolean shouldDieOnGround() {
        return true;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        ItemStack is = this.getPickupItem();
        if( !this.worldObj.isRemote && is != null && this.inGround && this.arrowShake <= 0 && this.isPickupable ) {
            boolean doPickup = true;

            if( !par1EntityPlayer.inventory.addItemStackToInventory(is) ) {
                doPickup = false;
            }

            if( doPickup ) {
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    public double getDamage() {
        return 2.0D;
    }

    public float getKnockbackStrengthFloat() {
        return this.knockbackStrength;
    }

    public void setKnockbackStrengthFloat(float par1) {
        this.knockbackStrength = par1;
    }

    public DamageSource getDamageSource(Entity entity) {
        if( this.shootingEntity == null ) {
            return DamageSource.causeArrowDamage(this, this);
        } else if( entity instanceof EntityDragon ) {
            return DamageSource.generic.setExplosion();
        } else if( /**this.isActingAsMeelee**/false && this.shootingEntity instanceof EntityLiving ) {
            return DamageSource.causeMobDamage((EntityLiving) this.shootingEntity);
        } else {
            return DamageSource.causeArrowDamage(this, this.shootingEntity);
        }
    }
}
