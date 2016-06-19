package de.sanandrew.mods.turretmod.entity.projectile;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
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
        this.renderDistanceWeight = 10.0D;
        this.yOffset = 0.0F;
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

        Vec3 targetVec = Vec3.createVectorHelper(target.posX - this.posX, target.boundingBox.minY + target.height / 1.4F - this.posY, target.posZ - this.posZ);
        this.setHeadingFromVec(targetVec.normalize());

        this.motionY += this.getArc() * Math.sqrt(targetVec.xCoord * targetVec.xCoord + targetVec.zCoord * targetVec.zCoord) * 0.05;
    }

    public EntityTurretProjectile(World world, Entity shooter, Vec3 shootingVec) {
        this(world, shooter, (Entity) null);
        this.setHeadingFromVec(shootingVec.normalize());
    }

    private void setHeadingFromVec(Vec3 vector) {
        double scatterVal = getScatterValue();
        float initSpeed = getInitialSpeedMultiplier();

        this.motionX = vector.xCoord * initSpeed + (TmrUtils.RNG.nextDouble() * 2.0D - 1.0D) * scatterVal;
        this.motionZ = vector.zCoord * initSpeed + (TmrUtils.RNG.nextDouble() * 2.0D - 1.0D) * scatterVal;
        this.motionY = vector.yCoord * initSpeed + (TmrUtils.RNG.nextDouble() * 2.0D - 1.0D) * scatterVal;

        float vecPlaneNormal = MathHelper.sqrt_double(vector.xCoord * vector.xCoord + vector.zCoord * vector.zCoord);

        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(vector.xCoord, vector.zCoord) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(vector.yCoord, vecPlaneNormal) * 180.0D / Math.PI);
    }

    @Override
    protected void entityInit() {
        if( this.shooterUUID != null && this.shooterCache == null ) {
            this.shooterCache = TmrUtils.getEntityByUUID(this.worldObj, this.shooterUUID);
        }
        if( this.targetUUID != null && this.targetCache == null ) {
            this.targetCache = TmrUtils.getEntityByUUID(this.worldObj, this.targetUUID);
        }
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int its3EveryTime) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void onUpdate() {
        this.isAirBorne = true;

        if( this.shooterCache instanceof EntityTurret && this.getDistanceToEntity(this.shooterCache) > ((EntityTurret) this.shooterCache).getTargetProcessor().getRange() * 4 ) {
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
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * disPos, this.posY - this.motionY * disPos, this.posZ - this.motionZ * disPos, this.motionX, this.motionY, this.motionZ);
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
        this.func_145775_I();
    }

    private void doCollisionCheck() {
        Vec3 posVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 futurePosVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition hitObj = this.worldObj.func_147447_a(posVec, futurePosVec, false, true, false);

        posVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        futurePosVec = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if( hitObj != null ) {
            futurePosVec = Vec3.createVectorHelper(hitObj.hitVec.xCoord, hitObj.hitVec.yCoord, hitObj.hitVec.zCoord);
        }

        Entity entity = null;
        AxisAlignedBB checkBB = this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D);

        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, checkBB);
        double minDist = 0.0D;
        float collisionRange;

        for( Object entityObj : list ) {
            Entity collidedEntity = (Entity) entityObj;

            if( collidedEntity.canBeCollidedWith() && collidedEntity != this.shooterCache ) {
                collisionRange = 0.3F;
                AxisAlignedBB collisionAABB = collidedEntity.boundingBox.expand(collisionRange, collisionRange, collisionRange);
                MovingObjectPosition interceptObj = collisionAABB.calculateIntercept(posVec, futurePosVec);

                if( interceptObj != null ) {
                    double vecDistance = posVec.distanceTo(interceptObj.hitVec);

                    if( vecDistance < minDist || minDist == 0.0D ) {
                        entity = collidedEntity;
                        minDist = vecDistance;
                    }
                }
            }
        }

        if( entity != null ) {
            hitObj = new MovingObjectPosition(entity);
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
                    if( EntityTurret.class.isAssignableFrom(hitObj.entityHit.getClass()) ) {
                        return;
                    }
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
                            EnchantmentHelper.func_151384_a(living, this.shooterCache);
                            EnchantmentHelper.func_151385_b((EntityLivingBase) this.shooterCache, living);
                        }
                    }
                }
            } else {
                this.onBlockHit(hitObj.blockX, hitObj.blockY, hitObj.blockZ);
            }

            this.processHit(hitObj);
        }
    }

    public static void setEntityTarget(EntityCreature target, EntityTurret attacker) {
        if( TmrUtils.getIsAIEnabled(target) ) {
            EntityAIBase ai = TmrUtils.getAIFromTaskList(target.targetTasks.taskEntries, EntityAIAttackTurret.class);
            if( ai == null ) {
                ai = new EntityAIAttackTurret(target, new RevengeEntitySelector(attacker));
                target.targetTasks.taskEntries.clear();
                target.targetTasks.addTask(0, ai);
            } else if( !ai.continueExecuting() ) {
                ((EntityAIAttackTurret) ai).overrideTarget(attacker);
            }
            target.setAttackTarget(attacker);

            if( target instanceof EntityZombie ) {
                EntityAIBase aiTgtFollow = TmrUtils.getAIFromTaskList(target.tasks.taskEntries, EntityAIMoveTowardsTurret.class);
                if( aiTgtFollow == null ) {
                    target.tasks.addTask(2, new EntityAIMoveTowardsTurret(target, attacker, 0.9D, 32.0F));
                    target.tasks.addTask(1, new EntityAIAttackOnCollide(target, EntityTurret.class, 1.0D, true));
                } else if( !aiTgtFollow.continueExecuting() ) {
                    ((EntityAIMoveTowardsTurret) aiTgtFollow).setNewTurret(attacker);
                }
            }
        } else {
            target.setTarget(attacker);
        }
    }

    public void knockBackEntity(EntityLivingBase living, double deltaX, double deltaZ) {
        if( this.rand.nextDouble() >= living.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue() ) {
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

    protected void processHit(MovingObjectPosition hitObj) {
        this.playSound(this.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        this.setDead();
    }

    private void onBlockHit(int blockX, int blockY, int blockZ) { }

    public abstract float getInitialSpeedMultiplier();

    public abstract float getDamage();

    public abstract float getKnockbackStrengthH();

    public abstract float getKnockbackStrengthV();

    public abstract String getRicochetSound();

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
            this.shooterCache = TmrUtils.getEntityByUUID(this.worldObj, this.shooterUUID);
        }

        if( nbt.hasKey("target") ) {
            this.targetUUID = UUID.fromString(nbt.getString("target"));
            this.targetCache = TmrUtils.getEntityByUUID(this.worldObj, this.targetUUID);
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

    private static final class RevengeEntitySelector
            implements IEntitySelector
    {
        public Entity target;

        public RevengeEntitySelector(Entity target) {
            this.target = target;
        }

        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity == this.target;
        }
    }

    private static final class EntityAIAttackTurret
            extends EntityAINearestAttackableTarget
    {
        private RevengeEntitySelector selector;

        public EntityAIAttackTurret(EntityCreature creature, RevengeEntitySelector selector) {
            super(creature, EntityTurret.class, 0, false, false, selector);
            this.selector = selector;
        }

        public void overrideTarget(Entity target) {
            this.selector.target = target;
        }
    }
}
