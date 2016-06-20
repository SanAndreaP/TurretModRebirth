package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

import java.util.List;

public class TurretProjectile extends EntityArrow {
    protected int xTile = -1;
    protected int yTile = -1;
    protected int zTile = -1;
    protected Block inTile = Blocks.air;
    protected int inData = 0;
    protected boolean inGround = false;
    protected int ticksInGround;
    protected int ticksInAir = 0;
    protected float knockbackStrength = 1.25F;

    public int ammoType = 0;

    public Entity targetedEntity;
    public boolean hasNoTarget;
    public boolean isMoving;
    public boolean isPickupable = true;
    public boolean isEndermanDamageable;

    public TurretProjectile(World par1World)
    {
        super(par1World);
        this.renderDistanceWeight = 10.0D;
        this.hasNoTarget = isEndermanDamageable = true;
    }

    public TurretProjectile(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
        this.hasNoTarget = isEndermanDamageable = true;
        this.renderDistanceWeight = 10.0D;
    }

    public void setTarget(EntityLivingBase par2EntityLiving, EntityLivingBase par3EntityLiving, float par4, float par5)
    {
        this.hasNoTarget = isEndermanDamageable = false;
        this.shootingEntity = par2EntityLiving;
        this.targetedEntity = par3EntityLiving;

//        this.posY = par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight() - 0.10000000149011612D;
//        double var6 = par3EntityLiving.posX - par2EntityLiving.posX;
//        double var8 = par3EntityLiving.posY + (double)par3EntityLiving.getEyeHeight() - 0.999999988079071D - this.posY;
//        double var10 = par3EntityLiving.posZ - par2EntityLiving.posZ;
//        double var12 = (double)MathHelper.sqrt_double(var6 * var6 + var10 * var10);
//
//        if (var12 >= 1.0E-7D)
//        {
//            float var14 = (float)(Math.atan2(var10, var6) * 180.0D / Math.PI) - 90.0F;
//            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / Math.PI));
//            this.setLocationAndAngles(par2EntityLiving.posX, this.posY, par2EntityLiving.posZ, var14, var15);
//            this.yOffset = 0.0F;
//            float var20 = (float)var12 * getCurveCorrector() - (float)(par2EntityLiving.rotationPitch / 180F);
//            this.setHeading(var6, var8 + (double)var20, var10, par4, par5);
//        }

        this.posY = par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight() - 0.10000000149011612D;
        double var6 = par3EntityLiving.posX - par2EntityLiving.posX;
        double var8 = par3EntityLiving.posY + (double)par3EntityLiving.getEyeHeight() - 0.699999988079071D - this.posY;
        double var10 = par3EntityLiving.posZ - par2EntityLiving.posZ;
        double var12 = (double)MathHelper.sqrt_double(var6 * var6 + var10 * var10);

        if (var12 >= 1.0E-7D)
        {
            float var14 = (float)(Math.atan2(var10, var6) * 180.0D / Math.PI) - 90.0F;
            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / Math.PI));
            double var16 = var6 / var12;
            double var18 = var10 / var12;
//            this.setPosition(par2EntityLiving.posX, this.posY, par2EntityLiving.posZ);
            this.setLocationAndAngles(par2EntityLiving.posX, this.posY, par2EntityLiving.posZ, var14, var15);
            this.yOffset = 0.0F;
            float var20 = (float)var12 * this.getCurveCorrector();
            this.setHeading(var6, var8 + (double)var20, var10, par4, par5);
        }
    }

    public void setHeading(double d1, double d2, double d3, float f1, float f2) {
        this.setThrowableHeading(d1, d2, d3, f1, f2);
        this.motionX *= this.getSpeedVal();
        this.motionY *= this.getSpeedVal();
        this.motionZ *= this.getSpeedVal();
    }

    public float getCurveCorrector() {
    	return 0.2F;
    }

    public boolean isArrow() {
    	return true;
    }

    public boolean dieOnImpact() {
    	return true;
    }

    public void processHit(MovingObjectPosition moving) {

        EntityLivingBase entity = (EntityLivingBase)moving.entityHit;

    	if (!this.worldObj.isRemote)
        {
    		if (this.isArrow())
    			entity.setArrowCountInEntity(entity.getArrowCountInEntity() + 1);
        }

    	ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, 0, "recentlyHit", "field_70718_bc");

    	float var26 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
    	moving.entityHit.motionX *= (double)this.knockbackStrength * 0.6000000238418579D / (double)var26;
    	moving.entityHit.motionZ *= (double)this.knockbackStrength * 0.6000000238418579D / (double)var26;

