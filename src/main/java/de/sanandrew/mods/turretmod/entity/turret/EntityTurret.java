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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.repairkit.TurretRepairKit;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityTurret
        extends EntityLiving
        implements IEntityAdditionalSpawnData, ITurretInst
{
    private static final AxisAlignedBB UPWARDS_BLOCK = new AxisAlignedBB(0.1D, 0.99D, 0.1D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB DOWNWARDS_BLOCK = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 1.0D, 0.01D, 1.0D);

    public static final DataParameter<Boolean> SHOT_CHNG = EntityDataManager.createKey(EntityTurret.class, DataSerializers.BOOLEAN);

    public boolean isUpsideDown;
    public boolean showRange;
    public boolean inGui;

    protected final TargetProcessor targetProc;
    protected final UpgradeProcessor upgProc;

    protected UUID ownerUUID;
    protected String ownerName;

    private DataWatcherBooleans<EntityTurret> dwBools;
    private BlockPos blockPos;
    private boolean prevShotChng;
    private boolean checkBlock;

    private ITurretRAM turretRAM;

    @Nonnull
    private ITurret delegate;

    public EntityTurret(World world) {
        super(world);
        this.targetProc = new TargetProcessor(this);
        this.upgProc = new UpgradeProcessor(this);
        this.rotationYaw = 0.0F;
        this.checkBlock = true;
        this.delegate = TurretRegistry.NULL_TURRET;
    }

    public EntityTurret(World world, ITurret delegate) {
        this(world);
        this.delegate = delegate;

        this.delegate.entityInit(this);
        this.delegate.applyEntityAttributes(this);
        this.setHealth(this.getMaxHealth());
    }

    public EntityTurret(World world, boolean isUpsideDown, EntityPlayer owner, ITurret delegate) {
        this(world, delegate);
        this.isUpsideDown = isUpsideDown;

        this.ownerUUID = owner.getUniqueID();
        this.ownerName = owner.getName();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getAttributeMap().registerAttribute(TurretAttributes.MAX_AMMO_CAPACITY);
        this.getAttributeMap().registerAttribute(TurretAttributes.MAX_RELOAD_TICKS);

    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dwBools = new DataWatcherBooleans<>(this);
        this.dwBools.registerDwValue();

        this.dataManager.register(SHOT_CHNG, false);

        this.setActive(true);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource dmgSrc) {
        return MiscUtils.defIfNull(this.delegate.getHurtSound(this), Sounds.hit_turrethit);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MiscUtils.defIfNull(this.delegate.getDeathSound(this), Sounds.hit_turretdeath);
    }

    protected SoundEvent getCollectSound() {
        return MiscUtils.defIfNull(this.delegate.getCollectSound(this), Sounds.collect_ia_get);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void faceEntity(Entity entity, float yawSpeed, float pitchSpeed) {
        double deltaX = entity.posX - this.posX;
        double deltaZ = entity.posZ - this.posZ;
        double deltaY;

        if( entity instanceof EntityLivingBase ) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            deltaY = (livingBase.posY + livingBase.getEyeHeight()) - (this.posY + this.getEyeHeight());
        } else {
            deltaY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D - (this.posY + this.getEyeHeight());
        }
        deltaY *= this.isUpsideDown ? -1.0D : 1.0D;

        double distVecXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yawRotation = (float) ((this.isUpsideDown ? -1.0D : 1.0D) * (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI)) - 90.0F;
        float pitchRotation = (float) -(Math.atan2(deltaY, distVecXZ) * 180.0D / Math.PI);
        this.rotationPitch = this.updateRotation(this.rotationPitch, pitchRotation);
        this.rotationYawHead = this.updateRotation(this.rotationYawHead, yawRotation);
    }

    /**
     * LEAVE EMPTY! Or else this causes visual glitches...
     */
    @Override
    public void setRotationYawHead(float rotation) { }

    protected float updateRotation(float prevRotation, float newRotation) {
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
    public void onUpdate() {
        if( !this.inGui ) {
            super.onUpdate();

            this.rotationYaw = 0.0F;
            this.renderYawOffset = 0.0F;

            this.delegate.onUpdate(this);
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

    @Override
    public void onLivingUpdate() {
        if( this.blockPos == null ) {
            this.blockPos = this.getPosition().up(this.isUpsideDown ? 2 : -1);
        }

        if( !this.isUpsideDown ) {
            this.motionY -= 0.0325F;
            super.move(MoverType.SELF, 0.0F, this.motionY, 0.0F);
            this.blockPos = this.getPosition().down(1);
        } else if( this.checkBlock && !canTurretBePlaced(this.world, this.blockPos, true, this.isUpsideDown) ) {
            this.onKillCommand();
        }

        this.world.profiler.startSection("ai");

        if( this.isMovementBlocked() ) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if( !this.world.isRemote ) {
            this.world.profiler.startSection("oldAi");
            this.updateMyEntityActionState();
            this.world.profiler.endSection();
        }

        Function<Float, Float> wrap360 = rot -> (rot < 0.0F ? 360.0F - Math.abs(rot) : rot) % 360.0F;
        if( this.isActive() ) {
            if( !this.world.isRemote ) {
                this.targetProc.onTick();
            }

            this.upgProc.onTick();

            if( this.targetProc.hasTarget() ) {
                this.faceEntity(this.targetProc.getTarget(), 10.0F, this.getVerticalFaceSpeed());
            } else if( this.world.isRemote && TmrUtils.INSTANCE.getPassengersOfClass(this, EntityPlayer.class).size() < 1 ) {
                this.rotationYawHead += 1.0F;
                this.rotationYawHead = wrap360.apply(this.rotationYawHead);
                this.prevRotationYawHead = wrap360.apply(this.prevRotationYawHead);

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
            this.rotationYawHead = wrap360.apply(this.rotationYawHead);
            this.prevRotationYawHead = wrap360.apply(this.prevRotationYawHead);
            float closestRot = (MathHelper.ceil(this.rotationYawHead) / 90) * 90.0F;
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

            final float lockedPitch = 30.0F;
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

        this.world.profiler.endSection();
    }

    private void onInteractSucceed(@Nonnull ItemStack heldItem, EntityPlayer player) {
        if( heldItem.getCount() == 0 ) {
            player.inventory.removeStackFromSlot(player.inventory.currentItem);
        } else {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem.copy());
        }

        TmrUtils.INSTANCE.updateTurretState(this);
        player.inventoryContainer.detectAndSendChanges();
        this.world.playSound(null, this.posX, this.posY, this.posZ, this.getCollectSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if( this.world.isRemote ) {
            if( ItemStackUtils.isItem(stack, ItemRegistry.turret_control_unit) ) {
                TmrUtils.INSTANCE.openGui(player, player.isSneaking() ? EnumGui.GUI_DEBUG_CAMERA : EnumGui.GUI_TCU_INFO, this.getEntityId(), this.hasPlayerPermission(player) ? 1 : 0, 0);
                return true;
            }

            return false;
        } else if( ItemStackUtils.isValid(stack) && hand == EnumHand.MAIN_HAND ) {
            TurretRepairKit repKit;

            if( this.targetProc.addAmmo(stack) ) {
                this.onInteractSucceed(stack, player);
                return true;
            } else if( (repKit = RepairKitRegistry.INSTANCE.getType(stack)).isApplicable(this) ) {
                    this.heal(repKit.getHealAmount());
                    repKit.onHeal(this);
                    stack.shrink(1);
                    this.onInteractSucceed(stack, player);

                    return true;
            } else if( this.upgProc.tryApplyUpgrade(stack.copy()) ) {
                stack.shrink(1);
                this.onInteractSucceed(stack, player);
                return true;
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public void onDeath(DamageSource dmgSrc) {
        super.onDeath(dmgSrc);

        if( !this.world.isRemote ) {
            this.targetProc.dropAmmo();
            this.upgProc.dropUpgrades();
        }

        //just insta-kill it
        this.setDead();
    }

    protected void updateMyEntityActionState() {
        this.idleTime++;
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        this.rotationYaw = 0.0F;
    }

    public ITargetProcessor getTargetProcessor() {
        return this.targetProc;
    }

    public IUpgradeProcessor getUpgradeProcessor() {
        return this.upgProc;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        UUID turretId = this.delegate.getId();
        buffer.writeLong(turretId.getMostSignificantBits());
        buffer.writeLong(turretId.getLeastSignificantBits());

        NBTTagCompound targetNbt = new NBTTagCompound();
        this.targetProc.writeToNbt(targetNbt);
        ByteBufUtils.writeTag(buffer, targetNbt);

        NBTTagCompound upgNbt = new NBTTagCompound();
        this.upgProc.writeToNbt(upgNbt);
        ByteBufUtils.writeTag(buffer, upgNbt);

        buffer.writeBoolean(this.isUpsideDown);

        if( this.ownerUUID != null ) {
            buffer.writeBoolean(true);
            buffer.writeLong(this.ownerUUID.getMostSignificantBits());
            buffer.writeLong(this.ownerUUID.getLeastSignificantBits());
            ByteBufUtils.writeUTF8String(buffer, this.ownerName);
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.delegate = TurretRegistry.INSTANCE.getTurret(new UUID(buffer.readLong(), buffer.readLong()));

        this.targetProc.readFromNbt(ByteBufUtils.readTag(buffer));
        this.upgProc.readFromNbt(ByteBufUtils.readTag(buffer));

        this.isUpsideDown = buffer.readBoolean();

        if( buffer.readBoolean() ) {
            this.ownerUUID = new UUID(buffer.readLong(), buffer.readLong());
            this.ownerName = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        nbt.setString("turretId", this.delegate.getId().toString());

        this.targetProc.writeToNbt(nbt);
        this.upgProc.writeToNbt(nbt);
        this.dwBools.writeToNbt(nbt);

        nbt.setBoolean("isUpsideDown", this.isUpsideDown);
        if( this.ownerUUID != null ) {
            nbt.setString("ownerUUID", this.ownerUUID.toString());
            nbt.setString("ownerName", this.ownerName);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        String turretId = nbt.getString("turretId");
        if( UuidUtils.isStringUuid(turretId) ) {
            this.delegate = TurretRegistry.INSTANCE.getTurret(UUID.fromString(turretId));
            this.delegate.entityInit(this);
            this.delegate.applyEntityAttributes(this);
        }

        this.targetProc.readFromNbt(nbt);
        this.upgProc.readFromNbt(nbt);
        this.dwBools.readFromNbt(nbt);

        this.isUpsideDown = nbt.getBoolean("isUpsideDown");
        if( nbt.hasKey("ownerUUID") ) {
            this.ownerUUID = UUID.fromString(nbt.getString("ownerUUID"));
            this.ownerName = nbt.getString("ownerName");
        }
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 50;
    }

    /**turrets are machines, they aren't affected by potions*/
    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect.getIsAmbient();
    }

    /**turrets are immobile, leave empty*/
    @Override
    public final void knockBack(Entity entity, float unknown, double motionXAmount, double motionZAmount) {}

    @Override
    public final void move(MoverType type, double motionX, double motionY, double motionZ) {
        if( type == MoverType.PISTON ) {
            super.move(type, motionX, motionY, motionZ);
        }
    }

    /**turrets are immobile, leave empty*/
    @Override
    public final void travel(float strafe, float vertical, float forward) {}

    public ResourceLocation getStandardTexture() {
        return this.delegate.getStandardTexture(this);
    }

    public ResourceLocation getGlowTexture() {
        return this.delegate.getGlowTexture(this);
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

    private static boolean isAABBInside(AxisAlignedBB bb1, AxisAlignedBB bb2) {
        return bb1.minX <= bb2.minX && bb1.minY <= bb2.minY && bb1.minZ <= bb2.minZ && bb1.maxX >= bb2.maxX && bb1.maxY >= bb2.maxY && bb1.maxZ >= bb2.maxZ;
    }

    public static boolean canTurretBePlaced(World world, BlockPos pos, boolean doBlockCheckOnly, boolean updideDown) {
        AxisAlignedBB blockBB = world.getBlockState(pos).getCollisionBoundingBox(world, pos);
        if( blockBB == null || !isAABBInside(blockBB, updideDown ? DOWNWARDS_BLOCK : UPWARDS_BLOCK) ) {
            return false;
        }

        BlockPos posPlaced = pos.offset(updideDown ? EnumFacing.DOWN : EnumFacing.UP);
        BlockPos posPlaced2 = pos.offset(updideDown ? EnumFacing.DOWN : EnumFacing.UP, 2);
        if( !world.getBlockState(posPlaced).getBlock().isReplaceable(world, posPlaced) || !world.getBlockState(posPlaced2).getBlock().isReplaceable(world, posPlaced2) ) {
            return false;
        }

        if( !doBlockCheckOnly ) {
            AxisAlignedBB aabb = new AxisAlignedBB(posPlaced.getX(), posPlaced.getY(), posPlaced.getZ(), posPlaced.getX() + 1.0D, posPlaced.getY() + (updideDown ? - 1.0D : 1.0D), posPlaced.getZ() + 1.0D);
            if( !world.getEntitiesWithinAABB(EntityTurret.class, aabb).isEmpty() ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getOwnerName() {
        return this.ownerName;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean hasPlayerPermission(EntityPlayer player) {
        if( player == null ) {
            return false;
        }

        if( this.ownerUUID == null ) {
            return true;
        }

        MinecraftServer mcSrv = this.world.getMinecraftServer();
        GameProfile profile = player.getGameProfile();

        if( mcSrv != null && mcSrv.isSinglePlayer() && mcSrv.getServerOwner().equals(profile.getName()) ) {
            return true;
        }

        if( TmrUtils.INSTANCE.canPlayerEditAll() || this.ownerUUID.equals(profile.getId()) ) {
            return true;
        }

        return player.canUseCommand(2, "") && TmrUtils.INSTANCE.canOpEditAll();
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
    @Nonnull
    public ItemStack getPickedResult(RayTraceResult target) {
        return ItemRegistry.turret_placer.getTurretItem(1, this.delegate);
    }

    @Override
    public void onKillCommand() {
        this.attackEntityFrom(DamageSource.MAGIC, Float.MAX_VALUE);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity.getEntityBoundingBox();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }

    public AxisAlignedBB getRangeBB() {
        return this.delegate.getRangeBB(this);
    }

    @Override
    public boolean isUpsideDown() {
        return this.isUpsideDown;
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }

    @Override
    public ITurret getTurret() {
        return this.delegate;
    }

}