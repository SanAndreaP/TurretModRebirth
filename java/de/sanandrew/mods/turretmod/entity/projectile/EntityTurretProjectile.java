/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"SuspiciousNameCombination", "BooleanMethodNameMustStartWithQuestion"})
public abstract class EntityTurretProjectile
        extends Entity
        implements IProjectile, IEntityAdditionalSpawnData
{
    protected UUID shooterUUID;
    protected Entity shooterCache;
    protected UUID targetUUID;
    protected Entity targetCache;

    public EntityTurretProjectile(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityTurretProjectile(World world, Entity shooter, Entity target) {
        this(world);

        double y = shooter.posY + shooter.getEyeHeight() - 0.1D;

        if( shooter instanceof EntityTurret && ((EntityTurret) shooter).isUpsideDown ) {
            y -= 1.0D;
        }

        this.setPosition(shooter.posX, y, shooter.posZ);

        this.shooterUUID = shooter.getUniqueID();
        this.shooterCache = shooter;

        this.targetUUID = target.getUniqueID();
        this.targetCache = target;

        Vec3d targetVec = new Vec3d(target.posX - shooter.posX, (target.getEntityBoundingBox().minY + target.height / 1.4D) - y, target.posZ - shooter.posZ);
        this.setHeadingFromVec(targetVec.normalize());

        this.motionY += this.getArc() * Math.sqrt(targetVec.xCoord * targetVec.xCoord + targetVec.zCoord * targetVec.zCoord) * 0.05;
    }

    public EntityTurretProjectile(World world, Entity shooter, Vec3d shootingVec) {
        this(world, shooter, (Entity) null);
        this.setHeadingFromVec(shootingVec.normalize());
    }

    private void setHeadingFromVec(Vec3d vector) {
        double scatterVal = getScatterValue();
        float initSpeed = getInitialSpeedMultiplier();

        this.motionX = vector.xCoord * initSpeed + (MiscUtils.RNG.randomDouble() * 2.0D - 1.0D) * scatterVal;
        this.motionZ = vector.zCoord * initSpeed + (MiscUtils.RNG.randomDouble() * 2.0D - 1.0D) * scatterVal;
        this.motionY = vector.yCoord * initSpeed + (MiscUtils.RNG.randomDouble() * 2.0D - 1.0D) * scatterVal;

        float vecPlaneNormal = MathHelper.sqrt_double(vector.xCoord * vector.xCoord + vector.zCoord * vector.zCoord);

        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(vector.xCoord, vector.zCoord) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(vector.yCoord, vecPlaneNormal) * 180.0D / Math.PI);
    }

    @Override
    protected void entityInit() {
        if( this.shooterUUID != null && this.shooterCache == null ) {
            this.shooterCache = EntityUtils.getEntityByUUID(this.worldObj, this.shooterUUID);
        }
        if( this.targetUUID != null && this.targetCache == null ) {
            this.targetCache = EntityUtils.getEntityByUUID(this.worldObj, this.targetUUID);
        }
    }

    @Override
    public void onUpdate() {
        this.isAirBorne = true;

        if( this.shooterCache instanceof EntityTurret && this.getDistanceToEntity(this.shooterCache) > ((EntityTurret) this.shooterCache).getTargetProcessor().getRangeVal() * 4 ) {
            this.setDead();
            return;
        }

        this.doCollisionCheck();

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0D / Math.PI);
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
        float speed = this.getSpeedMultiplierAir();

        if( this.isInWater() ) {
            for( int i = 0; i < 4; i++ ) {
                float disPos = 0.25F;
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * disPos, this.posY - this.motionY * disPos, this.posZ - this.motionZ * disPos, this.motionX, this.motionY, this.motionZ);
            }

            speed = this.getSpeedMultiplierLiquid();
        }

        if( this.isWet() ) {
            this.extinguish();
        }


        this.motionX *= speed;
        this.motionY *= speed;
        this.motionZ *= speed;
        this.motionY -= this.getArc() * 0.1F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.doBlockCollisions();
    }

    private void doCollisionCheck() {
        Vec3d posVec = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d futurePosVec = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult hitObj = this.worldObj.rayTraceBlocks(posVec, futurePosVec, false, true, false);

        posVec = new Vec3d(this.posX, this.posY, this.posZ);
        futurePosVec = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if( hitObj != null ) {
            futurePosVec = new Vec3d(hitObj.hitVec.xCoord, hitObj.hitVec.yCoord, hitObj.hitVec.zCoord);
        }

        Entity entity = null;
        AxisAlignedBB checkBB = this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D);

        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, checkBB);
        double minDist = 0.0D;
        float collisionRange;

        for( Object entityObj : list ) {
            Entity collidedEntity = (Entity) entityObj;

            if( collidedEntity.canBeCollidedWith() && collidedEntity != this.shooterCache ) {
                collisionRange = 0.3F;
                AxisAlignedBB collisionAABB = collidedEntity.getEntityBoundingBox().expand(collisionRange, collisionRange, collisionRange);
                RayTraceResult interceptObj = collisionAABB.calculateIntercept(posVec, futurePosVec);

                if( interceptObj != null ) {
                    double vecDistance = posVec.distanceTo(interceptObj.hitVec);

                    if( !EntityTurret.class.isAssignableFrom(collidedEntity.getClass()) && (vecDistance < minDist || minDist == 0.0D) ) {
                        entity = collidedEntity;
                        minDist = vecDistance;
                    }
                }
            }
        }

        if( entity != null ) {
            hitObj = new RayTraceResult(entity);
        }

        if( hitObj != null && hitObj.entityHit != null && hitObj.entityHit instanceof EntityPlayer ) {
            EntityPlayer player = (EntityPlayer)hitObj.entityHit;

            if( player.capabilities.disableDamage ) {
                hitObj = null;
            }
        }

        if( hitObj != null ) {
            if( hitObj.entityHit != null ) {
                float dmg = this.getDamage();

                DamageSource damagesource;

                if( this.shooterCache == null ) {
                    damagesource = DamageSource.causeThrownDamage(this, this);
                } else {
                    damagesource = DamageSource.causeThrownDamage(this, this.shooterCache);
                }

                if( this.isBurning() && !(hitObj.entityHit instanceof EntityEnderman) ) {
                    hitObj.entityHit.setFire(5);
                }

                boolean preHitVelocityChanged = hitObj.entityHit.velocityChanged;
                boolean preHitAirBorne = hitObj.entityHit.isAirBorne;
                double preHitMotionX = hitObj.entityHit.motionX;
                double preHitMotionY = hitObj.entityHit.motionY;
                double preHitMotionZ = hitObj.entityHit.motionZ;
                if( this.onPreHit(hitObj.entityHit, damagesource, dmg) && hitObj.entityHit.attackEntityFrom(damagesource, dmg) ) {
                    hitObj.entityHit.velocityChanged = preHitVelocityChanged;
                    hitObj.entityHit.isAirBorne = preHitAirBorne;
                    hitObj.entityHit.motionX = preHitMotionX;
                    hitObj.entityHit.motionY = preHitMotionY;
                    hitObj.entityHit.motionZ = preHitMotionZ;

                    this.onPostHit(hitObj.entityHit, damagesource);
                    if( hitObj.entityHit instanceof EntityLivingBase ) {
                        EntityLivingBase living = (EntityLivingBase) hitObj.entityHit;

                        if( !this.worldObj.isRemote ) {
                            living.setArrowCountInEntity(living.getArrowCountInEntity() + 1);
                        }

                        if( living instanceof EntityCreature && this.shooterCache instanceof EntityTurret ) {
                            setEntityTarget((EntityCreature) living, (EntityTurret) this.shooterCache);
                        }

                        double deltaX = this.posX - living.posX;
                        double deltaZ = this.posZ - living.posZ;

                        while( deltaX * deltaX + deltaZ * deltaZ < 0.0001D ) {
                            deltaZ = (Math.random() - Math.random()) * 0.01D;
                            deltaX = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.knockBackEntity(living, deltaX, deltaZ);

                        if( this.shooterCache instanceof EntityLivingBase ) {
                            EnchantmentHelper.applyThornEnchantments(living, this.shooterCache);
                            EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shooterCache, living);
                        }
                    }
                }
            } else {
                this.onBlockHit(hitObj.getBlockPos());
            }

            this.processHit(hitObj);
        }
    }

    public static void setEntityTarget(EntityCreature target, final EntityTurret attacker) {
        target.setAttackTarget(attacker);
        target.setRevengeTarget(attacker);

        List<EntityAIMoveTowardsTurret> aiLst = EntityUtils.getAisFromTaskList(target.tasks.taskEntries, EntityAIMoveTowardsTurret.class);
        if( aiLst.size() < 1 ) {
            target.tasks.addTask(10, new EntityAIMoveTowardsTurret(target, attacker, 1.1D, 64.0F));
        } else {
            aiLst.forEach(aiTgtFollow -> {
                if( !aiTgtFollow.continueExecuting() ) {
                    aiTgtFollow.setNewTurret(attacker);
                }
            });
        }
    }

    public void knockBackEntity(EntityLivingBase living, double deltaX, double deltaZ) {
        if( this.rand.nextDouble() >= living.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue() ) {
            living.isAirBorne = true;
            double normXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
            double kbStrengthXZ = this.getKnockbackStrengthH();
            double kbStrengthY = this.getKnockbackStrengthV();
            living.motionX /= 2.0D;
            living.motionY /= 2.0D;
            living.motionZ /= 2.0D;
            living.motionX -= deltaX / normXZ * kbStrengthXZ;
            living.motionY += kbStrengthY;
            living.motionZ -= deltaZ / normXZ * kbStrengthXZ;

            if( living.motionY > 0.4000000059604645D ) {
                living.motionY = 0.4000000059604645D;
            }
        }
    }

    protected void processHit(@SuppressWarnings("UnusedParameters") RayTraceResult hitObj) {
        if( hitObj != null ) {
            if( hitObj.typeOfHit == RayTraceResult.Type.BLOCK ) {
            }
        }
        this.setPosition(hitObj.hitVec.xCoord, hitObj.hitVec.yCoord, hitObj.hitVec.zCoord);
        this.playSound(this.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        this.setDead();
    }

    private void onBlockHit(@SuppressWarnings("UnusedParameters") BlockPos pos) { }

    public abstract float getInitialSpeedMultiplier();

    public abstract float getDamage();

    public abstract float getKnockbackStrengthH();

    public abstract float getKnockbackStrengthV();

    public abstract SoundEvent getRicochetSound();

    public double getScatterValue() {
        return 0.0F;
    }

    public float getSpeedMultiplierAir() {
        return 1.0F;
    }

    public float getSpeedMultiplierLiquid() {
        return 0.8F;
    }

    public boolean onPreHit(Entity e, DamageSource dmgSource, float dmg) {
        return true;
    }

    public void onPostHit(Entity e, DamageSource dmg) { }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        if( nbt.hasKey("shooter") ) {
            this.shooterUUID = UUID.fromString(nbt.getString("shooter"));
            this.shooterCache = EntityUtils.getEntityByUUID(this.worldObj, this.shooterUUID);
        }

        if( nbt.hasKey("target") ) {
            this.targetUUID = UUID.fromString(nbt.getString("target"));
            this.targetCache = EntityUtils.getEntityByUUID(this.worldObj, this.targetUUID);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        if( this.shooterUUID != null ) {
            nbt.setString("shooter", this.shooterUUID.toString());
        }

        if( this.targetUUID != null ) {
            nbt.setString("target", this.targetUUID.toString());
        }
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float recoil, float randMulti) {
        float vecNormal = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= vecNormal;
        y /= vecNormal;
        z /= vecNormal;
        x += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * randMulti;
        y += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * randMulti;
        z += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * randMulti;
        x *= recoil;
        y *= recoil;
        z *= recoil;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float vecPlaneNormal = MathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, vecPlaneNormal) * 180.0D / Math.PI);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(this.rotationYaw);
        buffer.writeFloat(this.rotationPitch);
        buffer.writeBoolean(this.shooterCache != null);
        if( this.shooterCache != null ) {
            buffer.writeInt(this.shooterCache.getEntityId());
        }
        buffer.writeBoolean(this.targetCache != null);
        if( this.targetCache != null ) {
            buffer.writeInt(this.targetCache.getEntityId());
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.rotationYaw = buffer.readFloat();
        this.rotationPitch = buffer.readFloat();

        if( buffer.readBoolean() ) {
            this.shooterCache = this.worldObj.getEntityByID(buffer.readInt());
        }
        if( buffer.readBoolean() ) {
            this.targetCache = this.worldObj.getEntityByID(buffer.readInt());
        }
    }

    public abstract float getArc();
}
