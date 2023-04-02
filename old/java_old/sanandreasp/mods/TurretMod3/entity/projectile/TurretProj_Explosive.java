package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretProj_Explosive extends TurretProjectile {

	public boolean isGriefing = true;
	public boolean isNapalm = false;
	public float explosionRadius = 2F;
	public boolean isPrecise = false;
	public boolean isFragmentating = false;

	public TurretProj_Explosive(World par1World) {
		super(par1World);
	}

	public TurretProj_Explosive(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public double getDamage() {
		return 0D;
	}

	@Override
	public boolean dieOnImpact() {
		return true;
	};

	@Override
	public boolean dieOnGround() {
		return true;
	}

	@Override
	public float getCurveCorrector() {
		float speed = 1.0F;
		return speed;
	}

	@Override
	public float getSpeedVal() {
		float speed = 1.2F;
		if (this.shootingEntity != null && this.targetedEntity != null) {
			speed *= (float)this.shootingEntity.getDistanceToEntity(this.targetedEntity) / 30F;
			speed -= (float)this.shootingEntity.getDistanceSqToEntity(this.targetedEntity) / 3500F;
			speed += ((float)this.targetedEntity.posY - (float)this.shootingEntity.posY) / 70F;
		}
		return speed;
	}

	@Override
	public String getHitSound() {
		return "";
	}

	private boolean isExploding = true;

	@Override
	public void setDead() {
		super.setDead();
		if (!this.worldObj.isRemote && this.isExploding)
        {
            boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.isGriefing;
            if (this.isNapalm) {
            	this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, this.explosionRadius, true, var2);
        	} else {
            	this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, this.explosionRadius, var2);
            }
        }
	}

	@Override
	public void onUpdate() {
		this.isEndermanDamageable = true;

		super.onUpdate();

		if (this.isFragmentating && this.motionY < -0.2F && !this.worldObj.isRemote) {
			for (int i = 0; i < 5; i++) {
				TurretProj_Explosive var2 = new TurretProj_Explosive(this.worldObj);
		        var2.isPickupable = false;
		        var2.hasNoTarget = true;
		        var2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
		        var2.yOffset = 0.0F;
		        var2.setHeading(this.motionX, this.motionY, this.motionZ, 1F * 1.5F, 20.0F);
		        var2.shootingEntity = this.shootingEntity;
		        var2.isNapalm = false;
		        var2.isGriefing = this.isGriefing;
		        var2.isFragmentating = false;
		        this.worldObj.spawnEntityInWorld(var2);
		        var2.isMoving = true;
			}
			this.isExploding = false;
			TM3ModRegistry.proxy.spawnParticle(9, this.posX, this.posY, this.posZ, 128, this.worldObj.provider.dimensionId, null);
			this.setDead();
		}

		if (this.targetedEntity != null && !this.targetedEntity.isDead && this.motionY < -0.5F && this.isPrecise) {
			double d = (this.targetedEntity.boundingBox.minX + (this.targetedEntity.boundingBox.maxX - this.targetedEntity.boundingBox.minX) / 2D)
					- this.posX;
			double d1 = (this.targetedEntity.boundingBox.minY + (this.targetedEntity.boundingBox.maxY - this.targetedEntity.boundingBox.minY) / 2D)
					- this.posY;
			double d2 = (this.targetedEntity.boundingBox.minZ + (this.targetedEntity.boundingBox.maxZ - this.targetedEntity.boundingBox.minZ) / 2D)
					- this.posZ;
			this.setHeading(d, d1, d2, 1.5F, 0.0F);
		}
	}
}
