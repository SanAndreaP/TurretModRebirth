/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectile;
import de.sanandrew.mods.turretmod.api.ammo.ITurretProjectileInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.projectile.ProjectileRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"SuspiciousNameCombination", "BooleanMethodNameMustStartWithQuestion"})
public class EntityTurretProjectile
        extends Entity
        implements IProjectile, IEntityAdditionalSpawnData, ITurretProjectileInst
{
    public ITurretProjectile delegate;
    private UUID shooterUUID;
    private EntityTurret shooterCache;
    private Entity lastDamaged;
    private int lastDamagedTimer;

    private double maxDist;
    private float lastDamage;

    public EntityTurretProjectile(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.maxDist = Integer.MAX_VALUE;

        this.lastDamage = -1.0F;
    }

    public EntityTurretProjectile(World world, ITurretProjectile delegate, EntityTurret shooter, Entity target) {
        this(world, delegate, shooter, target, null);
    }

    public EntityTurretProjectile(World world, ITurretProjectile delegate, EntityTurret shooter, Vec3d shootingVec) {
        this(world, delegate, shooter, null, shootingVec);
    }

    private EntityTurretProjectile(World world, ITurretProjectile delegate, EntityTurret shooter, Entity target, Vec3d shootingVec) {
        this(world);

        this.delegate = delegate;
        this.shooterUUID = shooter.getUniqueID();
        this.shooterCache = shooter;

        double y = shooter.posY + shooter.getEyeHeight() - 0.1D;

        this.setPosition(shooter.posX, y, shooter.posZ);

        if( shooter.isBuoy() ) {
            y -= 1.0D;
        }
        this.maxDist = shooter.getTargetProcessor().getRangeVal() * 4.0D;

        Vec3d targetVec;
        if( target != null ) {
            targetVec = new Vec3d(target.posX - shooter.posX, (target.getEntityBoundingBox().minY + target.height / 1.4D) - y, target.posZ - shooter.posZ);
        } else if( shootingVec != null ) {
            targetVec = shootingVec;
        } else {
            targetVec = new Vec3d(0.0D, -1.0D, 0.0D);
        }

        this.setHeadingFromVec(targetVec.normalize());
        this.motionY += this.delegate.getArc() * Math.sqrt(targetVec.x * targetVec.x + targetVec.z * targetVec.z) * 0.05;

        this.delegate.onCreate(this.shooterCache, this);
    }

    private void setHeadingFromVec(Vec3d vector) {
        double scatterVal = this.delegate.getScatterValue();
        float initSpeed = this.delegate.getSpeed();

        this.motionX = vector.x * initSpeed + (MiscUtils.RNG.randomDouble() * 2.0D - 1.0D) * scatterVal;
        this.motionZ = vector.z * initSpeed + (MiscUtils.RNG.randomDouble() * 2.0D - 1.0D) * scatterVal;
        this.motionY = vector.y * initSpeed + (MiscUtils.RNG.randomDouble() * 2.0D - 1.0D) * scatterVal;

        float vecPlaneNormal = MathHelper.sqrt(vector.x * vector.x + vector.z * vector.z);

        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(vector.x, vector.z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(vector.y, vecPlaneNormal) * 180.0D / Math.PI);
    }

    @Override
    protected void entityInit() {
        if( this.shooterUUID != null && this.shooterCache == null ) {
            this.shooterCache = (EntityTurret) EntityUtils.getEntityByUUID(this.world, this.shooterUUID);
        }
    }

    @Override
    public void onUpdate() {
        this.isAirBorne = true;

        if( (this.shooterCache != null && this.getDistance(this.shooterCache) > this.maxDist) || this.delegate == null ) {
            this.setDead();
            return;
        }

        if( this.lastDamaged != null && ++this.lastDamagedTimer >= 20 ) {
            this.lastDamaged = null;
            this.lastDamagedTimer = 0;
        }

        this.doCollisionCheck();

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
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
        float speed = this.delegate.getSpeedMultiplierAir();

        if( this.isInWater() ) {
            for( int i = 0; i < 4; i++ ) {
                float disPos = 0.25F;
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * disPos, this.posY - this.motionY * disPos, this.posZ - this.motionZ * disPos, this.motionX, this.motionY, this.motionZ);
            }

            //TODO: make speed dependend on viscosity
            speed = this.delegate.getSpeedMultiplierLiquid();
        }

        if( this.isWet() ) {
            this.extinguish();
        }

        this.motionX *= speed;
        this.motionY *= speed;
        this.motionZ *= speed;
        this.motionY -= this.delegate.getArc() * 0.1F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.doBlockCollisions();

        this.delegate.onUpdate(this.shooterCache, this);
    }

    private void doCollisionCheck() {
        Vec3d posVec = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d futurePosVec = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult hitObj = this.world.rayTraceBlocks(posVec, futurePosVec, false, true, false);

        posVec = new Vec3d(this.posX, this.posY, this.posZ);
        futurePosVec = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if( hitObj != null ) {
            futurePosVec = new Vec3d(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z);
        }

        Entity entity = null;
        AxisAlignedBB checkBB = this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, checkBB);
        double minDist = 0.0D;

        for( Entity collidedEntity : list ) {
            if( collidedEntity.canBeCollidedWith() && collidedEntity != this.shooterCache ) {
                AxisAlignedBB collisionAABB = collidedEntity.getEntityBoundingBox().grow(0.3D);
                RayTraceResult interceptObj = collisionAABB.calculateIntercept(posVec, futurePosVec);

                if( interceptObj != null ) {
//                    if( collidedEntity instanceof EntityDragonPart ) {
//                        IEntityMultiPart multiEntity = ((EntityDragonPart) collidedEntity).entityDragonObj;
//                        if( multiEntity instanceof EntityDragon ) {
//                            dragonPart = (EntityDragonPart) collidedEntity;
//                            collidedEntity = (EntityDragon) multiEntity;
//                        }
//                    }

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

        if( hitObj != null && hitObj.entityHit instanceof EntityPlayer ) {
            EntityPlayer player = (EntityPlayer)hitObj.entityHit;

            if( player.capabilities.disableDamage ) {
                hitObj = null;
            }
        }

        if( hitObj != null ) {
            if( hitObj.entityHit != null ) {
                MutableFloat dmg = new MutableFloat(this.delegate.getDamage());

                DamageSource damagesource = this.getProjDamageSource(hitObj.entityHit);

                if( this.isBurning() && !(hitObj.entityHit instanceof EntityEnderman) ) {
                    hitObj.entityHit.setFire(5);
                }

                boolean preHitVelocityChanged = hitObj.entityHit.velocityChanged;
                boolean preHitAirBorne = hitObj.entityHit.isAirBorne;
                double preHitMotionX = hitObj.entityHit.motionX;
                double preHitMotionY = hitObj.entityHit.motionY;
                double preHitMotionZ = hitObj.entityHit.motionZ;

                if( hitObj.entityHit instanceof EntityLivingBase ) {
                    ((EntityLivingBase) hitObj.entityHit).hurtResistantTime = 0;
                }

                if( this.delegate.onDamageEntityPre(this.shooterCache, this, hitObj.entityHit, damagesource, dmg) && hitObj.entityHit.attackEntityFrom(damagesource, dmg.floatValue()) ) {
                    this.lastDamage = dmg.floatValue();
                    this.lastDamaged = hitObj.entityHit;
                    this.lastDamagedTimer = 0;

                    hitObj.entityHit.velocityChanged = preHitVelocityChanged;
                    hitObj.entityHit.isAirBorne = preHitAirBorne;
                    hitObj.entityHit.motionX = preHitMotionX;
                    hitObj.entityHit.motionY = preHitMotionY;
                    hitObj.entityHit.motionZ = preHitMotionZ;

                    this.delegate.onDamageEntityPost(this.shooterCache, this, hitObj.entityHit, damagesource);
                    if( hitObj.entityHit instanceof EntityLivingBase ) {
                        EntityLivingBase living = (EntityLivingBase) hitObj.entityHit;

                        if( !this.world.isRemote ) {
                            living.setArrowCountInEntity(living.getArrowCountInEntity() + 1);
                        }

                        if( living instanceof EntityCreature && this.shooterCache != null ) {
                            TmrUtils.INSTANCE.setEntityTarget((EntityCreature) living, this.shooterCache);
                        }

                        double deltaX = this.posX - living.posX;
                        double deltaZ = this.posZ - living.posZ;

                        while( deltaX * deltaX + deltaZ * deltaZ < 0.0001D ) {
                            deltaZ = (Math.random() - Math.random()) * 0.01D;
                            deltaX = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.knockBackEntity(living, deltaX, deltaZ);

                        if( this.shooterCache != null ) {
                            EnchantmentHelper.applyThornEnchantments(living, this.shooterCache);
                            EnchantmentHelper.applyArthropodEnchantments(this.shooterCache, living);
                        }
                    }
                }
            }

            if( this.delegate.onHit(this.shooterCache, this, hitObj) ) {
                this.setPosition(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z);
                this.playSound(this.delegate.getRicochetSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.setDead();
            }
        }
    }

    public static DamageSource getDamageSource(ITurretInst turret, @Nonnull ITurretProjectileInst projectile, ITurretProjectile.TargetType type) {
        switch( type ) {
            case SPECIAL_ENDERMAN:
                return new DamageSourceProjectile(projectile.get(), turret);
            case SPECIAL_ENDER_DRAGON:
                return new DamageSourceProjectile(projectile.get(), turret).setExplosion();
            default:
                return new DamageSourceIndirectProjectile(projectile.get(), turret);
        }
    }

    private DamageSource getProjDamageSource(Entity hitEntity) {
        return MiscUtils.defIfNull(this.delegate.getCustomDamageSrc(this.shooterCache, this, hitEntity, ITurretProjectile.TargetType.REGULAR),
                                   () -> {
                                       ITurretProjectile.TargetType type = ITurretProjectile.TargetType.REGULAR;
                                       if( hitEntity instanceof EntityEnderman && false ) { //TODO: add upgrade to be able to damage endermen
                                           type = ITurretProjectile.TargetType.SPECIAL_ENDERMAN;
                                       } else if( false ) {//TODO: add upgrade to be able to damage the ender dragon
                                           if( hitEntity instanceof EntityDragon ) {
                                               type = ITurretProjectile.TargetType.SPECIAL_ENDER_DRAGON;
                                           } else if( hitEntity instanceof MultiPartEntityPart ) {
                                               if( ((MultiPartEntityPart) hitEntity).parent instanceof EntityDragon ) {
                                                   type = ITurretProjectile.TargetType.SPECIAL_ENDER_DRAGON;
                                               }
                                           }
                                       }

                                       return getDamageSource(this.shooterCache, this, type);
                                   });
    }

    private void knockBackEntity(EntityLivingBase living, double deltaX, double deltaZ) {
        if( this.rand.nextDouble() >= living.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue() ) {
            living.isAirBorne = true;
            double normXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            double kbStrengthXZ = this.delegate.getKnockbackHorizontal();
            double kbStrengthY = this.delegate.getKnockbackVertical();
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

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.delegate = ProjectileRegistry.INSTANCE.getProjectile(UUID.fromString(nbt.getString("DelegateId")));
        if( this.delegate == null ) {
            this.setDead();
        }
        if( nbt.hasKey("shooter") ) {
            this.shooterUUID = UUID.fromString(nbt.getString("shooter"));
        }
        this.lastDamage = nbt.hasKey("LastDamage") ? nbt.getFloat("LastDamage") : Float.MAX_VALUE;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setString("DelegateId", this.delegate.getId().toString());
        if( this.shooterUUID != null ) {
            nbt.setString("shooter", this.shooterUUID.toString());
        }
    }

    @Override
    public void shoot(double x, double y, double z, float recoil, float randMulti) {
        float vecNormal = MathHelper.sqrt(x * x + y * y + z * z);
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
        float vecPlaneNormal = MathHelper.sqrt(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, vecPlaneNormal) * 180.0D / Math.PI);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeLong(this.delegate.getId().getMostSignificantBits());
        buffer.writeLong(this.delegate.getId().getLeastSignificantBits());
        buffer.writeFloat(this.rotationYaw);
        buffer.writeFloat(this.rotationPitch);
        buffer.writeBoolean(this.shooterCache != null);
        if( this.shooterCache != null ) {
            buffer.writeInt(this.shooterCache.getEntityId());
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.delegate = ProjectileRegistry.INSTANCE.getProjectile(new UUID(buffer.readLong(), buffer.readLong()));
        this.rotationYaw = buffer.readFloat();
        this.rotationPitch = buffer.readFloat();

        if( buffer.readBoolean() ) {
            this.shooterCache = (EntityTurret) this.world.getEntityByID(buffer.readInt());
        }
    }

    @Override
    public float getLastCausedDamage() {
        return this.lastDamage;
    }

    @Override
    public Entity getLastDamagedEntity() {
        return this.lastDamaged;
    }

    @Override
    public Entity get() {
        return this;
    }

    public static class DamageSourceProjectile
            extends EntityDamageSource
    {
        private final ITurretInst turret;

        DamageSourceProjectile(Entity projectile, ITurretInst turret) {
            super(TmrConstants.ID + ".turret", projectile);
            this.setProjectile();

            this.turret = turret;
        }

        @Nullable
        @Override
        public Entity getImmediateSource() {
            return this.damageSourceEntity;
        }

        @Nullable
        @Override
        public Entity getTrueSource() {
            return this.turret.get();
        }

        @Override
        public ITextComponent getDeathMessage(EntityLivingBase attacked) {
            return getDeathMessage(attacked, this.turret, this.damageType);
        }

        static ITextComponent getDeathMessage(EntityLivingBase attacked, ITurretInst turret, String damageType) {
            String turretOwner = turret.getOwnerName();
            String turretName = turret.get().getDisplayName().getFormattedText();
            if( !Strings.isNullOrEmpty(turretOwner) ) {
                turretName = turretOwner + (turretOwner.endsWith("s") ? "' " : "s' ") + turretName;
            }
            String s = "death.attack." + damageType + '.' + turret.getTurret().getName();
            return new TextComponentTranslation(s, attacked.getDisplayName(), turretName);
        }
    }

    public static class DamageSourceIndirectProjectile
            extends EntityDamageSourceIndirect
    {
        private final ITurretInst turret;

        DamageSourceIndirectProjectile(Entity projectile, ITurretInst turret) {
            super(TmrConstants.ID + ".turret", projectile, turret.get());
            this.setProjectile();

            this.turret = turret;
        }

        @Nullable
        @Override
        public Entity getImmediateSource() {
            return this.damageSourceEntity;
        }

        @Nullable
        @Override
        public Entity getTrueSource() {
            return this.turret.get();
        }

        @Override
        public ITextComponent getDeathMessage(EntityLivingBase attacked) {
            return DamageSourceProjectile.getDeathMessage(attacked, this.turret, this.damageType);
        }
    }
}
