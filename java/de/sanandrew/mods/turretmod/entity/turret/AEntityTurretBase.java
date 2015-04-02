/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import com.mojang.authlib.GameProfile;
import de.sanandrew.mods.turretmod.util.ITurretInfo;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.UUID;

public abstract class AEntityTurretBase
        extends EntityLiving
        implements ITurretInfo
{
    private static final int DW_AMMO = 20;
    private static final int DW_EXPERIENCE = 21;
    private static final int DW_OWNER_UUID = 22;
    private static final int DW_OWNER_NAME = 23;
    private static final int DW_MYNAME = 24;
    private static final int DW_TARGET = 25;
    private static final int DW_SHOOT_TICKS = 26;
    private static final int DW_FREQUENCY = 27;
    private static final int DW_BOOLEANS = 28;

    protected Entity currentTarget;

    private final IEntitySelector parentTargetSelector = new IEntitySelector() {
        @Override public boolean isEntityApplicable(Entity entity) {
            return entity.isEntityAlive() && !entity.isEntityInvulnerable() && AEntityTurretBase.this.getTargetSelector().isEntityApplicable(entity);
        }
    };

    public AEntityTurretBase(World par1World) {
        super(par1World);
        this.setSize(0.3F, 1.8F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

//        this.dataWatcher.addObject(DW_AMMO, (short) 0);
//        this.dataWatcher.addObject(DW_AMMOTYPE, (short) 0); // Ammo type
        this.dataWatcher.addObject(DW_AMMO, 0); // Ammo Count
        this.dataWatcher.addObject(DW_EXPERIENCE, 0); // Experience
        this.dataWatcher.addObject(DW_OWNER_UUID, ""); // Player UUID
        this.dataWatcher.addObject(DW_OWNER_NAME, ""); // Player name
        this.dataWatcher.addObject(DW_MYNAME, this.getDefaultName()); // Turret name
        this.dataWatcher.addObject(DW_TARGET, ""); // Target name
        this.dataWatcher.addObject(DW_SHOOT_TICKS, this.getMaxShootTicks()); // shootTicks
        this.dataWatcher.addObject(DW_FREQUENCY, (byte) 0); // frequency

        this.dataWatcher.addObject(DW_BOOLEANS, (byte) 0);   // boolean stuff

        this.setActiveState(true);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void faceEntity(Entity entity, float yawSpeed, float pitchSpeed) {
        if( entity == null ) {
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

        double distVecXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        float yawRotation = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
        float pitchRotation = (float) -(Math.atan2(deltaY, distVecXZ) * 180.0D / Math.PI);
        this.rotationPitch = -this.updateRotation(this.rotationPitch, pitchRotation);
        this.rotationYawHead = this.updateRotation(this.rotationYawHead, yawRotation);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        return false;
    }

    @Override
    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
//        super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_, p_70653_5_); // TODO: apply knockback when riding a mobile base
    }

    @Override
    public void onLivingUpdate() {
        if( this.newPosRotationIncrements > 0 ) {
            double newX = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
            double newY = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
            double newZ = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;

            this.rotationPitch = (float) (this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / (float) this.newPosRotationIncrements);
            this.newPosRotationIncrements--;
            this.setPosition(newX, newY, newZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if( this.worldObj.isRemote ) {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if( Math.abs(this.motionX) < 0.005D ) {
            this.motionX = 0.0D;
        }

        if( Math.abs(this.motionY) < 0.005D ) {
            this.motionY = 0.0D;
        }

        if( Math.abs(this.motionZ) < 0.005D ) {
            this.motionZ = 0.0D;
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

        this.worldObj.theProfiler.endSection();

//        if (this.worldObj.isRemote && this.ticksExisted == 5) {
//            PacketSendUpgrades.send(this);
//        }

        if( this.isActive() && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) && !this.hasTarget() ) {
            this.rotationYawHead += 1.0F;
            this.rotationPitch = 0.0F;
        }
    }

    @Override
    public void onUpdate() {
        this.motionY = 0.0F;
        Block belowBlock = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ));
        if(belowBlock != null ) {
            AxisAlignedBB aabb = belowBlock.getCollisionBoundingBoxFromPool(this.worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1,
                                                                            MathHelper.floor_double(this.posZ));
            if( aabb == null || !this.boundingBox.intersectsWith(aabb) ) {
                moveEntity(0.0F, -0.045, 0.0F);
            } else {
                moveEntity(0.0F, 0.0F, 0.0F);
            }
        }

        if( !this.isActive() ) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(null);
            }

            if( this.rotationPitch < 25 ) {
                this.rotationPitch += 0.5F;
            }

            this.prevRotationYawHead = this.rotationYawHead;

            super.onUpdate();

            return;
        }

        if( this.getShootTicks() > 0 ) {
            this.dataWatcher.updateObject(DW_SHOOT_TICKS, this.getShootTicks() - 1);
        }

        EntityPlayer riddenPlayer = (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) ? (EntityPlayer) this.riddenByEntity : null;
        if( riddenPlayer != null ) {
            this.prevRotationPitch = this.rotationPitch = riddenPlayer.rotationPitch;
        }

        super.onUpdate();

        if( riddenPlayer != null ) {
            this.prevRotationPitch = this.rotationPitch = riddenPlayer.rotationPitch;
            this.rotationYawHead = riddenPlayer.rotationYawHead;
            if (!this.worldObj.isRemote) this.dataWatcher.updateObject(26, "");
        } else {
            updateTarget();

            shoot(false);

            this.rotationYaw = this.prevRotationYaw = this.renderYawOffset = this.prevRenderYawOffset = 0.0F;
        }

//        if( !this.worldObj.isRemote ) {
//            if (this.ticksExisted % 5 == 0 && TurretUpgrades.hasUpgrade(TUpgChestGrabbing.class, this.upgrades))
//                this.grabContentFromChests();
//            this.dataWatcher.updateObject(21, this.getHealth());
//        }

        if( this.ridingEntity == null ) {
            if (this.posX > Math.floor(posX) + 0.501F || this.posX < Math.floor(posX) + 0.499F) {
                this.posX = Math.floor(posX) + 0.5;
                this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            }
            if (this.posZ > Math.floor(posZ) + 0.501F || this.posZ < Math.floor(posZ) + 0.499F) {
                this.posZ = Math.floor(posZ) + 0.5;
                this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            }
        }
    }

    @Override
    protected void updateEntityActionState() {
        ++this.entityAge;
//        this.despawnEntity();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;

        if( !this.isActive() ) {
            return;
        }

        if( this.currentTarget != null ) {
            this.faceEntity(this.currentTarget, 10.0F, this.getVerticalFaceSpeed());
        } else if( this.worldObj.isRemote && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) ) {
            this.rotationYawHead += 1.0F;
            this.rotationPitch = 0.0F;
        }
    }

    public void shoot(boolean isRidden) {
        if( !this.worldObj.isRemote && this.getHealth() > 0 && (isRidden || this.hasTarget()) ) {
            if( this.getShootTicks() == 0 ) {
//                if( this.getAmmo() > 0 ) {// TODO: reimplement ammo cnt
                    this.shootProjectile(isRidden);
//                    this.decrAmmo();
//                } else {
//                    this.playSound("random.click", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
//                }
//                this.setShootTicks(this.getMaxShootTicks());
                this.dataWatcher.updateObject(DW_SHOOT_TICKS, this.getMaxShootTicks());
            }
        }
    }

    public void shootProjectile(boolean isRidden) {
//        TurretProjectile var2 = this.getProjectile();
        EntityArrow arrow = new EntityArrow(this.worldObj, this, (EntityLivingBase) this.currentTarget, 1.0F, 10.0F);
//        var2.isPickupable = !TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, this.upgrades) && !this.isEconomied;
//        var2.ammoType = this.getAmmoType();
//        if (isRidden) {
//            EntityPlayer player = (EntityPlayer) this.riddenByEntity;
//            var2.hasNoTarget = var2.isEndermanDamageable = true;
//            var2.setLocationAndAngles(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, player.rotationYaw, player.rotationPitch);
//            var2.posX -= (double)(MathHelper.cos(var2.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
//            var2.posY -= 0.10000000149011612D;
//            var2.posZ -= (double)(MathHelper.sin(var2.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
//            var2.setPosition(var2.posX, var2.posY, var2.posZ);
//            var2.yOffset = 0.0F;
//            var2.motionX = (double)(-MathHelper.sin(var2.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(var2.rotationPitch / 180.0F * (float)Math.PI));
//            var2.motionZ = (double)(MathHelper.cos(var2.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(var2.rotationPitch / 180.0F * (float)Math.PI));
//            var2.motionY = (double)(-MathHelper.sin(var2.rotationPitch / 180.0F * (float)Math.PI));
//            var2.setHeading(var2.motionX, var2.motionY, var2.motionZ, 1F * 1.5F, 1.0F);
//            var2.shootingEntity = this;
//        } else {
//            var2.setTarget(this, this.currentTarget, 1.4F, 0.0F);
//        }
//        var2.isEndermanDamageable = TurretUpgrades.hasUpgrade(TUpgEnderHitting.class, this.upgrades);
//        if(this.getShootSound()!=null)
//            this.playSound(this.getShootSound(), this.getShootSoundRng(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(arrow);
//        var2.isMoving = true;
    }

    public String getDefaultName() {
        return "Turret";
    }

    @SuppressWarnings("unchecked")
    protected List<Entity> getValidTargets() {
        return this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getRangeBB(), this.parentTargetSelector);
    }

    protected void updateTarget() {
        if( !this.worldObj.isRemote ) {
            if( this.currentTarget == null ) {
                List<Entity> targets = this.getValidTargets();
                if( targets.size() > 0 ) {
                    this.currentTarget = targets.get(this.rand.nextInt(targets.size()));
                    this.dataWatcher.updateObject(DW_TARGET, EntityList.getEntityString(this.currentTarget));
                }
            } else {
                if( !this.parentTargetSelector.isEntityApplicable(this.currentTarget) ) {
                    this.currentTarget = null;
                    this.dataWatcher.updateObject(DW_TARGET, "");
                }
            }
        }
    }

    protected float updateRotation(float prevRotation, float newRotation) {
        float var4 = MathHelper.wrapAngleTo180_float(newRotation - prevRotation);
        return prevRotation + var4;
    }

    public abstract AxisAlignedBB getRangeBB();
    public abstract int getMaxShootTicks();
    protected abstract IEntitySelector getTargetSelector();

    // GETTERS
    public int getExperience() {
        return this.dataWatcher.getWatchableObjectInt(DW_EXPERIENCE);
    }

    public int getFrequency() {
        return this.dataWatcher.getWatchableObjectByte(DW_FREQUENCY) & 255;
    }

    public UUID getOwnerUUID() {
        String uuidStr = this.dataWatcher.getWatchableObjectString(DW_OWNER_UUID);
        if( uuidStr != null && !uuidStr.isEmpty() ) {
            try {
                return UUID.fromString(this.dataWatcher.getWatchableObjectString(DW_OWNER_UUID));
            } catch( IllegalArgumentException ex ) {
                TurretMod.MOD_LOG.printf(Level.WARN, "Illegal Owner set, resetting to no owner! %s", ex.getMessage());
                this.setOwner(null);
                return null;
            }
        }

        return null;
    }

    public String getOwnerName() {
        String name = this.dataWatcher.getWatchableObjectString(DW_OWNER_NAME);
        return name.isEmpty() ? null : name;
    }

    public String getTurretName() {
        return this.dataWatcher.getWatchableObjectString(DW_MYNAME);
    }

    public String getTargetName() {
        return this.dataWatcher.getWatchableObjectString(DW_TARGET);
    }

    public int getShootTicks() {
        return this.dataWatcher.getWatchableObjectInt(DW_SHOOT_TICKS);
    }

    public boolean isActive() {
        return (this.dataWatcher.getWatchableObjectByte(DW_BOOLEANS) & 1) == 1;
    }

    public boolean hasTarget() {
        if( !this.worldObj.isRemote ) {
            return this.currentTarget != null;
        } else {
            String targetName = this.getTargetName();
            return targetName != null && !targetName.isEmpty();
        }
    }

    // SETTERS
    public void setOwner(EntityPlayer player) {
        if( player != null ) {
            GameProfile profile = player.getGameProfile();
            this.dataWatcher.updateObject(DW_OWNER_UUID, profile.getId().toString());
            this.dataWatcher.updateObject(DW_OWNER_NAME, profile.getName());
        } else {
            this.dataWatcher.updateObject(DW_OWNER_UUID, "");
            this.dataWatcher.updateObject(DW_OWNER_NAME, "");
        }
    }

    public void setTurretName(String name) {
        if( name != null && !name.isEmpty() ) {
            this.dataWatcher.updateObject(DW_MYNAME, name);
        } else {
            this.dataWatcher.updateObject(DW_MYNAME, this.getDefaultName());
        }
    }

    public void setActiveState(boolean isActive) {
        this.setDwBoolean(1, isActive);
    }

    private void setDwBoolean(int flag, boolean state) {
        byte dwVal = this.dataWatcher.getWatchableObjectByte(DW_BOOLEANS);
        this.dataWatcher.updateObject(DW_BOOLEANS, (byte) (state ? (dwVal | flag) : (dwVal & ~flag)));
    }
}
