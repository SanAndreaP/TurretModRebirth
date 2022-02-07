/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import com.mojang.authlib.GameProfile;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.entity.EntityRegistry;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import de.sanandrew.mods.turretmod.item.TurretItem;
import de.sanandrew.mods.turretmod.network.SyncTurretStatePacket;
import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class TurretEntity
        extends MobEntity
        implements IEntityAdditionalSpawnData, ITurretEntity
{
    private static final AxisAlignedBB UPWARDS_BLOCK = new AxisAlignedBB(0.1D, 0.99D, 0.1D, 1.0D, 1.0D, 1.0D);

    private static final DataParameter<Boolean> DATA_SHOT_CHANGED = EntityDataManager.defineId(TurretEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String>  DATA_VARIANT   = EntityDataManager.defineId(TurretEntity.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> DATA_IS_ACTIVE = EntityDataManager.defineId(TurretEntity.class, DataSerializers.BOOLEAN);

    public static final String NBT_TURRET_ID      = "TurretId";
    public static final String NBT_TURRET_NAME    = "TurretName";
    public static final String NBT_IS_ACTIVE      = "IsActive";
    public static final String NBT_TURRET_VARIANT = "Variant";
    public static final String NBT_OWNER          = "Owner";
    public static final String NBT_OWNER_ID       = "Id";
    public static final String NBT_OWNER_NAME     = "Name";

    private final TargetProcessor targetProc;
    private final UpgradeProcessor upgProc;

    @Nonnull
    private ITurret delegate;
    private boolean showRange;
    public  boolean        inGui;
    @Nonnull
    private UUID           ownerId;
    @Nonnull
    private ITextComponent ownerName;
    private boolean        prevShotChng;
    private ITurretRAM     turretRAM;

    public TurretEntity(EntityType<TurretEntity> type, World world) {
        super(type, world);
        this.ownerId = UuidUtils.EMPTY_UUID;
        this.ownerName = StringTextComponent.EMPTY;
        this.targetProc = new TargetProcessor(this);
        this.upgProc = new UpgradeProcessor(this);
        this.yRot = 0.0F;
        this.delegate = TurretRegistry.INSTANCE.getDefault();
    }

    public TurretEntity(World world, ITurret delegate, Vector3d pos) {
        this(EntityRegistry.TURRET, world);

        this.loadDelegate(delegate);
        this.setHealth(this.getMaxHealth());
        this.setPos(pos.x, pos.y, pos.z);
        this.setRot(world.random.nextFloat() * 360.0F, 0.0F);

        this.yHeadRot = this.yBodyRot;
        this.yHeadRotO = this.yBodyRot;
    }

    public TurretEntity(World world, PlayerEntity owner, ITurret delegate, Vector3d pos) {
        this(world, delegate, pos);

        this.ownerId = owner.getUUID();
        this.ownerName = owner.getName();
    }

//region Sounds
    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource dmgSrc) {
        return this.delegate.getHurtSound(this);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.delegate.getDeathSound(this);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.delegate.getIdleSound(this);
    }

    @Override
    public SoundEvent getShootSound() {
        return this.delegate.getShootSound(this);
    }

    @Override
    public SoundEvent getNoAmmoSound() {
        return MiscUtils.get(this.delegate.getEmptySound(this), SoundEvents.DISPENSER_FAIL);
    }

    protected void playPickupSound() {
        SoundEvent pickupSound = this.delegate.getPickupSound(this);
        if( pickupSound != null ) {
            this.level.playSound(null, this, pickupSound, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
    }
//endregion

//region Movement
    @Override
    public void lookAt(Entity entity, float yawSpeed, float pitchSpeed) {
        double deltaX = entity.getX() - this.getX();
        double deltaZ = entity.getZ() - this.getZ();
        double deltaY;

        if( entity instanceof LivingEntity ) {
            LivingEntity living = (LivingEntity) entity;
            deltaY = (living.getY() + living.getEyeHeight()) - (this.getY() + this.getEyeHeight());
        } else {
            deltaY = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - (this.getY() + this.getEyeHeight());
        }

        double distXZ     = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float  yawRotation   = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
        float  pitchRotation = (float) -(MathHelper.atan2(deltaY, distXZ) * 180.0D / Math.PI);
        this.xRot = calcRotation(this.xRot, pitchRotation);
        this.yHeadRot = calcRotation(this.yHeadRot, yawRotation);
    }

    @Override
    public void setYBodyRot(float rotation) { /* turrets are stationary, no body rotation allowed */ }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpHeadTo(float rotation, int lerpTicks) { /* NO lerping head, ONLY rotate in tick >:( */ }

    @Override
    public int getHeadRotSpeed() {
        return 50;
    }

    @Override
    public void knockback(float strength, double ratioX, double ratioZ) { /* turrets can't be knocked back */ }

    @Override
    public void push(@Nonnull Entity entity) { /* turrets can't be pushed by entities */ }

    @Override
    public void push(double dx, double dy, double dz) { /* turrets can't be pushed by entities */ }

    @Override
    public void travel(@Nonnull Vector3d travelVector) {
        double       motionY = this.getDeltaMovement().y;
        final double height  = this.getBbHeight();

        if( this.delegate.isBuoy() ) {
            Vector3d dm = this.getDeltaMovement();
            if( this.isSubmergedInLiquid(height + 0.2F) ) {
                motionY += 0.08F;
            } else if( this.isSubmergedInLiquid(height + 0.05F) ) {
                motionY += 0.005F;
            } else if( !this.isSubmergedInLiquid(height - 0.2F) ) {
                motionY -= 0.08F;
            } else {
                motionY -= 0.005F;
            }

            this.setDeltaMovement(dm.x, motionY * 0.98D, dm.z);
        }

        super.travel(travelVector);
    }

    @Override
    public void move(@Nonnull MoverType typeIn, @Nonnull Vector3d pos) {
        if( typeIn != MoverType.PLAYER ) {
            super.move(typeIn, pos);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    private static float calcRotation(float prevRotation, float newRotation) {
        final float speed = 20.0F;
        float       part  = MathHelper.wrapDegrees(newRotation - prevRotation);

        if( part > speed ) {
            part = speed;
        }

        if( part < -speed ) {
            part = -speed;
        }

        return prevRotation + part;
    }
//endregion

//region Getters/Setters
    @Override
    @SuppressWarnings("ConstantConditions")
    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return this.delegate != null ? this.delegate.getEyeHeight(poseIn, sizeIn) : super.getStandingEyeHeight(poseIn, sizeIn);
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        return effect.isAmbient();
    }

    @Override
    public boolean isGlowing() {
        return super.isGlowing() || TurretModRebirth.PROXY.checkTurretGlowing(this);
    }

    @Override
    @Nonnull
    public ItemStack getPickedResult(RayTraceResult target) {
        ItemStack pickedItem = TurretRegistry.INSTANCE.getItem(this.delegate.getId());
        if( this.delegate instanceof IVariantHolder ) {
            IVariant variant = this.getVariant();

            if( !((IVariantHolder) this.delegate).isDefaultVariant(variant) ) {
                new TurretItem.TurretStats(null, null, variant).updateData(pickedItem);
            }
        }

        return pickedItem;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Nonnull
    @Override
    protected ITextComponent getTypeName() {
        return new TranslationTextComponent(LangUtils.ENTITY_NAME.get(this.delegate.getId()));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return this.delegate.isBuoy();
    }

    @Override
    public boolean wasShooting() {
        boolean shot = this.entityData.get(DATA_SHOT_CHANGED) != this.prevShotChng;
        this.prevShotChng = this.entityData.get(DATA_SHOT_CHANGED);
        return shot;
    }

    @Override
    public void setShooting() {
        this.entityData.set(DATA_SHOT_CHANGED, !this.entityData.get(DATA_SHOT_CHANGED));
    }

    @Override
    public ITargetProcessor getTargetProcessor() {
        return this.targetProc;
    }

    @Override
    public IUpgradeProcessor getUpgradeProcessor() {
        return this.upgProc;
    }

    @Override
    public boolean isActive() {
        return this.entityData.get(DATA_IS_ACTIVE);
    }

    @Override
    public void setActive(boolean isActive) {
        this.entityData.set(DATA_IS_ACTIVE, isActive);
    }

    @Override
    public boolean shouldShowRange() {
        return this.showRange;
    }

    @Override
    public void setShowRange(boolean showRange) {
        this.showRange = showRange;
    }

    @Nonnull
    @Override
    public ITextComponent getOwnerName() {
        return this.ownerName;
    }

    @Override
    public boolean hasPlayerPermission(PlayerEntity player) {
        if( player == null ) {
            return false;
        }

        if( this.ownerId.equals(UuidUtils.EMPTY_UUID) ) {
            return true;
        }

        MinecraftServer mcSrv   = this.level.getServer();
        GameProfile     profile = player.getGameProfile();

        if( mcSrv != null && mcSrv.isSingleplayer() && mcSrv.getSingleplayerName().equals(profile.getName()) ) {
            return true;
        }

        //TODO: re-implement config
        return isOwner(player);
//        if( TmrUtils.INSTANCE.canPlayerEditAll() || this.ownerId.equals(profile.getId()) ) {
//            return true;
//        }
//
//        return player.hasPermissionLevel(2) && TmrUtils.INSTANCE.canOpEditAll();
    }

    @Override
    public boolean isOwner(PlayerEntity player) {
        return this.ownerId.equals(player.getUUID());
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        return this.level.getPlayerByUUID(this.ownerId);
    }

    @Override
    public boolean isInGui() {
        return this.inGui;
    }

    @Override
    public <V extends ITurretRAM> V getRAM(Supplier<V> onNull) {
        if( this.turretRAM == null && onNull != null ) {
            this.turretRAM = onNull.get();
        }
        return ReflectionUtils.getCasted(this.turretRAM);
    }

    @Override
    public AxisAlignedBB getRangeBB() {
        return this.delegate.getRangeBB(this);
    }

    @Override
    public boolean isBuoy() {
        return this.delegate.isBuoy();
    }

    @Override
    public LivingEntity get() {
        return this;
    }

    @Nonnull
    @Override
    public ITurret getDelegate() {
        return this.delegate;
    }

    @Override
    public ITurret.TargetType getAttackType() {
        return this.delegate.getTargetType();
    }

    @Override
    public IVariant getVariant() {
        if( this.delegate instanceof IVariantHolder ) {
            IVariantHolder vh = (IVariantHolder) this.delegate;
            return vh.hasVariants() ? vh.getVariant(this.entityData.get(DATA_VARIANT)) : null;
        }

        return null;
    }

    @Override
    public void setVariant(Object variantId) {
        if( this.delegate instanceof IVariantHolder && ((IVariantHolder) this.delegate).hasVariants() ) {
            this.entityData.set(DATA_VARIANT, variantId.toString());
        }
    }

    @Override
    public boolean canRemoteTransfer() {
        return this.getUpgradeProcessor().canAccessRemotely();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getPartBrightnessForRender(double partY) {
        BlockPos bpos = new BlockPos(MathHelper.floor(this.getX()), MathHelper.floor(this.getY() + partY), MathHelper.floor(this.getZ()));

        if( this.level.isLoaded(bpos) ) {
            return this.level.getRawBrightness(bpos, 0);
        } else {
            return 0;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getCameraQuality() {
        return 48;
    }

    @Override
    public ITextComponent getTurretTypeName() {
        return this.getTypeName();
    }

    private boolean isSubmergedInLiquid(double heightMod) {
        BlockPos pos = new BlockPos(this.getX(), this.getY() + heightMod, this.getZ());
        return this.level.getBlockState(pos).getMaterial().isLiquid();
    }

    private static boolean isAABBInside(AxisAlignedBB bb1) {
        return bb1.minX <= TurretEntity.UPWARDS_BLOCK.minX && bb1.minY <= TurretEntity.UPWARDS_BLOCK.minY && bb1.minZ <= TurretEntity.UPWARDS_BLOCK.minZ
               && bb1.maxX >= TurretEntity.UPWARDS_BLOCK.maxX && bb1.maxY >= TurretEntity.UPWARDS_BLOCK.maxY && bb1.maxZ >= TurretEntity.UPWARDS_BLOCK.maxZ;
    }

    public static boolean canTurretBePlaced(ITurret delegate, World level, BlockPos pos, boolean doBlockCheckOnly) {
        VoxelShape blockBB = level.getBlockState(pos).getCollisionShape(level, pos);
        boolean    buoyant = delegate.isBuoy();
        if( !buoyant && !blockBB.isEmpty() && !isAABBInside(blockBB.bounds()) ) {
            return false;
        }

        BlockPos posPlaced  = pos.above();
        BlockPos posPlaced2 = buoyant ? pos : posPlaced.above();
        if( !level.getBlockState(posPlaced).getMaterial().isReplaceable()
            || !level.getBlockState(posPlaced2).getMaterial().isReplaceable() )
        {
            return false;
        }

        if( !doBlockCheckOnly ) {
            AxisAlignedBB aabb = new AxisAlignedBB(posPlaced).move(0, buoyant ? -2 : 0, 0);
            return level.getEntitiesOfClass(TurretEntity.class, aabb).isEmpty();
        }

        return true;
    }
//endregion

//region Core
    @Override
    public void tick() {
        if( !this.inGui ) {
            super.tick();

            this.yBodyRot = this.yBodyRotO = 0.0F;

            this.delegate.tick(this);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();

        final IProfiler profiler = this.level.getProfiler();

        profiler.push("turretAI");

        if( this.isImmobile() ) {
            this.jumping = false;
        } else if( !this.level.isClientSide ) {
            this.yBodyRot = 0.0F;
        }
//        this.xxa = 0.0F; // moveStrafing
//        this.zza = 0.0F; // moveForward

        this.upgProc.onTick();

        if( !this.level.isClientSide ) {
            this.targetProc.onTick();
        } else {
            this.targetProc.onTickClient();
        }

        if( this.isActive() ) {
            if( this.targetProc.hasTarget() ) {
                this.lookAt(this.targetProc.getTarget(), 10.0F, this.getHeadRotSpeed());
            } else if( !(this.getControllingPassenger() instanceof PlayerEntity) ) {
                this.yHeadRot = MiscUtils.wrap360(this.yHeadRot + 1.0F);
                this.yHeadRotO = MiscUtils.wrap360(this.yHeadRotO);

                if( this.xRot < 0.0F ) {
                    this.xRot += 5.0F;
                    if( this.xRot > 0.0F ) {
                        this.xRot = 0.0F;
                    }
                } else if( this.xRot > 0.0F ) {
                    this.xRot -= 5.0F;
                    if( this.xRot < 0.0F ) {
                        this.xRot = 0.0F;
                    }
                }
            }
        } else {
            this.yHeadRot = MiscUtils.wrap360(this.yHeadRot);
            this.yHeadRotO = MiscUtils.wrap360(this.yHeadRotO);
            int closestRot = (MathHelper.ceil(this.yHeadRot) / 90) * 90;
            if( this.yHeadRot > closestRot ) {
                this.yHeadRot -= 5.0F;
                if( this.yHeadRot < closestRot ) {
                    this.yHeadRot = closestRot;
                }
            } else if( this.yHeadRot < closestRot ) {
                this.yHeadRot += 5.0F;
                if( this.yHeadRot > closestRot ) {
                    this.yHeadRot = closestRot;
                }
            }

            final float lockedPitch = this.delegate.getDeactiveHeadPitch();
            if( this.xRot < lockedPitch ) {
                this.xRot += 1.0F;
                if( this.xRot > lockedPitch ) {
                    this.xRot = lockedPitch;
                }
            } else if( this.xRot > lockedPitch ) {
                this.xRot -= 1.0F;
                if( this.xRot < lockedPitch ) {
                    this.xRot = lockedPitch;
                }
            }
        }

        profiler.pop();

        if( this.level.isClientSide && this.tickCount % 20 == 0 ) {
            for( int i = 0, max = this.upgProc.getContainerSize(); i < max; i++ ) {
                if( ItemStackUtils.isValid(this.upgProc.getItem(i)) ) {
                    System.out.println(this.upgProc.getItem(i));
                }
            }
            System.out.println("----");
        }
    }

    @Nonnull
    @Override
    protected ActionResultType mobInteract(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if( this.level.isClientSide ) {
            //TODO: reimplement GUIs
//            if( ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
//                if( !player.isCrouching() ) {
//                    NetworkHooks.openGui((ServerPlayerEntity) player, new TcuContainerFactory.Provider(stack, this));
//                }
//                return ActionResultType.SUCCESS;
//            } //else
            // if( !ItemStackUtils.isValid(stack) && hand == EnumHand.MAIN_HAND ) {
                //TmrUtils.INSTANCE.openGui(player, EnumGui.TINFO, this.getEntityId(), 0, 0);
//            }

            if( ItemStackUtils.isItem(stack, ItemRegistry.AMMO_CARTRIDGE) ) {
                return ActionResultType.SUCCESS;
            }

            return ActionResultType.PASS;
        } else if( ItemStackUtils.isValid(stack) ) {
            if( ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) && player.isCrouching() ) {
                TurretControlUnit.bindTurret(stack, this);
                return ActionResultType.CONSUME;
            } else if( this.targetProc.addAmmo(stack, player) ) {
                this.onPickupSucceed(stack, player);
                return ActionResultType.CONSUME;
            } else if( this.upgProc.tryApplyUpgrade(stack.copy()) ) {
                stack.shrink(1);
                this.onPickupSucceed(stack, player);
                return ActionResultType.SUCCESS;
            } else if( this.applyRepairKit(stack) ) {
                stack.shrink(1);
                this.onPickupSucceed(stack, player);
                return ActionResultType.CONSUME;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void die(@Nonnull DamageSource dmgSrc) {
        super.die(dmgSrc);

        if( !this.level.isClientSide ) {
            this.targetProc.dropAmmo();
            this.upgProc.dropUpgrades();
        }

        //just insta-kill it
        this.remove();
    }

    @Override
    public void kill() {
        this.hurt(DamageSource.MAGIC, Float.MAX_VALUE);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void syncState() {
        TurretModRebirth.NETWORK.sendToAllNear(new SyncTurretStatePacket(this),
                                               new PacketDistributor.TargetPoint(this.getX(), this.getY(), this.getZ(), 64.0D, this.level.dimension()));
    }

    //TODO: reimplement repair kits
    @Override
    public boolean applyRepairKit(ItemStack stack) {
//        IRepairKit repKit = RepairKitRegistry.INSTANCE.getObject(stack);
//
//        if( repKit.isApplicable(this) ) {
//            this.heal(repKit.getHealAmount());
//            repKit.onHeal(this);
//
//            return true;
//        }

        return false;
    }

    @Override
    public TurretCrateEntity dismantle() {
        BlockPos cratePos = this.blockPosition();
        if( this.level.setBlock(cratePos, BlockRegistry.TURRET_CRATE.defaultBlockState(), 3) ) {
            TileEntity te = this.level.getBlockEntity(cratePos);

            if( te instanceof TurretCrateEntity ) {
                TurretCrateEntity crate = (TurretCrateEntity) te;
                crate.insertTurret(this);

                this.kill();

                return crate;
            }
        }

        return null;
    }

    private void onPickupSucceed(@Nonnull ItemStack heldItem, PlayerEntity player) {
        if( heldItem.getCount() == 0 ) {
            player.inventory.removeItemNoUpdate(player.inventory.selected);
        } else {
            player.inventory.setItem(player.inventory.selected, heldItem.copy());
        }

        this.syncState();
        player.containerMenu.broadcastChanges();

        this.playPickupSound();
    }
//endregion

//region Data IO
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SHOT_CHANGED, false);
        this.entityData.define(DATA_VARIANT, "");
        this.entityData.define(DATA_IS_ACTIVE, false);

        this.setActive(true);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.addAdditionalSaveData(compound);

        compound.putString(NBT_TURRET_ID, this.delegate.getId().toString());

        this.targetProc.save(compound);
        this.upgProc.save(compound);
        compound.putBoolean(NBT_IS_ACTIVE, this.isActive());

        compound.put(NBT_OWNER, MiscUtils.apply(new CompoundNBT(), c -> {
            c.putUUID(NBT_OWNER_ID, TurretEntity.this.ownerId);
            c.putString(NBT_OWNER_NAME, ITextComponent.Serializer.toJson(TurretEntity.this.ownerName));
            return c;
        }));

        if( this.delegate instanceof IVariantHolder ) {
            compound.putString(NBT_TURRET_VARIANT, this.getVariant().getId().toString());
        }

        if( this.hasCustomName() ) {
            compound.putString(NBT_TURRET_NAME, ITextComponent.Serializer.toJson(this.getCustomName()));
        }

        this.delegate.onSave(this, compound);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);

        this.loadDelegate(new ResourceLocation(compound.getString(NBT_TURRET_ID)));

        this.targetProc.load(compound);
        this.upgProc.load(compound);
        this.setActive(compound.getBoolean(NBT_IS_ACTIVE));

        CompoundNBT ownerCompound = compound.getCompound(NBT_OWNER);
        this.ownerId = ownerCompound.getUUID(NBT_OWNER_ID);
        this.ownerName = MiscUtils.get(ITextComponent.Serializer.fromJson(ownerCompound.getString(NBT_OWNER_NAME)), StringTextComponent.EMPTY);

        if( compound.contains(NBT_TURRET_VARIANT, Constants.NBT.TAG_STRING) ) {
            this.setVariant(compound.getString(NBT_TURRET_VARIANT));
        }

        if( compound.contains(NBT_TURRET_NAME, Constants.NBT.TAG_STRING) ) {
            ITextComponent cn = ITextComponent.Serializer.fromJson(compound.getString(NBT_TURRET_NAME));
            if( cn != null ) {
                this.setCustomName(cn);
            }
        }

        this.delegate.onLoad(this, compound);
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.delegate.getId());

        CompoundNBT targetProcNbt = new CompoundNBT();
        this.targetProc.save(targetProcNbt);
        buffer.writeNbt(targetProcNbt);

        CompoundNBT upgNbt = new CompoundNBT();
        this.upgProc.save(upgNbt);
        buffer.writeNbt(upgNbt);

        buffer.writeUUID(this.ownerId);
        buffer.writeComponent(this.ownerName);

        this.delegate.writeSpawnData(this, buffer);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        this.loadDelegate(buffer.readResourceLocation());

        this.targetProc.load(buffer.readNbt());
        this.upgProc.load(buffer.readNbt());

        this.ownerId = buffer.readUUID();
        this.ownerName = buffer.readComponent();

        this.delegate.readSpawnData(this, buffer);
    }

    private void loadDelegate(ResourceLocation id) {
        this.loadDelegate(TurretRegistry.INSTANCE.get(id));
    }

    private void loadDelegate(ITurret delegate) {
        this.delegate = delegate;
        this.delegate.entityInit(this);
        this.delegate.applyEntityAttributes(this);

        Objects.requireNonNull(this.getAttribute(TurretAttributes.MAX_RELOAD_TICKS)).setBaseValue(this.delegate.getReloadTicks());
        Objects.requireNonNull(this.getAttribute(TurretAttributes.MAX_AMMO_CAPACITY)).setBaseValue(this.delegate.getAmmoCapacity());
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(this.delegate.getHealth());

        this.refreshDimensions();

        this.targetProc.init();
    }
//endregion
}
