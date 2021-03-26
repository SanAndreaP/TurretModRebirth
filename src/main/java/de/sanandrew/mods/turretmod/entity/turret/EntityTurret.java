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
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.ItemRemapper;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.network.UpdateTurretStatePacket;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretCrate;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class EntityTurret
        extends MobEntity
        implements IEntityAdditionalSpawnData, ITurretInst
{
    private static final AxisAlignedBB UPWARDS_BLOCK = new AxisAlignedBB(0.1D, 0.99D, 0.1D, 1.0D, 1.0D, 1.0D);

    private static final DataParameter<Boolean> SHOT_CHNG = EntityDataManager.createKey(EntityTurret.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> VARIANT = EntityDataManager.createKey(EntityTurret.class, DataSerializers.STRING);

    private boolean showRange;
    public boolean inGui;

    private final TargetProcessor targetProc;
    //TODO: reimplement upgrades
//    private final UpgradeProcessor upgProc;

    @Nonnull
    private UUID           ownerId;
    @Nonnull
    private ITextComponent ownerName;

    private DataWatcherBooleans<EntityTurret> dwBools;
    private boolean prevShotChng;

    private ITurretRAM turretRAM;

    @Nonnull
    private ITurret delegate;

    /** called when turret is loaded from disk **/
    public EntityTurret(EntityType<EntityTurret> type, World world) {
        super(type, world);
        this.ownerId = UuidUtils.EMPTY_UUID;
        this.ownerName = StringTextComponent.EMPTY;
        this.targetProc = new TargetProcessor(this);
        //TODO: reimplement upgrades
//        this.upgProc = new UpgradeProcessor(this);
        this.rotationYaw = 0.0F;
        this.delegate = TurretRegistry.INSTANCE.getDefaultObject();
    }

    //TODO: figure out how to apply the delegate on spawn
    //** called when turret is rendered in a GUI or its placed down by {@link EntityTurret#EntityTurret(World, EntityPlayer, ITurret)} **/
//    public EntityTurret(World world, ITurret delegate) {
//        this(world);
//
//        this.loadDelegate(delegate);
//
//        this.setHealth(this.getMaxHealth());
//    }

    //TODO: figure out how to apply the delegate and owner on spawn
    //** called when turret is placed down **/
//    public EntityTurret(World world, EntityPlayer owner, ITurret delegate) {
//        this(world, delegate);
//
//        this.ownerUUID = owner.getUniqueID();
//        this.ownerName = owner.getName();
//    }

    @Override
    protected void registerData() {
        super.registerData();

        this.dwBools = new DataWatcherBooleans<>(this);
        this.dwBools.registerDwValue();

        this.dataManager.register(SHOT_CHNG, false);
        this.dataManager.register(VARIANT, "");

        this.setActive(true);
    }

    //TODO: reimplement sounds
//    @Override
//    protected SoundEvent getHurtSound(DamageSource dmgSrc) {
//        return MiscUtils.defIfNull(this.delegate.getHurtSound(this), Sounds.HIT_TURRETHIT);
//    }
//
//    @Override
//    protected SoundEvent getDeathSound() {
//        return MiscUtils.defIfNull(this.delegate.getDeathSound(this), Sounds.HIT_TURRETDEATH);
//    }
//
//    private SoundEvent getCollectSound() {
//        return MiscUtils.defIfNull(this.delegate.getCollectSound(this), Sounds.COLLECT_IA_GET);
//    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void faceEntity(Entity entity, float yawSpeed, float pitchSpeed) {
        double deltaX = entity.getPosX() - this.getPosX();
        double deltaZ = entity.getPosZ() - this.getPosZ();
        double deltaY;

        if( entity instanceof LivingEntity ) {
            LivingEntity livingBase = (LivingEntity) entity;
            deltaY = (livingBase.getPosY() + livingBase.getEyeHeight()) - (this.getPosY() + this.getEyeHeight());
        } else {
            deltaY = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - (this.getPosY() + this.getEyeHeight());
        }

        double distVecXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yawRotation = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
        float pitchRotation = (float) -(Math.atan2(deltaY, distVecXZ) * 180.0D / Math.PI);
        this.rotationPitch = calcRotation(this.rotationPitch, pitchRotation);
        this.rotationYawHead = calcRotation(this.rotationYawHead, yawRotation);
    }

    @Override
    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return this.delegate.getEyeHeight(poseIn, sizeIn);
    }

    /**
     * LEAVE EMPTY! Or else this causes visual glitches...
     */
    @Override
    public void setRotationYawHead(float rotation) { }

    private static float calcRotation(float prevRotation, float newRotation) {
        final float speed = 20.0F;
        float part = MathHelper.wrapDegrees(newRotation - prevRotation);

        if( part > speed ) {
            part = speed;
        }

        if( part < -speed ) {
            part = -speed;
        }

        return prevRotation + part;
    }

    @Override
    public void tick() {
        if( !this.inGui ) {
            super.tick();

            this.rotationYaw = 0.0F;
            this.renderYawOffset = 0.0F;

            this.delegate.tick(this);
        }
    }

    @Override
    public boolean wasShooting() {
        boolean shot = this.dataManager.get(SHOT_CHNG) != this.prevShotChng;
        this.prevShotChng = this.dataManager.get(SHOT_CHNG);
        return shot;
    }

    public void setShooting() {
        this.dataManager.set(SHOT_CHNG, !this.dataManager.get(SHOT_CHNG));
    }

    private boolean isSubmergedInLiquid(double heightMod) {
        BlockPos pos = new BlockPos(this.getPosX(), this.getPosY() + heightMod, this.getPosZ());
        return this.world.getBlockState(pos).getMaterial().isLiquid();
    }

    @Override
    public void baseTick() {
        double motionY = this.getMotion().y;
        final double height = this.getHeight();
        final IProfiler profiler = this.world.getProfiler();

        if( !this.delegate.isBuoy() ) {
            motionY -= 0.0325F;
        } else {
            if( this.isSubmergedInLiquid(height + 0.2F) ) {
                motionY += 0.0125F;
            } else if( this.isSubmergedInLiquid(height + 0.05F) ) {
                motionY += 0.005F;
                if( motionY > 0.025F ) {
                    motionY *= 0.75F;
                }
            } else if( !this.isSubmergedInLiquid(height - 0.2F) ) {
                motionY -= 0.0325F;
            } else {
                motionY -= 0.005F;
                if( motionY < -0.025F ) {
                    motionY *= 0.75F;
                }
            }
        }
        this.move(MoverType.SELF, new Vector3d(0.0F, motionY, 0.0F));

        profiler.startSection("ai");

        if( this.isMovementBlocked() ) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
        } else if( !this.world.isRemote ) {
            profiler.startSection("turretAi");
            this.updateAI();
            profiler.endSection();
        }

        //TODO: reimplement upgrades
