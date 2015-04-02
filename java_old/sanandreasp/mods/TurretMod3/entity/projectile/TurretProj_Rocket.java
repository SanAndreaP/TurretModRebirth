package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretProj_Rocket extends TurretProjectile {

	public int ammoType = 0;

	public TurretProj_Rocket(World par1World) {
		super(par1World);
		this.knockbackStrength = 0.8F;
	}

	public TurretProj_Rocket(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public String getHitSound() {
		return "random.explode";
	}

	@Override
	public float getGravityVal() {
		return 0.001F;
	}

	@Override
	public float getSpeedVal() {
		return 3F;
	}

	@Override
	public boolean isArrow() {
		return false;
	}

	@Override
	public float getCurveCorrector() {
		return 0.01F;
	}

	@Override
	public double getDamage() {
		return 2D;
	}

	@Override
	protected boolean shouldTargetOneType() {
		return true;
	}

	@Override
	public boolean dieOnGround() {
		return false;
	}

	@Override
	public void onUpdate() {
		this.isEndermanDamageable = true;

		super.onUpdate();

		if (this.ammoType == 3 || this.ammoType == 4 || this.ammoType == 5) {
			if (this.targetedEntity != null) {
				if (this.targetedEntity.isDead) {
					this.setDead();
				}
				double d = (this.targetedEntity.boundingBox.minX + (this.targetedEntity.boundingBox.maxX - this.targetedEntity.boundingBox.minX) / 2D)
						- this.posX;
				double d1 = (this.targetedEntity.boundingBox.minY + (this.targetedEntity.boundingBox.maxY - this.targetedEntity.boundingBox.minY) / 2D)
						- this.posY;
				double d2 = (this.targetedEntity.boundingBox.minZ + (this.targetedEntity.boundingBox.maxZ - this.targetedEntity.boundingBox.minZ) / 2D)
						- this.posZ;
				this.setHeading(d, d1, d2, 1.5F, 0.0F);
			}
		}

		if (this.worldObj.isRemote)
			TM3ModRegistry.proxy.spawnParticle(5, this.posX, this.posY, this.posZ, 128, this.worldObj.provider.dimensionId, this);
	}

	@Override
	protected void processFailedHit(Entity hit) {
		this.setDead();
	}

	@Override
	public void setDead() {
		super.setDead();
		TM3ModRegistry.proxy.spawnParticle(6, this.posX, this.posY, this.posZ, 64, this.worldObj.provider.dimensionId, this);
	}
}