//        if (Math.abs(this.knockbackStrength) > 0)
//        {
//            if (var26 > 0.0F)
//            {
    	if (moving.entityHit.onGround) {
        	moving.entityHit.motionY = (double)(this.knockbackStrength / 3F);
        	moving.entityHit.isAirBorne = true;
    	}
//            }
//        }
        if(this.shootingEntity instanceof EntityLivingBase) {
            Enchantment.thorns.func_151367_b((EntityLivingBase)this.shootingEntity, entity, 1);

            if (moving.entityHit != this.shootingEntity && moving.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
            }
        }
    }

    public void onEntityHit(EntityLivingBase living) {
    	;
    }

    public boolean flyThroughOnEntityHit() {
    	return false;
    }

    public String getHitSound() {
    	return "random.bowhit";
    }

    public void spawnTail() {
//    	this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)var9 / 4.0D, this.posY + this.motionY * (double)var9 / 4.0D, this.posZ + this.motionZ * (double)var9 / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
    }

    public float getGravityVal() {
    	return 0.05F;
    }

    public float getSpeedVal() {
    	return 1.00F;
    }

    public float getMotionMulti() {
    	return 0.99F;
    }

    @Override
    public void onUpdate()
    {
    	if (!isMoving && !this.worldObj.isRemote) return;

    	this.onEntityUpdate();

    	if (!this.worldObj.isRemote && this.shootingEntity != null && (this.shootingEntity instanceof EntityTurret_Base) && this.getDistanceToEntity(this.shootingEntity) > 256D) {
    		this.motionX = 0D;
    		this.motionY = 0D;
    		this.motionZ = 0D;
    		this.setDead();
    		return;
    	}

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var1) * 180.0D / Math.PI);
        }

        Block var16 = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);

        if (var16.getMaterial() != Material.air)
        {
            var16.setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB var2 = var16.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if (var2 != null && var2.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            Block var18 = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
            int var19 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            if (var18 == this.inTile && var19 == this.inData)
            {
            	if (this.dieOnGround()) {
            		this.setDead();
            	}

                ++this.ticksInGround;

                if (this.ticksInGround == 1200)
                {
                    this.setDead();
                }
            }
            else
            {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                if (this.isArrow())
                	this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                else
                	this.motionY = -this.getSpeedVal();
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;
            Vec3 var17 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 var3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition var4 = this.worldObj.func_147447_a(var17, var3, false, true, false);//raytrace
            var17 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            var3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (var4 != null)
            {
                var3 = Vec3.createVectorHelper(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);
            }

            Entity var5 = null;
            List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double var7 = 0.0D;
            int var9;
            float var11;

            EntityDragonPart drgPart = null;

            for (var9 = 0; var9 < var6.size(); ++var9)
            {
                Entity var10 = (Entity)var6.get(var9);

                if (var10 instanceof EntityDragonPart) {
                	IEntityMultiPart multiEntity = ((EntityDragonPart) var10).entityDragonObj;
        			if (multiEntity instanceof EntityDragon) {
        				drgPart = (EntityDragonPart) var10;
        				var10 = (EntityDragon)multiEntity;
        			}
        		}


                boolean isShootEntityValid = !(this.shootingEntity == null || this.shootingEntity == this);
                boolean isDispensed = this.hasNoTarget && !isShootEntityValid;
                boolean isShootingEntity = isShootEntityValid && var10 == this.shootingEntity;
                boolean isNotRider = (isShootEntityValid && this.shootingEntity.riddenByEntity != var10) || isDispensed;
                boolean isHostRidden = isShootEntityValid && this.hasNoTarget && this.shootingEntity.riddenByEntity != null && this.shootingEntity.riddenByEntity instanceof EntityPlayer;
                boolean isTargetValid = isHostRidden
                		|| (this.shouldTargetOneType()
                				? this.targetedEntity != null && var10 != null && this.targetedEntity.getClass().isAssignableFrom(var10.getClass())
                				: this.shootingEntity instanceof EntityTurret_Base && var10 instanceof EntityLiving
                						&& ((EntityTurret_Base)this.shootingEntity).isTargetValid((EntityLiving) var10)
                						);
                boolean cannotBeHit = (var10 instanceof EntityLiving) && (float)((EntityLiving)var10).hurtResistantTime > (float)((EntityLiving)var10).maxHurtResistantTime / 2.0F || var10.isDead;


                if ((var10.canBeCollidedWith() || (var10 instanceof EntityDragon) ) && !isShootingEntity && isNotRider && (isDispensed || isTargetValid) && (!cannotBeHit || !flyThroughOnEntityHit()) )
                {
                    var11 = 0.3F;
                    AxisAlignedBB var12 = var10.boundingBox.expand((double)var11, (double)var11, (double)var11);
                    MovingObjectPosition var13 = var12.calculateIntercept(var17, var3);

                    if (var13 != null)
                    {
                        double var14 = var17.distanceTo(var13.hitVec);

                        if (var14 < var7 || var7 == 0.0D)
                        {
                            var5 = var10;
                            var7 = var14;
                        }
                    }
                }
            }

            if (var5 != null)
            {
                var4 = new MovingObjectPosition(var5);
            }

            float var20;
            float var26;

            if (var4 != null)
            {
                if (var4.entityHit != null)
                {
                    int var23 = MathHelper.ceiling_double_int(this.getDamage());

                    DamageSource var21 = null;

                    if (this.shootingEntity == null)
                    {
                        var21 = DamageSource.causeArrowDamage(this, this);
                    }
                    else if (this.isEndermanDamageable && this.shootingEntity != null && this.shootingEntity instanceof EntityLiving)
                    {
                    	var21 = DamageSource.causeMobDamage((EntityLiving)this.shootingEntity);
                    }
                    else
                    {
                        var21 = DamageSource.causeArrowDamage(this, this.shootingEntity);
                    }

//                    System.out.println (var4.entityHit);
                    if (var4.entityHit instanceof EntityDragon && drgPart != null) {
                    	if (((EntityDragon)var4.entityHit).attackEntityFromPart(drgPart, DamageSource.generic.setExplosion(), var23)) {
                    		if (var4.entityHit instanceof EntityLiving)
                            {
                                processHit(var4);
                                onEntityHit((EntityLiving) var4.entityHit);
                            }

                            this.playSound(getHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                            if (this.dieOnImpact()) this.setDead();
                    	}
//                    } else if (true) {
                    } else if (var4.entityHit.attackEntityFrom(var21, var23)) {
                        if (var4.entityHit instanceof EntityLiving)
                        {
                        	EntityLiving living = (EntityLiving)var4.entityHit;
                        	if (this.shootingEntity != null && this.shootingEntity instanceof EntityTurret_Base) {
                        		EntityTurret_Base turret = (EntityTurret_Base) this.shootingEntity;
                        		if (turret.getDistanceToEntity(living) <= 16.0F) {
	                        		living.setRevengeTarget(turret);
	                        		living.setAttackTarget(turret);
	                        		living.setLastAttacker(turret);
	                        		ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, living, turret, "currentTarget", "field_70776_bF");
                        		} else if (living instanceof EntityCreature && living instanceof IMob) {
                        			PathEntity path = this.worldObj.getEntityPathToXYZ(living, (int)turret.posX, (int)turret.posY, (int)turret.posZ, (float) turret.wdtRange*2F, true, false, false, true);
                        			living.getNavigator().setPath(path, 0.35F);
                        			((EntityCreature)living).setPathToEntity(path);
                        		}
                        	}
                            processHit(var4);
                            onEntityHit(living);
                        }

                        this.playSound(getHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                        if (!(var4.entityHit instanceof EntityEnderman) || this.isEndermanDamageable)
                        {
                        	if (this.dieOnImpact()) this.setDead();
                        }
                    }
                    else if (this.shouldTargetOneType() ? (this.targetedEntity != null
                    		&& this.targetedEntity.getClass().isAssignableFrom(var4.entityHit.getClass()))
                    	:
                    		(this.shootingEntity != null
                    		&& var4.entityHit instanceof EntityLiving
                    		&& this.shootingEntity instanceof EntityTurret_Base
                    		&& ((EntityTurret_Base)this.shootingEntity).isTargetValid((EntityLiving)var4.entityHit)))
                    {
                    	this.processFailedHit(var4.entityHit);
                    }
                }
                else
                {
                    this.xTile = var4.blockX;
                    this.yTile = var4.blockY;
                    this.zTile = var4.blockZ;
                    this.inTile = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = (double)((float)(var4.hitVec.xCoord - this.posX));
                    this.motionY = (double)((float)(var4.hitVec.yCoord - this.posY));
                    this.motionZ = (double)((float)(var4.hitVec.zCoord - this.posZ));
                    var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double)var20 * 0.05000000074505806D;
                    this.posY -= this.motionY / (double)var20 * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double)var20 * 0.05000000074505806D;
                    this.playSound(getHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
//                    if (this.isArrow())
                    	this.arrowShake = 7;
                    this.setIsCritical(false);

                    if (this.inTile != Blocks.air)
                    {
                        this.inTile.onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
                    }
                }
            }

            for (var9 = 0; var9 < 4; ++var9)
            {
            	spawnTail();
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var20) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float var22 = getMotionMulti();
            var11 = getGravityVal();

            if (this.isInWater())
            {
                for (int var25 = 0; var25 < 4; ++var25)
                {
                    var26 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var26, this.posY - this.motionY * (double)var26, this.posZ - this.motionZ * (double)var26, this.motionX, this.motionY, this.motionZ);
                }

                var22 = 0.8F;
            }

            this.motionX *= (double)var22;
            this.motionY *= (double)var22;
            this.motionZ *= (double)var22;
            this.motionY -= (double)var11;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();//collide with block
        }
    }

    protected void processFailedHit(Entity hit) {
    	if (this.isArrow()) {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
    	}
    }

    protected boolean shouldTargetOneType() {
    	return true;
    }

    @Override
    public void playSound(String sound, float volume, float pitch){
        if(sound!=null && !sound.isEmpty())
            super.playSound(sound, volume, pitch);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short)this.xTile);
        par1NBTTagCompound.setShort("yTile", (short)this.yTile);
        par1NBTTagCompound.setShort("zTile", (short)this.zTile);
        par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(this.inTile));
        par1NBTTagCompound.setByte("inData", (byte)this.inData);
        par1NBTTagCompound.setByte("shake", (byte)this.arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        par1NBTTagCompound.setByte("pickup", (byte)this.canBePickedUp);
        par1NBTTagCompound.setBoolean("dispensed", this.hasNoTarget);
        par1NBTTagCompound.setBoolean("move", this.isMoving);
        par1NBTTagCompound.setBoolean("isPickupable", this.isPickupable);
        par1NBTTagCompound.setInteger("ammoType", this.ammoType);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xTile = par1NBTTagCompound.getShort("xTile");
        this.yTile = par1NBTTagCompound.getShort("yTile");
        this.zTile = par1NBTTagCompound.getShort("zTile");
        this.inTile = Block.getBlockById(par1NBTTagCompound.getByte("inTile") & 255);
        this.inData = par1NBTTagCompound.getByte("inData") & 255;
        this.arrowShake = par1NBTTagCompound.getByte("shake") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
        this.hasNoTarget = par1NBTTagCompound.getBoolean("dispensed");
        this.isMoving = par1NBTTagCompound.getBoolean("move");
        this.isPickupable = par1NBTTagCompound.getBoolean("isPickupable");
        this.ammoType = par1NBTTagCompound.getInteger("ammoType");
    }

    public ItemStack getPickupItem() {
    	return null;
    }

    public boolean dieOnGround() {
		return false;
	}

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
    	ItemStack is = this.getPickupItem();
		if (!this.worldObj.isRemote && is != null && this.inGround && this.arrowShake <= 0 && this.isPickupable)
	    {
	        boolean var2 = true;

	        if (!par1EntityPlayer.inventory.addItemStackToInventory(is))
	        {
	            var2 = false;
	        }

	        if (var2)
	        {
	            this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
	            par1EntityPlayer.onItemPickup(this, 1);
	            this.setDead();
	        }
	    }
    }

    @Override
    public double getDamage()
    {
        return 2.0D;
    }

    @Override
    public void setKnockbackStrength(int par1)
    {
        this.knockbackStrength = par1;
    }

    public void setKnockbackStrength(float par1)
    {
    	this.knockbackStrength = par1;
    }
}
