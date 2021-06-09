/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IProjectile;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.EntityRegistry;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class TurretProjectileEntity
        extends ProjectileEntity
        implements IEntityAdditionalSpawnData, IProjectileInst
{
    @Nonnull
    private IProjectile delegate;

    @Nonnull
    private IAmmunition ammunition;
    private String ammoSubtype;

    private double maxDist;
    private float attackModifier;

    private float lastDamage;
    private UUID          lastDamagedId;
    private int          lastDamagedNetId;
    private WeakReference<Entity> lastDamagedEntity;

    private BlockPos lastBlockHit;

    @SuppressWarnings("WeakerAccess")
    public TurretProjectileEntity(EntityType<TurretProjectileEntity> type, World world) {
        super(type, world);
        this.maxDist = 64.0D;

        this.attackModifier = 1.0F;
        this.lastDamage = Float.MAX_VALUE;
        this.ammunition = AmmunitionRegistry.INSTANCE.getDefault();
        this.delegate = ProjectileRegistry.INSTANCE.getDefault();
    }

    public TurretProjectileEntity(World world, @Nonnull IProjectile delegate, @Nonnull IAmmunition ammunition, String ammoSubtype, float attackModifier) {
        this(EntityRegistry.PROJECTILE, world);

        this.delegate = delegate;
        this.ammunition = ammunition;
        this.ammoSubtype = ammoSubtype;
        this.attackModifier = attackModifier;
    }

    public void shoot(Entity owner, Entity target) {
        Vector3d ownerPos = owner.position();
        Vector3d targetPos = target.position();

        Vector3d targetVec = new Vector3d(targetPos.x - ownerPos.x,
                                          (targetPos.y + target.getBbHeight() / 1.4D) - ownerPos.y - owner.getEyeHeight() + 0.1D,
                                          targetPos.z - ownerPos.z);

        this.shoot(owner, targetVec);
    }

    public void shoot(Entity owner, Vector3d shootingVec) {
        Vector3d ownerPos = owner.position();

        this.setOwner(owner);

        this.setPos(ownerPos.x, ownerPos.y + owner.getEyeHeight() - 0.1D, ownerPos.z);
        this.setHeadingFromVec(shootingVec, this.delegate.getArc());

        if( owner instanceof ITurretInst ) {
            this.maxDist = ((ITurretInst) owner).getTargetProcessor().getRangeVal() * 4.0D;

            this.delegate.onShoot((ITurretInst) owner, this);
        }
    }

    @Override
    public void shoot(double x, double y, double z, float recoil, float randomMultiplier) {
        Vector3d randomVectorGaussian = new Vector3d(this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0075D * randomMultiplier,
                                                     this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0075D * randomMultiplier,
                                                     this.random.nextGaussian() * (this.random.nextBoolean() ? -1 : 1) * 0.0075D * randomMultiplier);
        Vector3d moveVector = new Vector3d(x, y, z).normalize().add(randomVectorGaussian).scale(recoil);

        this.setDeltaMovement(moveVector);
        this.forceUpdateRotation(moveVector);

        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
    }

    private void setHeadingFromVec(Vector3d vector, double yArc) {
        double scatter = this.delegate.getScatterValue();
        float speed = this.delegate.getSpeed();
        Vector3d newVector = vector.normalize().scale(speed);

        if( scatter >= 0.000001D ) {
            newVector = newVector.add(MiscUtils.RNG.randomVector(new Vector3d(-1, -1, -1), new Vector3d(1, 1, 1)).normalize().scale(scatter));
        }
        if( yArc >= 0.000001D ) {
            newVector = newVector.add(0.0D, yArc * Math.sqrt(vector.x * vector.x + vector.z * vector.z) * 0.05D, 0.0D);
        }

        this.setDeltaMovement(newVector);
        this.forceUpdateRotation(newVector);

        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        ITurretInst ownerTurret = owner instanceof ITurretInst ? (ITurretInst) owner : null;
        if( (owner != null && this.distanceTo(owner) > this.maxDist) || !this.delegate.isValid() ) {
            this.remove();
            return;
        }

        super.tick();
        this.delegate.tick(ownerTurret, this);

//        Minecraft.getInstance().level.addParticle(ParticleTypes.LARGE_SMOKE, this.position().x, this.position().y, this.position().z, 0.0D, 0.0D, 0.0D);

        Vector3d moveVector = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            this.forceUpdateRotation(moveVector);
        }

        this.clearFire();

        this.doCollisionCheck(moveVector);

            moveVector = this.getDeltaMovement();
            double futurePosX = this.getX() + moveVector.x;
            double futurePosY = this.getY() + moveVector.y;
            double futurePosZ = this.getZ() + moveVector.z;

//            float moveHorizontalNormal = MathHelper.sqrt(getHorizontalDistanceSqr(moveVector));
//            this.yRot = (float)(MathHelper.atan2(moveVector.x, moveVector.z) * (double)(180F / (float)Math.PI));
//
//            this.xRot = (float)(MathHelper.atan2(moveVector.y, moveHorizontalNormal) * (double)(180F / (float)Math.PI));
//            this.xRot = lerpRotation(this.xRotO, this.xRot);
//            this.yRot = lerpRotation(this.yRotO, this.yRot);

            float inertia = this.delegate.getAirInertia();

            if (this.isInWater()) {
                BlockPos currPos   = this.blockPosition();
                Fluid    fluid     = this.level.getFluidState(currPos).getType();
                float    viscosity = 1.0F;
                if( fluid != Fluids.EMPTY ) {
                    viscosity = fluid.getTickDelay(this.level) / 2.5F;
                }

                inertia = this.delegate.getFluidInertia(viscosity);
            }

            this.setDeltaMovement(moveVector.scale(MathHelper.clamp(inertia, 0.0F, 1.0F)).subtract(0, this.delegate.getArc() * 0.1F, 0));
            this.updateRotation();

            this.setPos(futurePosX, futurePosY, futurePosZ);

//        this.doCollisionCheck();

//        this.posX += this.motionX;
//        this.posY += this.motionY;
//        this.posZ += this.motionZ;
//        float yRotVec = MathHelper.sqrt(this.xPower * this.xPower + this.zPower * this.zPower);
//        this.yRot = (float)(Math.atan2(this.xPower, this.zPower) * 180.0D / Math.PI);
//
//        this.xRot = (float)(Math.atan2(this.yPower, yRotVec) * 180.0D / Math.PI);
//        while( this.yRot - this.yRotO < -180.0F ) {
//            this.yRotO -= 360.0F;
//        }
//
//        while( this.yRot - this.yRotO >= 180.0F ) {
//            this.yRotO += 360.0F;
//        }
//
//        while( this.xRot - this.xRotO < -180.0F ) {
//            this.xRotO -= 360.0F;
//        }
//
//        while( this.xRot - this.xRotO >= 180.0F ) {
//            this.xRotO += 360.0F;
//        }
//
//        this.yRot = this.yRotO + (this.yRot - this.yRotO) * 0.2F;
//        this.xRot = this.xRotO + (this.xRot - this.xRotO) * 0.2F;
//        float speed = this.delegate.getSpeedMultiplierAir();
//
//        if( this.isInWater() ) {
//            BlockPos currPos   = this.blockPosition();
//            Fluid    fluid     = this.level.getFluidState(currPos).getType();
//            float    viscosity = 1.0F;
//            if( fluid != Fluids.EMPTY ) {
//                viscosity = fluid.getTickDelay(this.level) / 2.5F;
//            }
//            speed = this.delegate.getSpeedMultiplierLiquid(viscosity);
//        }
//
//
//        this.xPower *= speed;
//        this.yPower *= speed;
//        this.zPower *= speed;
//        this.yPower -= this.delegate.getArc() * 0.1F;
//        this.setDeltaMovement(vector3d.add(this.xPower, this.yPower, this.zPower).scale((double)f));
//        this.setPos(this.xPower, this.yPower, this.zPower);

    }

    private void forceUpdateRotation(Vector3d moveVector) {
        float horizontalNormal = MathHelper.sqrt(getHorizontalDistanceSqr(moveVector));

        this.xRot = lerpRotation(this.xRotO, (float)(MathHelper.atan2(moveVector.y, horizontalNormal) * (double)(180F / (float)Math.PI)));
        this.yRot = lerpRotation(this.yRotO, (float)(MathHelper.atan2(moveVector.x, moveVector.z) * (double)(180F / (float)Math.PI)));

        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
    }

    @Override
    protected boolean canHitEntity(@Nonnull Entity entity) {
        Entity owner = this.getOwner();
        ITurretInst ownerTurret = owner instanceof ITurretInst ? (ITurretInst) owner : null;

        boolean isTarget = true;
        if( ownerTurret != null ) {
            isTarget = ownerTurret.getTargetProcessor().isEntityValidTarget(entity);
        }

        return super.canHitEntity(entity) && entity != owner && isTarget;
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d currPos, Vector3d nextPos) {
        return ProjectileHelper.getEntityHitResult(this.level, this, currPos, nextPos, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    private void doCollisionCheck(Vector3d moveVector) {
        // BLOCK COLLISION CHECKS
        Vector3d position = this.position();
//        BlockPos blockPos = this.blockPosition();
//        BlockState blockState = this.level.getBlockState(blockPos);
//        if( !blockState.isAir(this.level, blockPos) ) {
//            VoxelShape collisionShape = blockState.getCollisionShape(this.level, blockPos);
//            if( !collisionShape.isEmpty() ) {
//                for( AxisAlignedBB axisalignedbb : collisionShape.toAabbs() ) {
//                    if( axisalignedbb.move(blockPos).contains(position) ) {
//                        Vector3d blockPosVector = new Vector3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D);
//                        if( this.delegate.onHit(ownerTurret, this, collisionShape.clip(position, blockPosVector, blockPos)) ) {
//                            this.remove();
//                            return;
//                        }
//                    }
//                }
//            }
//        }

        Vector3d       futurePos = position.add(moveVector);
        RayTraceResult rtResult = null;

        if( this.lastBlockHit == null || !this.lastBlockHit.equals(this.blockPosition()) ) {
            RayTraceContext.FluidMode fluidMode = this.delegate.detectFluidCollision() ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE;
            rtResult  = this.level.clip(new RayTraceContext(position, futurePos, RayTraceContext.BlockMode.COLLIDER, fluidMode, this));
            if( rtResult.getType() != RayTraceResult.Type.MISS ) {
                futurePos = rtResult.getLocation();
            }
        }

        // ENTITY COLLISION CHECKS
        while( this.isAlive() ) {
            EntityRayTraceResult hitEntityResult = this.findHitEntity(position, futurePos);
            if( hitEntityResult != null ) {
                rtResult = hitEntityResult;
            }

//            if( rtResult instanceof EntityRayTraceResult && rtResult.getType() == RayTraceResult.Type.ENTITY ) {
//                Entity entity = ((EntityRayTraceResult) rtResult).getEntity();
//                if( entity instanceof PlayerEntity && ownerTurret.isOwner((PlayerEntity) entity) && !((PlayerEntity) owner).canHarmPlayer((PlayerEntity) entity) ) {
//                    rtResult = null;
//                    hitEntityResult = null;
//                }
//            }

            if( rtResult != null
                && rtResult.getType() != RayTraceResult.Type.MISS
                && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rtResult) )
            {
                this.onHit(rtResult);
                this.hasImpulse = true;
            }

            if( hitEntityResult == null ) {
                break;
            }

            rtResult = null;
        }
//        Vec3d posVec = new Vec3d(this.posX, this.posY, this.posZ);
//        Vec3d futurePosVec = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//        RayTraceResult hitObj = this.world.rayTraceBlocks(posVec, futurePosVec, false, true, false);
//
//        posVec = new Vec3d(this.posX, this.posY, this.posZ);
//        futurePosVec = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//
//        if( hitObj != null ) {
//            futurePosVec = new Vec3d(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z);
//        }
//
//        Entity entity = null;
//        AxisAlignedBB checkBB = this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D);
//        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, checkBB);
//        double minDist = 0.0D;
//
//        for( Entity collidedEntity : list ) {
//            if( collidedEntity.canBeCollidedWith() && collidedEntity != this.shooterCache ) {
//                AxisAlignedBB collisionAABB = collidedEntity.getEntityBoundingBox().grow(0.3D);
//                RayTraceResult interceptObj = collisionAABB.calculateIntercept(posVec, futurePosVec);
//
//                if( interceptObj != null ) {
//                    Entity validator = collidedEntity;
//                    if( collidedEntity instanceof MultiPartEntityPart ) {
//                        IEntityMultiPart iemp = ((MultiPartEntityPart) collidedEntity).parent;
//                        if( iemp instanceof Entity ) {
//                            validator = (Entity) iemp;
//                        }
//                    }
////                    if( collidedEntity instanceof EntityDragonPart ) {
////                        IEntityMultiPart multiEntity = ((EntityDragonPart) collidedEntity).entityDragonObj;
////                        if( multiEntity instanceof EntityDragon ) {
////                            dragonPart = (EntityDragonPart) collidedEntity;
////                            collidedEntity = (EntityDragon) multiEntity;
////                        }
////                    }
//
//                    double vecDistance = posVec.distanceTo(interceptObj.hitVec);
//                    boolean isClosest = vecDistance < minDist || minDist == 0.0D;
//
//                    if( (this.shooterCache == null || this.shooterCache.getTargetProcessor().isEntityValidTarget(validator)) && isClosest ) {
//                        entity = collidedEntity;
//                        minDist = vecDistance;
//                    }
//
////                    if( !EntityTurret.class.isAssignableFrom(collidedEntity.getClass()) && (vecDistance < minDist || minDist == 0.0D) ) {
////                        entity = collidedEntity;
////                        minDist = vecDistance;
////                    }
//                }
//            }
//        }
//
//        if( entity != null ) {
//            hitObj = new RayTraceResult(entity);
//        }
//
//        if( hitObj != null && hitObj.entityHit instanceof EntityPlayer ) {
//            EntityPlayer player = (EntityPlayer)hitObj.entityHit;
//
//            if( player.capabilities.disableDamage ) {
//                hitObj = null;
//            }
//        }
//
//        if( hitObj != null ) {
//            if( hitObj.entityHit != null ) {
//                MutableFloat dmg = new MutableFloat(this.delegate.getDamage() * this.attackModifier);
//
//                IProjectile.TargetType tgtType = this.getTargetType(hitObj.entityHit);
//                DamageSource damagesource = this.getProjDamageSource(hitObj.entityHit, tgtType);
//
//                if( this.isBurning() && !(hitObj.entityHit instanceof EntityEnderman) ) {
//                    hitObj.entityHit.setFire(5);
//                }
//
//                boolean preHitVelocityChanged = hitObj.entityHit.velocityChanged;
//                boolean preHitAirBorne = hitObj.entityHit.isAirBorne;
//                double preHitMotionX = hitObj.entityHit.motionX;
//                double preHitMotionY = hitObj.entityHit.motionY;
//                double preHitMotionZ = hitObj.entityHit.motionZ;
//
//                hitObj.entityHit.hurtResistantTime = 0;
//
//                if( hitObj.entityHit instanceof EntityCreature && this.shooterCache != null ) {
//                    TmrUtils.INSTANCE.setEntityTarget((EntityCreature) hitObj.entityHit, this.shooterCache);
//                }
//
//                if( this.delegate.onDamageEntityPre(this.shooterCache, this, hitObj.entityHit, damagesource, dmg) && hitObj.entityHit.attackEntityFrom(damagesource, dmg.floatValue()) ) {
//                    this.lastDamage = dmg.floatValue();
//                    this.lastDamaged = hitObj.entityHit;
//                    this.lastDamagedTimer = 0;
//
//                    hitObj.entityHit.velocityChanged = preHitVelocityChanged;
//                    hitObj.entityHit.isAirBorne = preHitAirBorne;
//                    hitObj.entityHit.motionX = preHitMotionX;
//                    hitObj.entityHit.motionY = preHitMotionY;
//                    hitObj.entityHit.motionZ = preHitMotionZ;
//
//                    this.delegate.onDamageEntityPost(this.shooterCache, this, hitObj.entityHit, damagesource);
//                    if( hitObj.entityHit instanceof EntityLivingBase ) {
//                        EntityLivingBase living = (EntityLivingBase) hitObj.entityHit;
//
//                        if( !this.world.isRemote ) {
//                            living.setArrowCountInEntity(living.getArrowCountInEntity() + 1);
//                        }
//
//                        double deltaX = this.posX - living.posX;
//                        double deltaZ = this.posZ - living.posZ;
//
//                        while( deltaX * deltaX + deltaZ * deltaZ < 0.0001D ) {
//                            deltaZ = (Math.random() - Math.random()) * 0.01D;
//                            deltaX = (Math.random() - Math.random()) * 0.01D;
//                        }
//
//                        this.knockBackEntity(living, deltaX, deltaZ);
//
//                        if( this.shooterCache != null ) {
//                            EnchantmentHelper.applyThornEnchantments(living, this.shooterCache);
//                            EnchantmentHelper.applyArthropodEnchantments(this.shooterCache, living);
//                        }
//                    }
//                }
//            }
//
//            if( this.delegate.onHit(this.shooterCache, this, hitObj) ) {
//                this.setPosition(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z);
//                this.playSound(this.delegate.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//                this.setDead();
//            }
//        }
    }



    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult rtResult) {
        super.onHitBlock(rtResult);

        Entity owner = this.getOwner();
        ITurretInst ownerTurret = owner instanceof ITurretInst ? (ITurretInst) owner : null;

        if( this.delegate.processImpact(ownerTurret, this, rtResult) && this.delegate.finishImpact(ownerTurret, this, rtResult) ) {
            this.playHitSound();
            if( !this.level.isClientSide ) {
                this.remove();
            }
        } else {
            this.lastBlockHit = rtResult.getBlockPos();
        }
    }

    @Override
    protected void onHitEntity(@Nonnull EntityRayTraceResult rtResult) {
        super.onHitEntity(rtResult);

        Entity owner = this.getOwner();
        ITurretInst ownerTurret = owner instanceof ITurretInst ? (ITurretInst) owner : null;
        Entity target = rtResult.getEntity();

        if( this.delegate.processImpact(ownerTurret, this, rtResult) ) {
            DamageSource dmgSource = this.getProjDamageSource(target, owner, this.getTargetType(target, owner));
            float damage = MathHelper.clamp(this.delegate.getDamage(ownerTurret, this, target, dmgSource, this.attackModifier), 0.0F, Integer.MAX_VALUE);

            if( owner instanceof LivingEntity ) {
                ((LivingEntity) owner).setLastHurtMob(target);
            }

            if( target.hurt(dmgSource, damage) ) {
                this.lastDamage = damage;
                this.lastDamagedId = target.getUUID();
                this.lastDamagedNetId = target.getId();
                this.lastDamagedEntity = new WeakReference<>(target);

                if( target instanceof LivingEntity ) {
                    float knockbackH = this.delegate.getKnockbackHorizontal();
                    float knockbackV = this.delegate.getKnockbackVertical();

                    if( knockbackH >= 0.0000001F ) {
                        Vector3d vector3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(knockbackH);
                        if( vector3d.lengthSqr() > 0.0D ) {
                            target.push(vector3d.x, knockbackV, vector3d.z);
                        }
                    }

                    this.delegate.onPostEntityDamage(ownerTurret, this, target, dmgSource);
                }
            }

            if( this.delegate.finishImpact(ownerTurret, this, rtResult) ) {
                this.playHitSound();
                if( !this.level.isClientSide ) {
                    this.remove();
                }
            }
        }
    }

    private void playHitSound() {
        SoundEvent ricochetSound = this.delegate.getRicochetSound();
        if( ricochetSound != null ) {
            this.playSound(ricochetSound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    public static DamageSource getDamageSource(ITurretInst turret, @Nonnull IProjectileInst projectile, IProjectile.TargetType type) {
        switch( type ) {
            case SPECIAL_ENDERMAN:
                return new DamageSourceHiddenProjectile(projectile.get(), turret).setThorns().setProjectile();
            case SPECIAL_ENDER_DRAGON:
                return new DamageSourceHiddenProjectile(projectile.get(), turret).setExplosion().setProjectile();
            case SPECIAL_WITHER:
                return new DamageSourceHiddenProjectile(projectile.get(), turret);
            default:
                return new DamageSourceIndirectProjectile(projectile.get(), turret);
        }
    }

    private DamageSource getProjDamageSource(Entity hitEntity, Entity owner, IProjectile.TargetType type) {
        ITurretInst ownerTurret = owner instanceof ITurretInst ? (ITurretInst) owner : null;
        return MiscUtils.defIfNull(this.delegate.getCustomDamageSource(ownerTurret, this, hitEntity, type),
                                   () -> getDamageSource(ownerTurret, this, type));
    }

    private IProjectile.TargetType getTargetType(Entity entity, Entity owner) {
        if( !(owner instanceof ITurretInst) ) {
            return IProjectile.TargetType.REGULAR;
        }

        ITurretInst ownerTurret = (ITurretInst) owner;
        //TODO: reimplement upgrades
        boolean hasToxinI = false;//this.shooterCache.getUpgradeProcessor().hasUpgrade(Upgrades.ENDER_TOXIN_I);
        boolean hasToxinII = hasToxinI && false;//this.shooterCache.getUpgradeProcessor().hasUpgrade(Upgrades.ENDER_TOXIN_II);

        if( entity instanceof EndermanEntity && hasToxinI ) {
            return IProjectile.TargetType.SPECIAL_ENDERMAN;
        } else if( (entity instanceof EnderDragonEntity
                       || (entity instanceof PartEntity && ((PartEntity<?>) entity).getParent() instanceof EnderDragonEntity))
                   && hasToxinII )
        {
            return IProjectile.TargetType.SPECIAL_ENDER_DRAGON;
        } else if( entity instanceof WitherEntity && hasToxinII ) {
            return IProjectile.TargetType.SPECIAL_WITHER;
        } else {
            return IProjectile.TargetType.REGULAR;
        }
    }

//    private void knockBackEntity(EntityLivingBase living, double deltaX, double deltaZ) {
//        if( this.rand.nextDouble() >= living.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue() ) {
//            living.isAirBorne = true;
//            double normXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
//            double kbStrengthXZ = this.delegate.getKnockbackHorizontal();
//            double kbStrengthY = this.delegate.getKnockbackVertical();
//            living.motionX /= 2.0D;
//            living.motionY /= 2.0D;
//            living.motionZ /= 2.0D;
//            living.motionX -= deltaX / normXZ * kbStrengthXZ;
//            living.motionY += kbStrengthY;
//            living.motionZ -= deltaZ / normXZ * kbStrengthXZ;
//
//            if( living.motionY > 0.4000000059604645D ) {
//                living.motionY = 0.4000000059604645D;
//            }
//        }
//    }

    private static final String NBT_DELEGATE_ID = "DelegateId";
    private static final String NBT_ATTACK_MODIFIER = "AttackModifier";
    private static final String NBT_AMMO_TYPE = "AmmoType";
    private static final String NBT_AMMO_SUBTYPE = "AmmoSubtype";
    private static final String NBT_LAST_DAMAGE  = "LastDamage";
    private static final String NBT_LAST_DAMAGED_ID  = "LastDamagedEntityId";

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);

        this.lastDamagedEntity = null;

        this.delegate = ProjectileRegistry.INSTANCE.get(new ResourceLocation(nbt.getString(NBT_DELEGATE_ID)));
        this.ammunition = AmmunitionRegistry.INSTANCE.get(new ResourceLocation(nbt.getString(NBT_AMMO_TYPE)));
        this.attackModifier = nbt.getFloat(NBT_ATTACK_MODIFIER);
        this.lastDamage = nbt.getFloat(NBT_LAST_DAMAGE);
        this.lastDamagedId = nbt.contains(NBT_LAST_DAMAGED_ID) ? nbt.getUUID(NBT_LAST_DAMAGED_ID) : null;
        this.ammoSubtype = nbt.contains(NBT_AMMO_SUBTYPE) ? nbt.getString(NBT_AMMO_SUBTYPE) : null;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.putString(NBT_DELEGATE_ID, this.delegate.getId().toString());
        nbt.putString(NBT_AMMO_TYPE, this.ammunition.getId().toString());
        nbt.putFloat(NBT_ATTACK_MODIFIER, this.attackModifier);
        nbt.putFloat(NBT_LAST_DAMAGE, this.lastDamage);

        if( this.ammoSubtype != null ) {
            nbt.putString(NBT_AMMO_SUBTYPE, this.ammoSubtype);
        }
        if( this.lastDamagedId != null ) {
            nbt.putUUID(NBT_LAST_DAMAGED_ID, this.lastDamagedId);
        }
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        Entity owner = this.getOwner();

        buffer.writeResourceLocation(this.delegate.getId());
        buffer.writeResourceLocation(this.ammunition.getId());
        buffer.writeFloat(this.xRot);
        buffer.writeFloat(this.yRot);
        buffer.writeBoolean(owner != null);
        if( owner != null ) {
            buffer.writeInt(owner.getId());
        }
        buffer.writeBoolean(this.ammoSubtype != null);
        if( this.ammoSubtype != null ) {
            buffer.writeUtf(this.ammoSubtype);
        }
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        this.delegate = ProjectileRegistry.INSTANCE.get(buffer.readResourceLocation());
        this.ammunition = AmmunitionRegistry.INSTANCE.get(buffer.readResourceLocation());
        this.xRot = buffer.readFloat();
        this.yRot = buffer.readFloat();

        if( buffer.readBoolean() ) {
            this.setOwner(this.level.getEntity(buffer.readInt()));
        }
        if( buffer.readBoolean() ) {
            this.ammoSubtype = buffer.readUtf();
        }
    }

    @Override
    public float getLastCausedDamage() {
        return this.lastDamage;
    }

    @Override
    public Entity getLastDamagedEntity() {
        if( this.lastDamagedId != null ) {
            Entity cachedEntity = this.lastDamagedEntity != null ? this.lastDamagedEntity.get() : null;
            if( cachedEntity == null ) {
                if( this.level instanceof ServerWorld ) {
                    cachedEntity = ((ServerWorld) this.level).getEntity(this.lastDamagedId);
                } else if( this.lastDamagedNetId != 0 ) {
                    cachedEntity = this.level.getEntity(this.lastDamagedNetId);
                }

                if( cachedEntity != null ) {
                    this.lastDamagedEntity = new WeakReference<>(cachedEntity);
                }
            }

            return cachedEntity;
        }

        return null;
    }

    @Override
    public Entity get() {
        return this;
    }

    @Nonnull
    @Override
    public IAmmunition getAmmunition() {
        return this.ammunition;
    }

    @Override
    public String getAmmunitionSubtype() {
        return this.ammoSubtype;
    }

    public interface ITurretDamageSource {
        ITurretInst getTurretInst();
    }

    public static class DamageSourceHiddenProjectile
            extends EntityDamageSource
            implements ITurretDamageSource
    {
        private final ITurretInst turretInst;

        DamageSourceHiddenProjectile(Entity projectile, ITurretInst turretInst) {
            super(TmrConstants.ID + ".turret", projectile);

            this.turretInst = turretInst;
        }

        @Nullable
        @Override
        public Entity getDirectEntity() {
            return this.turretInst != null ? this.turretInst.get() : this.entity;
        }

        @Nonnull
        @Override
        public ITextComponent getLocalizedDeathMessage(@Nonnull LivingEntity attacked) {
            return getDeathMessage(attacked, this.turretInst);
        }

        static ITextComponent getDeathMessage(LivingEntity attacked, ITurretInst turret) {
            ITextComponent turretOwner = turret.getOwnerName();
            ITextComponent turretName = turret.get().getName();

            if( !Strings.isNullOrEmpty(turretOwner.getString()) ) {
                turretName = new TranslationTextComponent(Lang.DEATH_OWNER.get(), turretOwner, turretName);
            }
            return new TranslationTextComponent(Lang.DEATH_TURRET.get(turret.getTurret().getId()), attacked.getDisplayName(), turretName);
        }

        @Override
        public ITurretInst getTurretInst() {
            return this.turretInst;
        }
    }

    public static class DamageSourceIndirectProjectile
            extends IndirectEntityDamageSource
            implements ITurretDamageSource
    {
        private final ITurretInst turretInst;

        DamageSourceIndirectProjectile(Entity projectile, ITurretInst turretInst) {
            super(TmrConstants.ID + ".turret", projectile, turretInst != null ? turretInst.get() : projectile);
            this.setProjectile();

            this.turretInst = turretInst;
        }

        @Override
        @Nonnull
        public ITextComponent getLocalizedDeathMessage(@Nonnull LivingEntity attacked) {
            return this.turretInst != null
                   ? DamageSourceHiddenProjectile.getDeathMessage(attacked, this.turretInst)
                   : super.getLocalizedDeathMessage(attacked);
        }

        @Override
        public ITurretInst getTurretInst() {
            return this.turretInst;
        }
    }
}