//        this.upgProc.onTick();

        if( !this.world.isRemote ) {
            this.targetProc.onTick();
        } else {
            this.targetProc.onTickClient();
        }

        if( this.isActive() ) {
            if( this.targetProc.hasTarget() ) {
                this.faceEntity(this.targetProc.getTarget(), 10.0F, this.getVerticalFaceSpeed());
            } else if( this.world.isRemote && this.getControllingPassenger() instanceof PlayerEntity ) {
                this.rotationYawHead += 1.0F;
                this.rotationYawHead = MiscUtils.wrap360(this.rotationYawHead);
                this.prevRotationYawHead = MiscUtils.wrap360(this.prevRotationYawHead);

                if( this.rotationPitch < 0.0F ) {
                    this.rotationPitch += 5.0F;
                    if( this.rotationPitch > 0.0F ) {
                        this.rotationPitch = 0.0F;
                    }
                } else if( this.rotationPitch > 0.0F ) {
                    this.rotationPitch -= 5.0F;
                    if( this.rotationPitch < 0.0F ) {
                        this.rotationPitch = 0.0F;
                    }
                }
            }
        } else {
            this.rotationYawHead = MiscUtils.wrap360(this.rotationYawHead);
            this.prevRotationYawHead = MiscUtils.wrap360(this.prevRotationYawHead);
            int closestRot = (MathHelper.ceil(this.rotationYawHead) / 90) * 90;
            if( this.rotationYawHead > closestRot ) {
                this.rotationYawHead -= 5.0F;
                if( this.rotationYawHead < closestRot ) {
                    this.rotationYawHead = closestRot;
                }
            } else if( this.rotationYawHead < closestRot ) {
                this.rotationYawHead += 5.0F;
                if( this.rotationYawHead > closestRot ) {
                    this.rotationYawHead = closestRot;
                }
            }

            final float lockedPitch = this.delegate.getDeactiveHeadPitch();
            if( this.rotationPitch < lockedPitch ) {
                this.rotationPitch += 1.0F;
                if( this.rotationPitch > lockedPitch ) {
                    this.rotationPitch = lockedPitch;
                }
            } else if( this.rotationPitch > lockedPitch ) {
                this.rotationPitch -= 1.0F;
                if( this.rotationPitch < lockedPitch ) {
                    this.rotationPitch = lockedPitch;
                }
            }
        }

        profiler.endSection();
    }

    private void onInteractSucceed(@Nonnull ItemStack heldItem, PlayerEntity player) {
        if( heldItem.getCount() == 0 ) {
            player.inventory.removeStackFromSlot(player.inventory.currentItem);
        } else {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem.copy());
        }

        this.updateState();
        player.container.detectAndSendChanges();
        //TODO: reimplement sounds
