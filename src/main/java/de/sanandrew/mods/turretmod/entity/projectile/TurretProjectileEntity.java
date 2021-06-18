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
import de.sanandrew.mods.turretmod.api.ammo.IProjectileEntity;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
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
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TurretProjectileEntity
        extends ProjectileEntity
        implements IEntityAdditionalSpawnData, IProjectileEntity
{
    private final LastDamagedList lastDamaged = new LastDamagedList();

    @Nonnull
    private IProjectile delegate;
    @Nonnull
    private IAmmunition ammunition;
    private String ammoSubtype;
    private double maxDist;
    private float attackModifier;
    private float lastDamage;
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

        if( owner instanceof ITurretEntity ) {
            this.maxDist = ((ITurretEntity) owner).getTargetProcessor().getRangeVal() * 4.0D;

            this.delegate.onShoot((ITurretEntity) owner, this);
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
    protected void defineSynchedData() { }

    @Override
    public void tick() {
        Entity        owner       = this.getOwner();
        ITurretEntity ownerTurret = owner instanceof ITurretEntity ? (ITurretEntity) owner : null;
        if( (owner != null && this.distanceTo(owner) > this.maxDist) || !this.delegate.isValid() ) {
            this.remove();
            return;
        }

        super.tick();
        this.delegate.tick(ownerTurret, this);

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

    }

    private void forceUpdateRotation(Vector3d moveVector) {
        float horizontalNormal = MathHelper.sqrt(getHorizontalDistanceSqr(moveVector));

        this.xRot = (float)(MathHelper.atan2(moveVector.y, horizontalNormal) * (double)(180F / (float)Math.PI));
        this.yRot = (float)(MathHelper.atan2(moveVector.x, moveVector.z) * (double)(180F / (float)Math.PI));

        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
    }

    @Override
    protected boolean canHitEntity(@Nonnull Entity entity) {
        Entity        owner       = this.getOwner();
        ITurretEntity ownerTurret = owner instanceof ITurretEntity ? (ITurretEntity) owner : null;

        boolean isTarget = true;
        if( ownerTurret != null ) {
            isTarget = ownerTurret.getTargetProcessor().isEntityValidTarget(entity);
        }

        if( this.lastDamaged.stream().anyMatch(e -> e.getLastDamagedEntity(this.level) == entity) ) { // do not attack the same entity twice
            return false;
        }

        return super.canHitEntity(entity) && entity != owner && isTarget;
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d currPos, Vector3d nextPos) {
        return ProjectileHelper.getEntityHitResult(this.level, this, currPos, nextPos, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    private void doCollisionCheck(Vector3d moveVector) {
        Vector3d pos = this.position();
        Vector3d       futurePos = pos.add(moveVector);
        RayTraceResult rtResult = null;

        // BLOCK COLLISION CHECKS
        if( this.lastBlockHit == null || !this.lastBlockHit.equals(this.blockPosition()) ) {
            RayTraceContext.FluidMode fluidMode = this.delegate.detectFluidCollision() ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE;
            rtResult  = this.level.clip(new RayTraceContext(pos, futurePos, RayTraceContext.BlockMode.COLLIDER, fluidMode, this));
            if( rtResult.getType() != RayTraceResult.Type.MISS ) {
                futurePos = rtResult.getLocation();
            }
        }

        // ENTITY COLLISION CHECKS
        while( this.isAlive() ) {
            EntityRayTraceResult hitEntityResult = this.findHitEntity(pos, futurePos);
            if( hitEntityResult != null ) {
                rtResult = hitEntityResult;
            }

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
    }

    @Override
    protected void onHitBlock(@Nonnull BlockRayTraceResult rtResult) {
        super.onHitBlock(rtResult);

        Entity        owner       = this.getOwner();
        ITurretEntity ownerTurret = owner instanceof ITurretEntity ? (ITurretEntity) owner : null;

        if( this.delegate.processImpact(ownerTurret, this, rtResult) && this.delegate.finishImpact(ownerTurret, this, rtResult) ) {
            this.playHitSound();
            this.remove();
        } else {
            this.lastBlockHit = rtResult.getBlockPos();
        }
    }

    @Override
    protected void onHitEntity(@Nonnull EntityRayTraceResult rtResult) {
        super.onHitEntity(rtResult);

        Entity        owner       = this.getOwner();
        ITurretEntity ownerTurret = owner instanceof ITurretEntity ? (ITurretEntity) owner : null;
        Entity        target      = rtResult.getEntity();

        if( this.delegate.processImpact(ownerTurret, this, rtResult) ) {
            DamageSource dmgSource = this.getProjDamageSource(target, owner, this.getTargetType(target, owner));
            float damage = MathHelper.clamp(this.delegate.getDamage(ownerTurret, this, target, dmgSource, this.attackModifier), 0.0F, Integer.MAX_VALUE);

            if( owner instanceof LivingEntity ) {
                ((LivingEntity) owner).setLastHurtMob(target);
            }

            if( target.hurt(dmgSource, damage) ) {
                this.lastDamage = damage;
                this.lastDamaged.addEntity(target);

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
                this.remove();
            }
        }
    }

    private void playHitSound() {
        SoundEvent ricochetSound = this.delegate.getRicochetSound();
        if( ricochetSound != null ) {
            this.playSound(ricochetSound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    public static DamageSource getDamageSource(ITurretEntity turret, @Nonnull IProjectileEntity projectile, IProjectile.TargetType type) {
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
        ITurretEntity ownerTurret = owner instanceof ITurretEntity ? (ITurretEntity) owner : null;
        return MiscUtils.defIfNull(this.delegate.getCustomDamageSource(ownerTurret, this, hitEntity, type),
                                   () -> getDamageSource(ownerTurret, this, type));
    }

    private IProjectile.TargetType getTargetType(Entity entity, Entity owner) {
        if( !(owner instanceof ITurretEntity) ) {
            return IProjectile.TargetType.REGULAR;
        }

        ITurretEntity ownerTurret = (ITurretEntity) owner;
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

    private static final String NBT_DELEGATE_ID = "DelegateId";
    private static final String NBT_ATTACK_MODIFIER = "AttackModifier";
    private static final String NBT_AMMO_TYPE = "AmmoType";
    private static final String NBT_AMMO_SUBTYPE = "AmmoSubtype";
    private static final String NBT_LAST_DAMAGE  = "LastDamage";

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);

        this.lastDamaged.clear();

        this.delegate = ProjectileRegistry.INSTANCE.get(new ResourceLocation(nbt.getString(NBT_DELEGATE_ID)));
        this.ammunition = AmmunitionRegistry.INSTANCE.get(new ResourceLocation(nbt.getString(NBT_AMMO_TYPE)));
        this.attackModifier = nbt.getFloat(NBT_ATTACK_MODIFIER);
        this.lastDamage = nbt.getFloat(NBT_LAST_DAMAGE);
        this.ammoSubtype = nbt.contains(NBT_AMMO_SUBTYPE) ? nbt.getString(NBT_AMMO_SUBTYPE) : null;

        this.lastDamaged.loadFromTag(nbt);
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

        this.lastDamaged.saveToTag(nbt);
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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

        this.lastDamaged.saveToPacket(buffer, this.level);
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

        this.lastDamaged.loadFromPacket(buffer, this.level);
    }

    @Override
    public float getLastCausedDamage() {
        return this.lastDamage;
    }

    @Nonnull
    @Override
    public Entity[] getLastDamagedEntities() {
        return this.lastDamaged.getEntities(this.level);
    }

    @Override
    public Entity get() {
        return this;
    }

    @Override
    public IProjectile getDelegate() {
        return this.delegate;
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
        ITurretEntity getTurretInst();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldShowName() {
        return false;
    }

    public static class DamageSourceHiddenProjectile
            extends EntityDamageSource
            implements ITurretDamageSource
    {
        private final ITurretEntity turretInst;

        DamageSourceHiddenProjectile(Entity projectile, ITurretEntity turretInst) {
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

        static ITextComponent getDeathMessage(LivingEntity attacked, ITurretEntity turret) {
            ITextComponent turretOwner = turret.getOwnerName();
            ITextComponent turretName = turret.get().getName();

            if( !Strings.isNullOrEmpty(turretOwner.getString()) ) {
                turretName = new TranslationTextComponent(Lang.DEATH_OWNER.get(), turretOwner, turretName);
            }
            return new TranslationTextComponent(Lang.DEATH_TURRET.get(turret.getDelegate().getId()), attacked.getDisplayName(), turretName);
        }

        @Override
        public ITurretEntity getTurretInst() {
            return this.turretInst;
        }
    }

    public static class DamageSourceIndirectProjectile
            extends IndirectEntityDamageSource
            implements ITurretDamageSource
    {
        private final ITurretEntity turretInst;

        DamageSourceIndirectProjectile(Entity projectile, ITurretEntity turretInst) {
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
        public ITurretEntity getTurretInst() {
            return this.turretInst;
        }
    }

    private static class LastDamagedEntry
    {
        @Nonnull
        private final UUID lastDamagedId;
        private       int  lastDamagedNetId;
        private       WeakReference<Entity> lastDamagedEntity;

        private LastDamagedEntry(Entity e) {
            this.lastDamagedId = e.getUUID();
            this.lastDamagedNetId = e.getId();
            this.lastDamagedEntity = new WeakReference<>(e);
        }

        private LastDamagedEntry(@Nonnull UUID id) {
            this.lastDamagedId = id;
        }

        public Entity getLastDamagedEntity(World level) {
            Entity cachedEntity = this.lastDamagedEntity != null ? this.lastDamagedEntity.get() : null;
            if( cachedEntity == null ) {
                if( level instanceof ServerWorld ) {
                    cachedEntity = ((ServerWorld) level).getEntity(this.lastDamagedId);
                } else if( this.lastDamagedNetId != 0 ) {
                    cachedEntity = level.getEntity(this.lastDamagedNetId);
                }

                if( cachedEntity != null ) {
                    this.lastDamagedEntity = new WeakReference<>(cachedEntity);
                }
            }

            return cachedEntity;
        }
    }

    private static class LastDamagedList
            extends ArrayList<LastDamagedEntry>
    {
        private static final String NBT_LAST_DAMAGED_IDS = "LastDamagedEntityIds";

        private void addEntity(Entity e) {
            this.add(new LastDamagedEntry(e));
        }

        private Entity[] getEntities(World level) {
            return this.stream().map(e -> e.getLastDamagedEntity(level)).toArray(Entity[]::new);
        }

        private void loadFromTag(CompoundNBT nbt) {
            ListNBT ids = nbt.getList(NBT_LAST_DAMAGED_IDS, Constants.NBT.TAG_INT_ARRAY);

            this.clear();

            for( INBT id : ids ) {
                this.add(new LastDamagedEntry(NBTUtil.loadUUID(id)));
            }
        }

        private void saveToTag(CompoundNBT nbt) {
            ListNBT ids = new ListNBT();

            for( LastDamagedEntry entry : this ) {
                ids.add(NBTUtil.createUUID(entry.lastDamagedId));
            }

            nbt.put(NBT_LAST_DAMAGED_IDS, ids);
        }

        private void saveToPacket(PacketBuffer buffer, World level) {
            List<Integer> ids = new ArrayList<>();

            for( LastDamagedEntry entry : this ) {
                Entity e = entry.getLastDamagedEntity(level);
                if( e != null ) {
                    ids.add(e.getId());
                }
            }

            buffer.writeVarInt(ids.size());
            for( Integer id : ids ) {
                buffer.writeVarInt(id);
            }
        }

        private void loadFromPacket(PacketBuffer buffer, World level) {
            this.clear();

            for( int i = 0, max = buffer.readVarInt(); i < max ; i++ ) {
                Entity e = level.getEntity(buffer.readVarInt());
                if( e != null ) {
                    this.add(new LastDamagedEntry(e));
                }
            }
        }
    }
}
