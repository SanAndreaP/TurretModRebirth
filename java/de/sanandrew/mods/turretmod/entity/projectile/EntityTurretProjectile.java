package de.sanandrew.mods.turretmod.entity.projectile;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
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
public class EntityTurretProjectile
        extends Entity
        implements IProjectile, IEntityAdditionalSpawnData
{
    private UUID shooter;
    private Entity shooterCache;
    private UUID target;
    private Entity targetCache;

    private float arc = 0.4F;

    public EntityTurretProjectile(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.renderDistanceWeight = 10.0D;
        this.yOffset = 0.0F;
    }

    public EntityTurretProjectile(World world, Entity shooter, Entity target) {
        this(world);
        this.setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1D, shooter.posZ);

        this.shooter = shooter.getUniqueID();
        this.shooterCache = shooter;

        this.target = target.getUniqueID();
        this.targetCache = target;

        Vec3 targetVec = Vec3.createVectorHelper(target.posX - this.posX, target.boundingBox.minY + target.height / 1.5F - this.posY, target.posZ - this.posZ);
        this.setHeadingFromVec(targetVec.normalize());

        this.arc = 0.4F;

        this.motionY += arc * Math.sqrt(targetVec.xCoord * targetVec.xCoord + targetVec.zCoord * targetVec.zCoord) * 0.05;
    }

    public EntityTurretProjectile(World world, Entity shooter, Vec3 shootingVec) {
        this(world, shooter, (Entity) null);
        this.setHeadingFromVec(shootingVec.normalize());
    }

    private void setHeadingFromVec(Vec3 vector) {
        this.motionX = vector.xCoord;
        this.motionZ = vector.zCoord;
        this.motionY = vector.yCoord;

        float vecPlaneNormal = MathHelper.sqrt_double(vector.xCoord * vector.xCoord + vector.zCoord * vector.zCoord);

        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(vector.xCoord, vector.zCoord) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(vector.yCoord, vecPlaneNormal) * 180.0D / Math.PI);
    }

    @Override
    protected void entityInit() {
        if( this.shooter != null && this.shooterCache == null ) {
            this.shooterCache = TmrUtils.getEntityByUUID(this.worldObj, this.shooter);
        }
        if( this.target != null && this.targetCache == null ) {
            this.targetCache = TmrUtils.getEntityByUUID(this.worldObj, this.target);
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
//        if( this.ticksExisted < 20 ) {
//            return;
//        }

//        this.isAirBorne = true;

//        super.onUpdate();
//        if( this.ticksExisted > 200 ) {
//            this.setDead();
//            this.isDead = true;
//        }

//        this.doBlockCheck();
        if( !this.worldObj.isRemote ) {
            this.doCollisionCheck();
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0D / Math.PI);
        while( this.rotationPitch - this.prevRotationPitch < -180.0F ) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float speed = 1.00F;

        if (this.isInWater())
        {
            for (int l = 0; l < 4; ++l)
            {
                float f4 = 0.25F;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
            }

            speed = 0.8F;
        }

        if (this.isWet())
        {
            this.extinguish();
        }

        this.motionX *= speed;
        this.motionY *= speed;
        this.motionZ *= speed;
        this.motionY -= this.arc * 0.1F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.func_145775_I();
    }

//    private void doBlockCheck() {
//        Block block = this.worldObj.getBlock(this.tileX, this.tileY, this.tileZ);
//
//        if( block.getMaterial() != Material.air ) {
//            block.setBlockBoundsBasedOnState(this.worldObj, this.tileX, this.tileY, this.tileZ);
//            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.tileX, this.tileY, this.tileZ);
//
//            if( axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)) ) {
//                this.setDead();
//            }
//        }
//    }

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
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
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
                }

                if( this.isBurning() && !(hitObj.entityHit instanceof EntityEnderman) ) {
                    hitObj.entityHit.setFire(5);
                }

                if( hitObj.entityHit.attackEntityFrom(damagesource, dmg) ) {
                    if( hitObj.entityHit instanceof EntityLivingBase ) {
                        EntityLivingBase living = (EntityLivingBase) hitObj.entityHit;

                        if( !this.worldObj.isRemote ) {
                            living.setArrowCountInEntity(living.getArrowCountInEntity() + 1);
                        }

                        if( living instanceof EntityCreature && TmrUtils.getIsAIEnabled(living) && this.shooterCache instanceof EntityLivingBase ) {
                            EntityCreature creature = (EntityCreature) living;
                            EntityAIBase ai = TmrUtils.getAIFromTaskList(creature.targetTasks.taskEntries, EntityAIAttackTurret.class);
                            if( ai == null ) {
                                ai = new EntityAIAttackTurret(creature, new RevengeEntitySelector(this.shooterCache));
                                creature.targetTasks.taskEntries.clear();
                                creature.targetTasks.addTask(0, ai);
                            } else {
                                ((EntityAIAttackTurret) ai).overrideTarget(this.shooterCache);
                            }
                            ((EntityCreature) living).setAttackTarget((EntityLivingBase) this.shooterCache);

                            if( creature instanceof EntityZombie ) {
                                EntityAIBase aiTgtFollow = TmrUtils.getAIFromTaskList(creature.tasks.taskEntries, EntityAIMoveTowardsTarget.class);
                                if( aiTgtFollow == null ) {
                                    creature.tasks.addTask(2, new EntityAIMoveTowardsTarget(creature, 0.9D, 32.0F));
                                    creature.tasks.addTask(1, new EntityAIAttackOnCollide(creature, EntityTurret.class, 1.0D, true));
                                }
                            }
                        }

                        float knockback = this.getKnockbackStrength();
                        if( knockback > 0.0F ) {
                            double horizMotion = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                            if( horizMotion > 0.0F ) {
                                hitObj.entityHit.addVelocity(this.motionX * knockback * 0.6D / horizMotion, 0.1D, this.motionZ * knockback * 0.6D / horizMotion);
                            }
                        }

                        if( this.shooterCache instanceof EntityLivingBase ) {
                            EnchantmentHelper.func_151384_a(living, this.shooterCache);
                            EnchantmentHelper.func_151385_b((EntityLivingBase) this.shooterCache, living);
                        }

                        //                        if (this.shooterCache != null && hitObj.entityHit != this.shooterCache && hitObj.entityHit instanceof EntityPlayer && this.shooterCache instanceof EntityPlayerMP )
                        //                        {
                        //                            ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                        //                        }
                    }

                    //                    if(!(hitObj.entityHit instanceof EntityEnderman))
                    //                    {
                    //                    }
                }