//        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), this.getCollectSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    @Nonnull
    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        //TODO: reimplement TCU & GUIs
        if( this.world.isRemote ) {
//            if( ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) ) {
//                if( !player.isSneaking() ) {
//                    TmrUtils.INSTANCE.openGui(player, EnumGui.TCU, this.getEntityId(), 0, 0);
//                }
//                return ActionResultType.SUCCESS;
//            } else if( !ItemStackUtils.isValid(stack) && hand == EnumHand.MAIN_HAND ) {
//                TmrUtils.INSTANCE.openGui(player, EnumGui.TINFO, this.getEntityId(), 0, 0);
//            }

            return ActionResultType.PASS;
        } else if( ItemStackUtils.isValid(stack) ) {
            /*if( ItemStackUtils.isItem(stack, ItemRegistry.TURRET_CONTROL_UNIT) && player.isSneaking() ) {
                ItemTurretControlUnit.bindTurret(stack, this);
                return true;
            } else */if( this.targetProc.addAmmo(stack, player) ) {
                this.onInteractSucceed(stack, player);
                return ActionResultType.SUCCESS;
                //TODO: reimplement upgrades
//            } else if( this.upgProc.tryApplyUpgrade(stack.copy()) ) {
//                stack.shrink(1);
//                this.onInteractSucceed(stack, player);
//                return ActionResultType.SUCCESS;
            } else if( this.applyRepairKit(stack) ) {
                stack.shrink(1);
                this.onInteractSucceed(stack, player);
                return ActionResultType.SUCCESS;
            }
        }

        return super.func_230254_b_(player, hand);
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
    public void onDeath(@Nonnull DamageSource dmgSrc) {
        super.onDeath(dmgSrc);

        if( !this.world.isRemote ) {
            this.targetProc.dropAmmo();
            //TODO: reimplement upgrades
//            this.upgProc.dropUpgrades();
        }

        //just insta-kill it
        this.setDead();
    }

    private void updateAI() {
        this.idleTime++;
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        this.rotationYaw = 0.0F;
    }

    public ITargetProcessor getTargetProcessor() {
        return this.targetProc;
    }

    //TODO: reimplement upgrades
//    public IUpgradeProcessor getUpgradeProcessor() {
//        return this.upgProc;
//    }


    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.delegate.getId());
        buffer.writeCompoundTag(this.targetProc.save(new CompoundNBT()));

        //TODO: reimplement upgrades
//        NBTTagCompound upgNbt = new NBTTagCompound();
//        this.upgProc.writeToNbt(upgNbt);
//        ByteBufUtils.writeTag(buffer, upgNbt);

        buffer.writeUniqueId(this.ownerId);
        buffer.writeTextComponent(this.ownerName);

        this.delegate.writeSpawnData(this, buffer);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        this.delegate = TurretRegistry.INSTANCE.getObject(buffer.readResourceLocation());

        this.targetProc.load(buffer.readCompoundTag());
        //TODO: reimplement upgrades
//        this.upgProc.readFromNbt(ByteBufUtils.readTag(buffer));

        this.ownerId = buffer.readUniqueId();
        this.ownerName = buffer.readTextComponent();

        this.delegate.readSpawnData(this, buffer);
    }

    public static final String NBT_TURRET_ID = "TurretId";
    public static final String NBT_TURRET_VARIANT = "Variant";
    public static final String NBT_OWNER = "Owner";
    public static final String NBT_ID = "Id";
    public static final String NBT_NAME = "Name";
    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);

        compound.putString(NBT_TURRET_ID, this.delegate.getId().toString());

        this.targetProc.save(compound);
        //TODO: reimplement upgrades
//        this.upgProc.writeToNbt(nbt);
        this.dwBools.save(compound);

        compound.put(NBT_OWNER, new CompoundNBT() {{
            this.putUniqueId(NBT_ID, EntityTurret.this.ownerId);
            this.putString(NBT_NAME, ITextComponent.Serializer.toJson(EntityTurret.this.ownerName));
        }});

        if( this.delegate instanceof IVariantHolder ) {
            compound.putString(NBT_TURRET_VARIANT, this.getVariant().getId().toString());
        }

        this.delegate.onSave(this, compound);
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);

        this.loadDelegate(new ResourceLocation(compound.getString(NBT_TURRET_ID)));

        this.targetProc.load(compound);
        //TODO: reimplement upgrades
