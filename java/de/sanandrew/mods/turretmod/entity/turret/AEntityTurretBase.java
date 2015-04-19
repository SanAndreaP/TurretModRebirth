/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.network.packet.PacketTargetList;
import de.sanandrew.mods.turretmod.network.packet.PacketTargetListRequest;
import de.sanandrew.mods.turretmod.util.*;
import de.sanandrew.mods.turretmod.util.TurretRegistry.AmmoInfo;
import de.sanandrew.mods.turretmod.util.TurretRegistry.HealInfo;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.logging.log4j.Level;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public abstract class AEntityTurretBase
        extends EntityLiving
        implements ITurretInfo
{
    private static final int DW_AMMO = 20; /* INT */
    private static final int DW_AMMO_TYPE = 21; /* ITEM_STACK */
    private static final int DW_EXPERIENCE = 22; /* INT */
    private static final int DW_OWNER_UUID = 23; /* STRING */
    private static final int DW_OWNER_NAME = 24; /* STRING */
//    private static final int DW_TURRET_NAME = 25; /* STRING */
    private static final int DW_TARGET = 26; /* STRING */
    private static final int DW_SHOOT_TICKS = 27; /* INT */
    private static final int DW_FREQUENCY = 28; /* BYTE */
    private static final int DW_BOOLEANS = 29; /* BYTE */

    protected Entity currentTarget;

    private final IEntitySelector parentTargetSelector = new IEntitySelector() {
        @Override public boolean isEntityApplicable(Entity entity) {
            return entity instanceof EntityLiving && this.isTargetApplicable((EntityLiving) entity);
        }

        private boolean isTargetApplicable(EntityLiving living) {
            return AEntityTurretBase.this.activeTargets != null && AEntityTurretBase.this.activeTargets.get(living.getClass())
                   && living.isEntityAlive() && !living.isEntityInvulnerable() && AEntityTurretBase.this.getTargetSelector().isEntityApplicable(living);
        }
    };

    protected final TurretRegistry<? extends AEntityTurretBase> myInfo = TurretRegistry.getTurretInfo(this.getClass());

    protected Map<Class<? extends EntityLiving>, Boolean> activeTargets = Maps.newHashMap();
    protected Integer targetMapHash = null;

    public AEntityTurretBase(World par1World) {
        super(par1World);
        this.setSize(0.3F, 1.8F);

        Predicate<Class> entityTargetPredicate = new Predicate<Class>()
        {
            @Override
            public boolean apply(Class input) {
                return EntityLiving.class.isAssignableFrom(input) && input != EntityLivingBase.class && !AEntityTurretBase.class.isAssignableFrom(input);
            }
        };
        @SuppressWarnings("unchecked")
        Collection<Class<? extends EntityLiving>> livingClsList = Collections2.filter(EntityList.stringToClassMapping.values(), entityTargetPredicate);

        for( Class<? extends EntityLiving> livingCls : livingClsList ) {
            this.activeTargets.put(livingCls, IMob.class.isAssignableFrom(livingCls));
        }

        if( !this.worldObj.isRemote ) {
            this.targetMapHash = livingClsList.hashCode();
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataWatcher.addObject(DW_AMMO, 0); // Ammo Count
        this.dataWatcher.addObjectByDataType(DW_AMMO_TYPE, 5 /*ITEM_STACK*/); // Ammo Count
        this.dataWatcher.addObject(DW_EXPERIENCE, 0); // Experience
        this.dataWatcher.addObject(DW_OWNER_UUID, ""); // Player UUID
        this.dataWatcher.addObject(DW_OWNER_NAME, ""); // Player name
//        this.dataWatcher.addObject(DW_TURRET_NAME, this.getDefaultName()); // Turret name
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
    public void knockBack(Entity entity, float unknown, double motionXAmount, double motionZAmount) {
//        super.knockBack(entity, unknown, motionXAmount, motionZAmount); // TODO: apply knockback when riding a mobile base
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

        if( this.isActive() && !(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) && !this.hasTarget() ) {
            this.rotationYawHead += 1.0F;
            this.rotationPitch = 0.0F;
        }
    }

    @Override
    public void onUpdate() {
        int currTargetHash = this.activeTargets.hashCode();
        if( this.targetMapHash == null || this.targetMapHash != currTargetHash ) {
            if( this.worldObj.isRemote ) {
                PacketTargetListRequest.sendPacket(this);
            } else {
                PacketTargetList.sendPacket(this);
            }
            this.targetMapHash = currTargetHash; // prevent resending packet until it arrives, TODO: maybe do a tick counter if the packet gets lost...
        }

        this.motionY -= 0.045F;
        Block belowBlock = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ));
        if(belowBlock != null ) {
            AxisAlignedBB aabb = belowBlock.getCollisionBoundingBoxFromPool(this.worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1,
                                                                            MathHelper.floor_double(this.posZ));
            if( aabb != null && this.boundingBox.intersectsWith(aabb) ) {
                this.motionY = 0.0F;
            }

            moveEntity(0.0F, this.motionY, 0.0F);
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

    @Override
    protected String getDeathSound() {
        return TurretMod.MOD_ID + ":hit.turretDeath";
    }

    @Override
    protected String getHurtSound() {
        return TurretMod.MOD_ID + ":hit.turrethit";
    }

    @Override
    protected String getLivingSound() {
        return this.isActive() ? TurretMod.MOD_ID + ":idle.turretidle" : null;
    }

    @Override
    public int getTalkInterval() {
        return 800;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if( heldItem != null ) {
            if( !this.worldObj.isRemote ) {
                HealInfo healInfo;
                if( heldItem.getItem() == TmrItems.turretCtrlUnit ) {
                    TurretMod.proxy.openGui(player, EnumGui.GUI_TCU_PG1, this.getEntityId(), 0, 0);
                    return true;
                } else if( this.myInfo.getAmmo(heldItem) != null ) {
                    this.addAmmo(heldItem);
                    if( heldItem.stackSize <= 0 ) {
                        player.setCurrentItemOrArmor(0, null);
                    } else {
                        player.setCurrentItemOrArmor(0, heldItem.copy()); // resetting the stack with a copy prevents the creative inventory from fucking over...
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    this.playSound(TurretMod.MOD_ID + ":collect.ia_get", 1.0F, 1.0F);

                    return true;
                } else if( (healInfo = this.myInfo.getHeal(heldItem)) != null && this.getHealth() < this.getMaxHealth() ) {
                    this.heal(healInfo.getAmount());
                    heldItem.stackSize--;
                    if( heldItem.stackSize <= 0 ) {
                        player.setCurrentItemOrArmor(0, null);
                    } else {
                        player.setCurrentItemOrArmor(0, heldItem.copy()); // resetting the stack with a copy prevents the creative inventory from fucking over...
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    this.playSound(TurretMod.MOD_ID + ":collect.ia_get", 1.0F, 1.0F);

                    return true;
                }
            }
        }

        return super.interact(player);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);

        nbt.setInteger("ammoAmount", this.dataWatcher.getWatchableObjectInt(DW_AMMO));
        nbt.setInteger("turretExp", this.dataWatcher.getWatchableObjectInt(DW_EXPERIENCE));
        nbt.setString("ownerUUID", this.dataWatcher.getWatchableObjectString(DW_OWNER_UUID));
        nbt.setString("ownerName", this.dataWatcher.getWatchableObjectString(DW_OWNER_NAME));
//        nbt.setString("turretName", this.dataWatcher.getWatchableObjectString(DW_TURRET_NAME));
        nbt.setByte("frequency", this.dataWatcher.getWatchableObjectByte(DW_FREQUENCY));
        nbt.setByte("boolFlags", this.dataWatcher.getWatchableObjectByte(DW_BOOLEANS));

        ItemStack ammoStack = this.dataWatcher.getWatchableObjectItemStack(DW_AMMO_TYPE);
        if( ammoStack != null ) {
            NBTTagCompound itemNBT = new NBTTagCompound();
            ammoStack.writeToNBT(itemNBT);
            nbt.setTag("ammoItem", itemNBT);
        }

        NBTTagList targetList = new NBTTagList();
        for( Entry<Class<? extends EntityLiving>, Boolean> entry : this.activeTargets.entrySet() ) {
            if( entry.getValue() ) {
                targetList.appendTag(new NBTTagString((String) EntityList.classToStringMapping.get(entry.getKey())));
            }
        }
        nbt.setTag("targetList", targetList);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        this.dataWatcher.updateObject(DW_AMMO, nbt.getInteger("ammoAmount"));
        this.dataWatcher.updateObject(DW_EXPERIENCE, nbt.getInteger("turretExp"));
        this.dataWatcher.updateObject(DW_OWNER_UUID, nbt.getString("ownerUUID"));
        this.dataWatcher.updateObject(DW_OWNER_NAME, nbt.getString("ownerName"));
//        this.dataWatcher.updateObject(DW_TURRET_NAME, nbt.getString("turretName"));
        this.dataWatcher.updateObject(DW_FREQUENCY, nbt.getByte("frequency"));
        this.dataWatcher.updateObject(DW_BOOLEANS, nbt.getByte("boolFlags"));

        if( nbt.hasKey("ammoItem") ) {
            NBTTagCompound itemNBT = nbt.getCompoundTag("ammoItem");
            this.dataWatcher.updateObject(DW_AMMO_TYPE, ItemStack.loadItemStackFromNBT(itemNBT));
        }

        NBTTagList targetList = nbt.getTagList("targetList", NBT.TAG_STRING);
        for( int i = 0; i < targetList.tagCount(); i++ ) {
            Class entityCls = (Class) EntityList.stringToClassMapping.get(targetList.getStringTagAt(i));
            if( EntityLiving.class.isAssignableFrom(entityCls) && this.activeTargets.containsKey(entityCls) ) {
                //noinspection unchecked
                this.activeTargets.put(entityCls, true);
            }
        }
    }

    @Override
    public boolean getAlwaysRenderNameTag() {
        return true;
    }

    public void shoot(boolean isRidden) {
        if( !this.worldObj.isRemote && this.getHealth() > 0 && (isRidden || this.hasTarget()) ) {
            if( this.getShootTicks() == 0 ) {
                if( this.getAmmo() > 0 ) {
                    this.shootProjectile(isRidden);
                    this.dataWatcher.updateObject(DW_AMMO, this.getAmmo() - 1);
                    if( this.getAmmo() == 0 ) {
                        this.dataWatcher.updateObject(DW_AMMO_TYPE, null);
                    }
                } else {
                    this.playSound("random.click", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                }
                this.dataWatcher.updateObject(DW_SHOOT_TICKS, this.getMaxShootTicks());
            }
        }
    }

    public void shootProjectile(boolean isRidden) {
        EntityTurretProjectile proj = this.getProjectile();
//        EntityArrow arrow = new EntityArrow(this.worldObj, this, (EntityLivingBase) this.currentTarget, 1.0F, 10.0F);
//        arrow.setDamage(40.0D);
        proj.isPickupable = true;//!TurretUpgrades.hasUpgrade(TUpgInfAmmo.class, this.upgrades) && !this.isEconomied;
        proj.ammoType = 0;//this.getAmmoType();
//        if( isRidden ) {
//            EntityPlayer player = (EntityPlayer) this.riddenByEntity;
//            proj.hasNoTarget = proj.isActingAsMeelee = true;
//            proj.setLocationAndAngles(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, player.rotationYaw, player.rotationPitch);
//            proj.posX -= (double)(MathHelper.cos(proj.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
//            proj.posY -= 0.10000000149011612D;
//            proj.posZ -= (double)(MathHelper.sin(proj.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
//            proj.setPosition(proj.posX, proj.posY, proj.posZ);
//            proj.yOffset = 0.0F;
//            proj.motionX = (double)(-MathHelper.sin(proj.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(proj.rotationPitch / 180.0F * (float)Math.PI));
//            proj.motionZ = (double)(MathHelper.cos(proj.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(proj.rotationPitch / 180.0F * (float)Math.PI));
//            proj.motionY = (double)(-MathHelper.sin(proj.rotationPitch / 180.0F * (float)Math.PI));
//            proj.setHeading(proj.motionX, proj.motionY, proj.motionZ, 1F * 1.5F, 1.0F);
//            proj.shootingEntity = this;
//        } else {
        proj.setTarget(this, this.currentTarget, 1.4F, 0.0F);
//        }
        proj.isActingAsMeelee = false;//TurretUpgrades.hasUpgrade(TUpgEnderHitting.class, this.upgrades);
        if( this.getShootSound() != null ) {
            this.playSound(this.getShootSound(), this.getShootSoundRng(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        }
        this.worldObj.spawnEntityInWorld(proj);
        proj.isMoving = true;
    }

    public String getDefaultName() {
        return "Turret";
    }

    @SuppressWarnings("unchecked")
    protected List<Entity> getValidTargets() {
        return this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getRangeBB(), this.parentTargetSelector);
    }

    public final IEntitySelector getParentTargetSelector() {
        return this.parentTargetSelector;
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

    protected float getShootSoundRng() {
        return 1.5F;
    }

    public abstract AxisAlignedBB getRangeBB();
    public abstract int getMaxShootTicks();
    protected abstract IEntitySelector getTargetSelector();
    protected abstract EntityTurretProjectile getProjectile();
    protected abstract String getShootSound();

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
        String customName = this.getCustomNameTag();
        return customName.isEmpty() ? this.getDefaultName() : customName;//this.dataWatcher.getWatchableObjectString(DW_TURRET_NAME);
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

    public Map<Class<? extends EntityLiving>, Boolean> getTargetList() {
        return Maps.newHashMap(this.activeTargets);
    }

    public int getAmmo() {
        return this.dataWatcher.getWatchableObjectInt(DW_AMMO);
    }

    public int getMaxAmmo() {
        return 256;
    }

    public AmmoInfo getAmmoType() {
        ItemStack stack = this.dataWatcher.getWatchableObjectItemStack(DW_AMMO_TYPE);
        if( stack != null ) {
            return this.myInfo.getAmmo(stack);
        }

        return null;
    }

    public void addAmmo(ItemStack stack) {
        AmmoInfo ammoInfo = this.getAmmoType();
        if( ammoInfo != null ) {
            ItemStack typeItem = ammoInfo.getTypeItem();
            AmmoInfo newType = this.myInfo.getAmmo(stack);
            if( newType != null && SAPUtils.areStacksEqual(newType.getTypeItem(), typeItem, typeItem.hasTagCompound()) ) {
                int remainAmount = this.getMaxAmmo() - this.getAmmo();
                if( remainAmount > 0 ) {
                    int stackSubtract = Math.min(stack.stackSize, MathHelper.floor_double(remainAmount / (double) newType.getAmount()));
                    this.dataWatcher.updateObject(DW_AMMO, this.getAmmo() + newType.getAmount() * stackSubtract);
                    stack.stackSize -= stackSubtract;
                }
            }
        } else {
            AmmoInfo newInfo = this.myInfo.getAmmo(stack);
            if( newInfo != null ) {
                this.dataWatcher.updateObject(DW_AMMO_TYPE, newInfo.getTypeItem());

                int stackSubtract = Math.min(stack.stackSize, MathHelper.floor_double(this.getMaxAmmo() / (double) newInfo.getAmount()));
                this.dataWatcher.updateObject(DW_AMMO, newInfo.getAmount() * stackSubtract);
                stack.stackSize -= stackSubtract;
            }
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

//    public void setTurretName(String name) {
//        if( name != null && !name.isEmpty() ) {
//            this.dataWatcher.updateObject(DW_TURRET_NAME, name);
//        } else {
//            this.dataWatcher.updateObject(DW_TURRET_NAME, this.getDefaultName());
//        }
//    }

    public void setActiveState(boolean isActive) {
        this.setDwBoolean(1, isActive);
    }

    public void setTargetList(List<Class<? extends EntityLiving>> activeTargetClsList) {
        for( Class<? extends EntityLiving> cls : this.activeTargets.keySet() ) {
            this.activeTargets.put(cls, activeTargetClsList.contains(cls));
        }
        this.targetMapHash = this.activeTargets.hashCode();
    }

    public void toggleTarget(Class<? extends EntityLiving> entityCls, boolean flag) {
        this.activeTargets.put(entityCls, flag);
    }

    private void setDwBoolean(int flag, boolean state) {
        byte dwVal = this.dataWatcher.getWatchableObjectByte(DW_BOOLEANS);
        this.dataWatcher.updateObject(DW_BOOLEANS, (byte) (state ? (dwVal | flag) : (dwVal & ~flag)));
    }
}
