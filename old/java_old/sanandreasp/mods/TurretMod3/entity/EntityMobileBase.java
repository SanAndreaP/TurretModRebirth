package sanandreasp.mods.TurretMod3.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class EntityMobileBase extends EntityLiving implements IHealable {

	private final float stdHeight = 0.4F;
//	private final float stdWidth = 0.4F;

	public EntityMobileBase(World par1World) {
		super(par1World);
		this.setSize(0.99F, stdHeight);
	}

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }

	@Override
	public void onLivingUpdate() {
		if (this.riddenByEntity != null) {
			this.riddenByEntity.onUpdate();
		}

        if (this.newPosRotationIncrements > 0)
        {
            double var1 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double var3 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double var5 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            --this.newPosRotationIncrements;
            this.setPosition(var1, var3, var5);
        }
		else if (!this.isClientWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.startSection("ai");

        this.worldObj.theProfiler.startSection("oldAi");
        this.updateEntityActionState();
        this.worldObj.theProfiler.endSection();

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isRemote)
        {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();

        if (!this.worldObj.isRemote) this.dataWatcher.updateObject(20, (byte) (this.isRidden() ? 1 : 0));
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (this.riddenByEntity != null && ticksExisted == 20) {
			this.riddenByEntity.mountEntity(null);
		}
		if (this.ridingEntity != null && ticksExisted == 15) {
			this.mountEntity(null);
		}

		if (this.isRidden()) {
			this.setSize(0.3F, 1.4F);
		} else {
			this.setSize(0.99F, this.stdHeight);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		if (!this.worldObj.isRemote && this.riddenByEntity == null && par1Entity instanceof EntityTurret_Base) {
			par1Entity.mountEntity(this);
		}
		if (par1Entity instanceof EntityTurret_Base)
			return null;
		return this.boundingBox;
	}

	public boolean isRidden() {
		return (!this.worldObj.isRemote && this.riddenByEntity != null && this.riddenByEntity instanceof EntityTurret_Base) || (this.worldObj.isRemote && this.dataWatcher.getWatchableObjectByte(20) == 1);
	}

	@Override
	public double getMountedYOffset() {
		return 0.2D;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean shouldRiderFaceForward(EntityPlayer player) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity par1Entity) {
//		if (!(par1Entity instanceof EntityTurret_Base))
			super.collideWithEntity(par1Entity);
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer) {
		return this.isRidden() ? ((EntityTurret_Base)this.riddenByEntity).interact(par1EntityPlayer) : super.interact(par1EntityPlayer);
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
		if (this.riddenByEntity != null) {
			this.riddenByEntity.attackEntityFrom(par1DamageSource, par2);
			return false;
		}
		if (par1DamageSource.getSourceOfDamage() instanceof EntityPlayer) {
			if (!((EntityPlayer)par1DamageSource.getSourceOfDamage()).capabilities.isCreativeMode && !this.worldObj.isRemote)
				this.entityDropItem(new ItemStack(TM3ModRegistry.mobileBase, 1), 0.0F);
			this.playSound("turretmod3:hit.turretDeath", 1.0F, 1.0F);
			onDeathUpdate();
			return true;
		}
		return super.attackEntityFrom(par1DamageSource, par2);
	}

	@Override
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime > 0)
        {
            int var1;

            this.setDead();

            for (var1 = 0; var1 < 20 && this.worldObj.isRemote; ++var1)
            {
            	this.worldObj.spawnParticle("iconcrack_"+TM3ModRegistry.mobileBase, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height)+0.2D, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextDouble()*0.2D-0.1D, this.rand.nextDouble()*0.2D-0.1D, this.rand.nextDouble()*0.2D-0.1D);
            	this.worldObj.spawnParticle("iconcrack_"+TM3ModRegistry.mobileBase, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height)+0.2D, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.rand.nextDouble()*0.2D-0.1D, this.rand.nextDouble()*0.2D-0.1D, this.rand.nextDouble()*0.2D-0.1D);
//            	this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0D, 0D, 0D);
            }
        }
    }

	@Override
	public void moveEntity(double var1, double var3, double var5) {
		float var7 = this.width / 2.0F;
		float var71 = 0.99F / 2.0F;

		float var8 = (this.isRidden() ? 1.4F : 0.4F);
		this.boundingBox.setBounds(this.posX - var71,
				this.posY,
				this.posZ - var71,
				this.posX + var71,
				this.posY + var8,
				this.posZ + var71);
		super.moveEntity(var1, var3, var5);
		var8 = this.height;
		this.boundingBox.setBounds(this.posX - var7,
				this.posY,
				this.posZ - var7,
				this.posX + var7,
				this.posY + var8,
				this.posZ + var7);
	}
}
