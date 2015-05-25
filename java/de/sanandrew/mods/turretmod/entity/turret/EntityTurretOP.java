package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTurretOP
		extends EntityTurretBase
{
	private static final AxisAlignedBB rangeAABB = AxisAlignedBB.getBoundingBox(-64.0F, -64.0F, -64.0F, 64.0F, 64.0F, 64.0F);

	public EntityTurretOP(World par1World) {
		super(par1World);
	}

	@Override
	public AxisAlignedBB getRangeBB() {
		return rangeAABB.getOffsetBoundingBox(this.posX, this.posY, this.posZ);
	}

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000.0D);
		this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(15.0F);
    }

//	@Override
//	public EntityTurretProjectile getProjectile() {
//		return new EntityProjectileBullet(this.worldObj) {
//			@Override public double getDamage() { return 100.0D; }
//
//			@Override
//			public float getCurveCorrector() {
//				return 0.015F;
//			}
//		};
//	}

	@Override
	public void shoot(boolean isRidden) {
		if( !this.worldObj.isRemote && this.getHealth() > 0 && (isRidden || this.hasTarget()) ) {
			this.shootProjectile(isRidden);
		}
	}

	@Override
	public void shootProjectile(boolean isRidden) {
		super.shootProjectile(isRidden);

		double rotYawX = Math.sin((this.rotationYawHead / 180) * Math.PI);
		double rotYawZ = Math.cos((this.rotationYawHead / 180) * Math.PI);
		double partX = this.posX - rotYawX * (Math.cos(this.rotationPitch / (180F / (float)Math.PI))) * 0.7D;
		double partY = this.posY + this.getEyeHeight() - Math.sin(this.rotationPitch / (180F / (float)Math.PI)) * 0.7D;
		double partZ = this.posZ + rotYawZ * (Math.cos(this.rotationPitch / (180F / (float)Math.PI))) * 0.7D;

		//TODO: readd particles
//		TM3ModRegistry.proxy.spawnParticle(0, partX - (this.isRight ? 0.1D : -0.1D)*rotYawZ, partY, partZ - (this.isRight ? 0.1D : -0.1D)*rotYawX, 64, this.worldObj.provider.dimensionId, this);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		EntityPlayer owner = this.worldObj.getPlayerEntityByName(this.getOwnerName());
		if( !this.worldObj.isRemote && owner != null && !owner.capabilities.isCreativeMode ) {
			this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5.0F, true);
			this.setDead();
		}
	}

	@Override
	public String getShootSound() {
		return TurretMod.MOD_ID + ":shoot.pistol";
	}

	@Override
	public ResourceLocation getStandardTexture() {
		return EnumTextures.TURRET_T1_CROSSBOW.getResource();
	}

	@Override
	public ResourceLocation getGlowTexture() {
		return EnumTextures.TURRET_T2_REVOLVER_GLOW.getResource();
	}

	@Override
	protected IEntitySelector getTargetSelector() {
		return new TargetSelector();
	}

	public class TargetSelector implements IEntitySelector
	{
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return EntityTurretOP.this.canEntityBeSeen(entity)
					&& EntityTurretOP.this.getRangeBB().intersectsWith(entity.boundingBox);
		}
	}
}
