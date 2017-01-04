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
import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.InventoryUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketPlayerTurretAction;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.registry.medpack.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.medpack.TurretRepairKit;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.DataWatcherBooleans;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TmrConfiguration;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class EntityTurret
        extends EntityLiving
        implements IEntityAdditionalSpawnData
{
    private static final AxisAlignedBB UPWARDS_BLOCK = new AxisAlignedBB(0.1D, 0.99D, 0.1D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB DOWNWARDS_BLOCK = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 1.0D, 0.01D, 1.0D);

    public static final DataParameter<Boolean> SHOT_CHNG = EntityDataManager.createKey(EntityTurret.class, DataSerializers.BOOLEAN);

    public boolean isUpsideDown;
    public boolean showRange;

    private BlockPos blockPos;
    private boolean prevShotChng;
    private boolean checkBlock;

    protected TargetProcessor targetProc;
    protected UpgradeProcessor upgProc;

    protected UUID ownerUUID;
    protected String ownerName;

    public boolean inGui;

    private DataWatcherBooleans<EntityTurret> dwBools;
    private boolean isPushedByPiston;

    public EntityTurret(World world) {
        super(world);
        this.upgProc = new UpgradeProcessor(this);
        this.rotationYaw = 0.0F;
        this.checkBlock = true;
    }

    public EntityTurret(World world, boolean isUpsideDown, EntityPlayer owner) {
        this(world);
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
    protected SoundEvent getHurtSound() {
        return Sounds.TURRET_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return Sounds.TURRET_DEATH;
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

        double distVecXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
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
        }
    }

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
            this.blockPos = new BlockPos((int) Math.floor(this.posX), (int)Math.floor(this.posY) + (this.isUpsideDown ? 2 : -1), (int)Math.floor(this.posZ));
        }

        if( !this.isUpsideDown ) {
            this.motionY -= 0.0325F;
            super.moveEntity(0.0F, this.motionY, 0.0F);
        } else if( this.checkBlock && !canTurretBePlaced(this.world, this.blockPos, true, this.isUpsideDown) ) {
            this.kill();
        }

        this.isPushedByPiston = false;

        this.world.theProfiler.startSection("ai");

        if( this.isMovementBlocked() ) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if( !this.world.isRemote ) {
            this.world.theProfiler.startSection("oldAi");
            this.updateMyEntityActionState();
            this.world.theProfiler.endSection();
        }

        if( this.isActive() ) {
            if( !this.world.isRemote ) {
                this.targetProc.onTick();
            }

            this.upgProc.onTick();

            if( this.targetProc.hasTarget() ) {
                this.faceEntity(this.targetProc.getTarget(), 10.0F, this.getVerticalFaceSpeed());
            } else if( this.world.isRemote && EntityUtils.getPassengersOfClass(this, EntityPlayer.class).size() < 1 ) {
                this.rotationYawHead += 1.0F;
                if( this.rotationYawHead >= 360.0D ) {
                    this.rotationYawHead -= 360.0D;
                    this.prevRotationYawHead -= 360.0D;
                }

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
            if( this.rotationYawHead > 0.0F ) {
                this.rotationYawHead -= 5.0F;
                if( this.rotationYawHead < 0.0F ) {
                    this.rotationYawHead = 0.0F;
                }
            } else if( this.rotationYawHead < 0.0F ) {
                this.rotationYawHead += 5.0F;
                if( this.rotationYawHead > 0.0F ) {
                    this.rotationYawHead = 0.0F;
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

        this.world.theProfiler.endSection();
    }

    private void onInteractSucceed(ItemStack heldItem, EntityPlayer player) {
        if( heldItem.stackSize == 0 ) {
            player.inventory.removeStackFromSlot(player.inventory.currentItem);
        } else {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem.copy());
        }

        this.updateState();
        player.inventoryContainer.detectAndSendChanges();
        this.world.playSound(null, this.posX, this.posY, this.posZ, Sounds.TURRET_COLLECT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
        if( this.world.isRemote ) {
            if( ItemStackUtils.isValid(stack) && stack.getItem() == ItemRegistry.turret_control_unit ) {
                TurretModRebirth.proxy.openGui(player, player.isSneaking() ? EnumGui.GUI_DEBUG_CAMERA : EnumGui.GUI_TCU_INFO, this.getEntityId(), this.hasPlayerPermission(player) ? 1 : 0, 0);
                return true;
            }

            return false;
        } else if( ItemStackUtils.isValid(stack) && hand == EnumHand.MAIN_HAND ) {
            if( this.targetProc.addAmmo(stack) ) {
                this.onInteractSucceed(stack, player);
                return true;
            } else if( stack.getItem() == ItemRegistry.repair_kit ) {
                TurretRepairKit repKit = RepairKitRegistry.INSTANCE.getType(stack);
                if( repKit != null && repKit.isApplicable(this) ) {
                    this.heal(repKit.getHealAmount());
                    repKit.onHeal(this);
                    stack.stackSize--;
                    this.onInteractSucceed(stack, player);

                    return true;
                }
            } else if( stack.getItem() == ItemRegistry.turret_upgrade && this.upgProc.tryApplyUpgrade(stack.copy()) ) {
                stack.stackSize--;
                this.onInteractSucceed(stack, player);
                return true;
            }
        }

        return super.processInteract(player, hand, stack);
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
        ++this.entityAge;
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        this.rotationYaw = 0.0F;
    }

    public TargetProcessor getTargetProcessor() {
        return this.targetProc;
    }

    public UpgradeProcessor getUpgradeProcessor() {
        return this.upgProc;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        NBTTagCompound targetNbt = new NBTTagCompound();
        this.targetProc.writeToNbt(targetNbt);
        ByteBufUtils.writeTag(buffer, targetNbt);

        NBTTagCompound upgNbt = new NBTTagCompound();
        this.upgProc.writeToNbt(upgNbt);
        ByteBufUtils.writeTag(buffer, upgNbt);

        buffer.writeBoolean(this.isUpsideDown);

        if( this.ownerUUID != null ) {
            ByteBufUtils.writeUTF8String(buffer, this.ownerUUID.toString());
            ByteBufUtils.writeUTF8String(buffer, this.ownerName);
        } else {
            ByteBufUtils.writeUTF8String(buffer, "[UNKNOWN_OWNER]");
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.targetProc.readFromNbt(ByteBufUtils.readTag(buffer));
        this.upgProc.readFromNbt(ByteBufUtils.readTag(buffer));
        this.isUpsideDown = buffer.readBoolean();

        String ownerUUIDStr = ByteBufUtils.readUTF8String(buffer);
        if( !ownerUUIDStr.equals("[UNKNOWN_OWNER]") ) {
            this.ownerUUID = UUID.fromString(ownerUUIDStr);
            this.ownerName = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

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
    public final void moveEntity(double motionX, double motionY, double motionZ) {
        if( this.isPushedByPiston ) {
            super.moveEntity(motionX, motionY, motionZ);
        }
    }

    /**turrets are immobile, leave empty*/
    @Override
    public final void moveEntityWithHeading(float strafe, float forward) {}

    public abstract ResourceLocation getStandardTexture();

    public abstract ResourceLocation getGlowTexture();

    public void updateState() {
        PacketRegistry.sendToAllAround(new PacketUpdateTurretState(this), this.dimension, this.posX, this.posY, this.posZ, 64.0D);
    }

    public boolean isActive() {
        return this.dwBools.getBit(DataWatcherBooleans.Turret.ACTIVE.bit);
    }

    public void setActive(boolean active) {
        this.dwBools.setBit(DataWatcherBooleans.Turret.ACTIVE.bit, active);
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

        if( TmrConfiguration.playerCanEditAll || this.ownerUUID.equals(profile.getId()) ) {
            return true;
        }

        return player.canCommandSenderUseCommand(2, "") && TmrConfiguration.opCanEditAll;
    }

    public boolean tryDismantle(EntityPlayer player) {
        Tuple chestItm = InventoryUtils.getSimilarStackFromInventory(new ItemStack(Blocks.CHEST), player.inventory, true);
        if( chestItm != null && ItemStackUtils.isValid(chestItm.getValue(1)) ) {
            ItemStack chestStack = chestItm.getValue(1);
            if( this.world.isRemote ) {
                PacketRegistry.sendToServer(new PacketPlayerTurretAction(this, PacketPlayerTurretAction.DISMANTLE));
                return true;
            } else {
                this.checkBlock = false;
                this.posY += 2048.0F;
                this.setPosition(this.posX, this.posY, this.posZ);
                int y = this.isUpsideDown ? 2 : -1;
                if( chestStack.getItem().onItemUse(chestStack, player, this.world, this.blockPos.offset(EnumFacing.DOWN, y), EnumHand.MAIN_HAND,
                                                   this.isUpsideDown ? EnumFacing.DOWN : EnumFacing.UP, 0.5F, 1.0F, 0.5F) == EnumActionResult.SUCCESS )
                {
                    TileEntity te = this.world.getTileEntity(this.blockPos.offset(EnumFacing.DOWN, y));

                    if( te instanceof TileEntityChest ) {
                        this.posY -= 2048.0F;
                        this.setPosition(this.posX, this.posY, this.posZ);

                        TileEntityChest chest = (TileEntityChest) te;
                        chest.setInventorySlotContents(0, ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(this.getClass()), this));
                        this.targetProc.putAmmoInInventory(chest);

                        if( chestStack.stackSize < 1 ) {
                            player.inventory.setInventorySlotContents(chestItm.getValue(0), null);
                        }
                        player.inventoryContainer.detectAndSendChanges();
                        //TODO: make custom container for turrets and put upgrades in it
                        this.upgProc.dropUpgrades();
                        this.kill();
                        return true;
                    }
                }
                this.checkBlock = true;
                this.posY -= 2048.0F;
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }

        return false;
    }

    @Override
    public EnumPushReaction getPushReaction() {
        this.isPushedByPiston = true;

        return EnumPushReaction.NORMAL;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(this.getClass()));
    }

    @Override
    protected void kill() {
        this.attackEntityFrom(DamageSource.magic, Float.MAX_VALUE);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity.getEntityBoundingBox();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }
}