//                else {
//                    this.motionX *= -0.10000000149011612D;
//                    this.motionY *= -0.10000000149011612D;
//                    this.motionZ *= -0.10000000149011612D;
//                    this.rotationYaw += 180.0F;
//                    this.prevRotationYaw += 180.0F;
////                    this.ticksInAir = 0;
//                }
            } else {
//                this.tileX = hitObj.blockX;
//                this.tileY = hitObj.blockY;
//                this.tileZ = hitObj.blockZ;
////                this.field_145790_g = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
////                this.inData = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);
//                this.motionX = (double)((float)(hitObj.hitVec.xCoord - this.posX));
//                this.motionY = (double)((float)(hitObj.hitVec.yCoord - this.posY));
//                this.motionZ = (double)((float)(hitObj.hitVec.zCoord - this.posZ));
//                f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
//                this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
//                this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
//                this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
//                this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
////                this.inGround = true;
////                this.arrowShake = 7;
////                this.setIsCritical(false);
//
//                if (this.field_145790_g.getMaterial() != Material.air)
//                {
//                    this.field_145790_g.onEntityCollidedWithBlock(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f, this);
//                }
            }
            this.setDead();

            this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    public float getDamage() {
        return 2.0F;
    }

    public float getKnockbackStrength() {
        return 0.0F;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {

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
//        buffer.writeDouble(this.motionX);
//        buffer.writeDouble(this.motionY);
//        buffer.writeDouble(this.motionZ);
        buffer.writeFloat(this.rotationYaw);
        buffer.writeFloat(this.rotationPitch);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
//        this.motionX = buffer.readDouble();
//        this.motionY = buffer.readDouble();
//        this.motionZ = buffer.readDouble();
        this.rotationYaw = buffer.readFloat();
        this.rotationPitch = buffer.readFloat();
    }

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

        @Override
        public void startExecuting() {
            super.startExecuting();
        }
    }
}
