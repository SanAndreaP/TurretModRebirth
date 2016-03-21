/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import codechicken.lib.inventory.InventoryUtils;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.registry.medpack.TurretRepairKit;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;
import scala.Int;

import java.util.List;

public abstract class EntityTurret
        extends EntityLiving
        implements IEntityAdditionalSpawnData
{
    private boolean isInitialized = false;

    public boolean isUpsideDown;
    public boolean showRange;

    // data watcher IDs
//    private static final int DW_AMMO = 20; /* INT */
//    private static final int DW_AMMO_TYPE = 21; /* ITEM_STACK */
    private static final int DW_EXPERIENCE = 22; /* INT */
    private static final int DW_OWNER_UUID = 23; /* STRING */
    private static final int DW_OWNER_NAME = 24; /* STRING */
    private static final int DW_SHOOT_TICKS = 26; /* INT */
    private static final int DW_FREQUENCY = 27; /* BYTE */
    private static final int DW_BOOLEANS = 28; /* BYTE */

    private Triplet<Integer, Integer, Integer> blockPos = null;

    protected TargetProcessor targetProc;

    public EntityTurret(World world) {
        super(world);
    }

    public EntityTurret(World world, boolean isUpsideDown) {
        this(world);
        this.isUpsideDown = isUpsideDown;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void faceEntity(Entity entity, float yawSpeed, float pitchSpeed) {
        if( entity == null || entity.boundingBox == null ) {
            return;
        }

        double deltaX = entity.posX - this.posX;
        double deltaZ = entity.posZ - this.posZ;
        double deltaY;

        if( entity instanceof EntityLivingBase ) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            deltaY = this.posY + this.getEyeHeight() - (livingBase.posY + livingBase.getEyeHeight());
        } else {
            deltaY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D - (this.posY + this.getEyeHeight());
        }
        deltaY *= this.isUpsideDown ? -1.0D : 1.0D;

//        deltaX *= this.isUpsideDown ? -1.0D : 1.0D;
//        deltaZ *= this.isUpsideDown ? -1.0D : 1.0D;

        double distVecXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        float yawRotation = (float) ((this.isUpsideDown ? -1.0D : 1.0D) * (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI)) - 90.0F;
        float pitchRotation = (float) -(Math.atan2(deltaY, distVecXZ) * 180.0D / Math.PI);
//        yawRotation -= this.isUpsideDown ? 180.0F : 0.0F;
        this.rotationPitch = -this.updateRotation(this.rotationPitch, pitchRotation);
        this.rotationYawHead = this.updateRotation(this.rotationYawHead, yawRotation);
    }

    protected float updateRotation(float prevRotation, float newRotation) {
        float var4 = MathHelper.wrapAngleTo180_float(newRotation - prevRotation);
        return prevRotation + var4;
    }

    @Override
    public void onLivingUpdate() {
        this.rotationYaw = 0.0F;

        if( this.blockPos == null ) {
            this.blockPos = Triplet.with((int) Math.floor(this.posX), (int)Math.floor(this.posY) + (this.isUpsideDown ? 3 : 0), (int)Math.floor(this.posZ));
        }

        if( !canTurretBePlaced(this.worldObj, this.blockPos.getValue0(), this.blockPos.getValue1(), this.blockPos.getValue2(), true, this.isUpsideDown) ) {
            this.attackEntityFrom(DamageSource.magic, Float.MAX_VALUE);
        }

        if( this.newPosRotationIncrements > 0 ) {
            this.rotationPitch = (float) (this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / (float) this.newPosRotationIncrements);
            this.newPosRotationIncrements--;
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        this.worldObj.theProfiler.startSection("ai");

        if( this.isMovementBlocked() ) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if( !this.worldObj.isRemote ) {
            this.worldObj.theProfiler.startSection("oldAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
        }

        if( !this.worldObj.isRemote ) {
            this.targetProc.onTick();
        }
//        if( this.entityToAttack == null ) {
//            List entities = this.worldObj.getEntitiesWithinAABB(IMob.class, this.boundingBox.expand(16.0D, 16.0D, 16.0D));
//            for( Object e : entities ) {
//                Entity entity = (Entity) e;
//                if( !(entity.isDead || entity.isEntityInvulnerable()) ) {
//                    this.entityToAttack = entity;
//                    break;
//                }
//            }
//
////            this.entityToAttack = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 16.0D);
//        } else if( !this.worldObj.isRemote && this.ticksExisted % 20 == 0 ) {
//            EntityTurretProjectile projectile = new EntityTurretProjectile(this.worldObj, this, this.entityToAttack);
//            this.worldObj.spawnEntityInWorld(projectile);
//        }
//
//        if( this.entityToAttack != null && (this.entityToAttack.isDead || this.entityToAttack.isEntityInvulnerable() || this.getDistanceToEntity(this.entityToAttack) > 16.0D) ) {
//            this.entityToAttack = null;
//        }

        this.worldObj.theProfiler.endSection();

        if( this.targetProc.hasTarget() ) {
            this.faceEntity(this.targetProc.getTarget(), 10.0F, this.getVerticalFaceSpeed());
        } else if( this.worldObj.isRemote && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) ) {
            this.rotationYawHead += 1.0F;
            this.rotationPitch = 0.0F;
        }
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        ItemStack heldItem = player.getCurrentEquippedItem();
        if( this.worldObj.isRemote ) {
            if( ItemStackUtils.isValidStack(heldItem) && heldItem.getItem() == ItemRegistry.tcu ) {
//                this.showRange = !this.showRange;
//                this.ignoreFrustumCheck = this.showRange;
                TurretModRebirth.proxy.openGui(player, EnumGui.GUI_TCU_TARGETS, this.getEntityId(), 0, 0);
                return true;
            }

            return false;
        } else if( ItemStackUtils.isValidStack(heldItem) ) {
            if( this.targetProc.isAmmoApplicable(heldItem) ) {
                boolean succeed = this.targetProc.addAmmo(heldItem);
                if( succeed ) {
                    if( heldItem.stackSize == 0 ) {
                        player.destroyCurrentEquippedItem();
                    } else {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem.copy());
                    }
                    this.updateState();
                    player.inventoryContainer.detectAndSendChanges();
                    this.worldObj.playSoundAtEntity(this, TurretModRebirth.ID + ":collect.ia_get", 1.0F, 1.0F);
                }
                return succeed;
            } else if( heldItem.getItem() == ItemRegistry.repairKit ) {
                TurretRepairKit repKit = ItemRegistry.repairKit.getRepKitType(heldItem);
                if( repKit != null && repKit.isApplicable(this) ) {
                    this.heal(repKit.getHealAmount());
                    repKit.onHeal(this);
                    heldItem.stackSize--;
                    if( heldItem.stackSize == 0 ) {
                        player.destroyCurrentEquippedItem();
                    } else {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem.copy());
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    this.worldObj.playSoundAtEntity(this, TurretModRebirth.ID + ":collect.ia_get", 1.0F, 1.0F);
                    return true;
                }
            }
        }

        return super.interact(player);
    }

    @Override
    public void onDeath(DamageSource dmgSrc) {
        super.onDeath(dmgSrc);

        if( !this.worldObj.isRemote ) {
            this.targetProc.dropAmmo();
        }

        //just insta-kill it
        this.setDead();
    }

    @Override
    protected void updateEntityActionState() {
        ++this.entityAge;
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
    }

    public TargetProcessor getTargetProcessor() {
        return this.targetProc;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        NBTTagCompound targetNbt = new NBTTagCompound();
        this.targetProc.writeToNbt(targetNbt);
        ByteBufUtils.writeTag(buffer, targetNbt);
        buffer.writeBoolean(this.isUpsideDown);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.targetProc.readFromNbt(ByteBufUtils.readTag(buffer));
        this.isUpsideDown = buffer.readBoolean();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        this.targetProc.writeToNbt(nbt);

        nbt.setBoolean("isUpsideDown", this.isUpsideDown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        this.targetProc.readFromNbt(nbt);

        this.isUpsideDown = nbt.getBoolean("isUpsideDown");
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 5;
    }

    /**turrets are machines, they aren't affected by potions*/
    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect != null && effect.getIsAmbient();
    }

    /**turrets are immobile, leave empty*/
    @Override
    public void knockBack(Entity entity, float unknown, double motionXAmount, double motionZAmount) { }

    /**turrets are immobile, leave empty*/
    @Override
    public void moveEntity(double motionX, double motionY, double motionZ) { }

    /**turrets are immobile, leave empty*/
    @Override
    public void moveEntityWithHeading(float strafe, float forward) { }

    public abstract ResourceLocation getStandardTexture();

    public abstract ResourceLocation getGlowTexture();

    public void updateState() {
        PacketRegistry.sendToAllAround(new PacketUpdateTurretState(this), this.dimension, this.posX, this.posY, this.posZ, 64.0D);
    }

    public static boolean canTurretBePlaced(World world, int x, int y, int z, boolean doBlockCheckOnly, boolean updideDown) {
        if( !Blocks.lever.canPlaceBlockAt(world, x, y, z) ) {
            return false;
        }

        if( !doBlockCheckOnly ) {
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + (updideDown ? - 1.0D : 1.0D), z + 1.0D);
            if( !world.getEntitiesWithinAABB(EntityTurret.class, aabb).isEmpty() ) {
                return false;
            }
        }

        return true;
    }
}