//        this.upgProc.readFromNbt(nbt);
        this.dwBools.load(compound);

        CompoundNBT ownerCompound = compound.getCompound(NBT_OWNER);
        this.ownerId = ownerCompound.getUniqueId(NBT_ID);
        this.ownerName = MiscUtils.defIfNull(ITextComponent.Serializer.getComponentFromJson(ownerCompound.getString(NBT_NAME)), StringTextComponent.EMPTY);

        if( compound.contains(NBT_TURRET_VARIANT, Constants.NBT.TAG_STRING) ) {
            this.setVariant(new ResourceLocation(compound.getString(NBT_TURRET_VARIANT)));
        }

        this.delegate.onLoad(this, compound);
    }

    private void loadDelegate(ResourceLocation id) {
        this.loadDelegate(TurretRegistry.INSTANCE.getObject(id));
    }

    private void loadDelegate(ITurret turret) {
        this.delegate = turret;
        this.delegate.entityInit(this);
        this.delegate.applyEntityAttributes(this);

        Objects.requireNonNull(this.getAttribute(TurretAttributes.MAX_RELOAD_TICKS)).setBaseValue(this.delegate.getReloadTicks());
        Objects.requireNonNull(this.getAttribute(TurretAttributes.MAX_AMMO_CAPACITY)).setBaseValue(this.delegate.getAmmoCapacity());
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(this.delegate.getHealth());

        this.targetProc.init();
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 50;
    }

    @Override
    public boolean isPotionApplicable(EffectInstance effect) {
        return effect.isAmbient();
    }

    /**turrets are immobile, leave empty*/
    @Override
    public void applyKnockback(float strength, double ratioX, double ratioZ) { }

    /**turrets are immobile, leave empty*/
    @Override
    public void travel(Vector3d travelVector) { }

    @Override
    public void move(MoverType typeIn, Vector3d pos) {
        if( typeIn == MoverType.PISTON || typeIn == MoverType.SELF ) {
            super.move(typeIn, pos);
        }
    }

    @Override
    public boolean isGlowing() {
        return super.isGlowing() || TurretModRebirth.PROXY.checkTurretGlowing(this);
    }

    public SoundEvent getShootSound() {
        return this.delegate.getShootSound(this);
    }

    public SoundEvent getNoAmmoSound() {
        return MiscUtils.defIfNull(this.delegate.getNoAmmoSound(this), SoundEvents.BLOCK_DISPENSER_FAIL);
    }

    public boolean isActive() {
        return this.dwBools.getBit(DataWatcherBooleans.Turret.ACTIVE.bit);
    }

    public void setActive(boolean isActive) {
        this.dwBools.setBit(DataWatcherBooleans.Turret.ACTIVE.bit, isActive);
    }

    @Override
    public boolean showRange() {
        return this.showRange;
    }

    @Override
    public void setShowRange(boolean showRange) {
        this.showRange = showRange;
    }

    private static boolean isAABBInside(AxisAlignedBB bb1) {
        return bb1.minX <= EntityTurret.UPWARDS_BLOCK.minX && bb1.minY <= EntityTurret.UPWARDS_BLOCK.minY && bb1.minZ <= EntityTurret.UPWARDS_BLOCK.minZ
                       && bb1.maxX >= EntityTurret.UPWARDS_BLOCK.maxX && bb1.maxY >= EntityTurret.UPWARDS_BLOCK.maxY && bb1.maxZ >= EntityTurret.UPWARDS_BLOCK.maxZ;
    }

    public static boolean canTurretBePlaced(ITurret delegate, BlockItemUseContext context, boolean doBlockCheckOnly) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();

        VoxelShape blockBB = world.getBlockState(pos).getCollisionShape(world, pos);
        boolean    buoyant = delegate.isBuoy();
        if( !buoyant && !blockBB.isEmpty() && !isAABBInside(blockBB.getBoundingBox()) ) {
            return false;
        }

        BlockItemUseContext posPlaced = BlockItemUseContext.func_221536_a(context, pos, Direction.UP);
        BlockItemUseContext posPlaced2 = buoyant ? context : BlockItemUseContext.func_221536_a(posPlaced, posPlaced.getPos(), Direction.UP);
        if( !world.getBlockState(posPlaced.getPos()).isReplaceable(posPlaced)
            || !world.getBlockState(posPlaced2.getPos()).isReplaceable(posPlaced2) )
        {
            return false;
        }

        if( !doBlockCheckOnly ) {
            AxisAlignedBB aabb = new AxisAlignedBB(posPlaced.getPos()).offset(0, buoyant ? -2 : 0, 0);
            return world.getEntitiesWithinAABB(EntityTurret.class, aabb).isEmpty();
        }

        return true;
    }

    @Override
    public ITextComponent getOwnerName() {
        return this.ownerName;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean hasPlayerPermission(PlayerEntity player) {
        if( player == null ) {
            return false;
        }

        if( this.ownerId.equals(UuidUtils.EMPTY_UUID) ) {
            return true;
        }

        MinecraftServer mcSrv = this.world.getServer();
        GameProfile profile = player.getGameProfile();

        if( mcSrv != null && mcSrv.isSinglePlayer() && mcSrv.getServerOwner().equals(profile.getName()) ) {
            return true;
        }

        if( TmrUtils.INSTANCE.canPlayerEditAll() || this.ownerId.equals(profile.getId()) ) {
            return true;
        }

        return player.hasPermissionLevel(2) && TmrUtils.INSTANCE.canOpEditAll();
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
    public void updateState() {
        TurretModRebirth.NETWORK.sendToAllNear(new UpdateTurretStatePacket(this),
                                               new PacketDistributor.TargetPoint(this.getPosX(), this.getPosY(), this.prevPosZ, 64.0D, this.world.getDimensionKey()));
//        PacketRegistry.sendToAllAround(new PacketUpdateTurretState(this), this.dimension, this.posX, this.posY, this.posZ, 64.0D);
    }

    @Override
    @Nonnull
    public ItemStack getPickedResult(RayTraceResult target) {
        ItemStack pickedItem = TurretRegistry.INSTANCE.getItem(this.delegate.getId());
        if( this.delegate instanceof IVariantHolder ) {
            IVariant variant = this.getVariant();

            if( !((IVariantHolder) this.delegate).isDefaultVariant(variant) ) {
                new ItemTurret.TurretStats(null, null, variant).updateData(pickedItem);
            }
        }

        return pickedItem;
    }

    @Override
    public void onKillCommand() {
        this.attackEntityFrom(DamageSource.MAGIC, Float.MAX_VALUE);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity instanceof EntityPlayer ? entity.getEntityBoundingBox() : null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    public AxisAlignedBB getRangeBB() {
        return this.delegate.getRangeBB(this);
    }

    @Override
    public boolean isBuoy() {
        return this.delegate.isBuoy();
    }

    @Override
    public EntityLiving get() {
        return this;
    }

    @Override
    public ITurret getTurret() {
        return this.delegate;
    }

    @Override
    public ITurret.AttackType getAttackType() {
        return this.delegate.getAttackType();
    }

    @Override
    public String getName() {
        if( this.hasCustomName() ) {
            return this.getCustomNameTag();
        } else {
            return LangUtils.translate(LangUtils.ENTITY_NAME.get(this.delegate.getId()));
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return this.delegate.isBuoy();
    }

    @Override
    public TileEntityTurretCrate dismantle() {
        BlockPos cratePos = this.getPosition();
        if( this.world.setBlockState(cratePos, BlockRegistry.TURRET_CRATE.getDefaultState(), 3) ) {
            TileEntity te = this.world.getTileEntity(cratePos);

            if( te instanceof TileEntityTurretCrate ) {
                TileEntityTurretCrate crate = (TileEntityTurretCrate) te;
                crate.insertTurret(this);

                this.onKillCommand();

                return crate;
            }
        }

        return null;
    }

    @Override
    public IVariant getVariant() {
        if( this.delegate instanceof IVariantHolder ) {
            return ((IVariantHolder) this.delegate).getVariant(new ResourceLocation(this.dataManager.get(VARIANT)));
        }

        return null;
    }

    @Override
    public void setVariant(ResourceLocation variantId) {
        if( this.delegate instanceof IVariantHolder ) {
            this.dataManager.set(VARIANT, variantId.toString());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getPartBrightnessForRender(double partY) {
        BlockPos.MutableBlockPos bpos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));

        if( this.world.isBlockLoaded(bpos) ) {
            bpos.setY(MathHelper.floor(this.posY + partY));
            return this.world.getCombinedLight(bpos, 0);
        } else {
            return 0;
        }
    }
}
