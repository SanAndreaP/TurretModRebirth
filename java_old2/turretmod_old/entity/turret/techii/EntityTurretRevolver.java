package de.sanandrew.mods.turretmod.entity.turret.techii;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.entity.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTurretRevolver
		extends EntityTurretBase
{
	private static final AxisAlignedBB RANGE_AABB = AxisAlignedBB.getBoundingBox(-24.0F, -6.0F, -24.0F, 24.0F, 6.0F, 24.0F);
	private boolean isRight = false;
	private int rightBarrelOffset = 0;
	private int leftBarrelOffset = 0;
	private boolean prevShooting = false;

	public EntityTurretRevolver(World par1World) {
		super(par1World);
	}

	@Override
	public AxisAlignedBB getRangeBB() {
		return RANGE_AABB.getOffsetBoundingBox(this.posX, this.posY, this.posZ);
	}

    @Override
    protected void entityInit() {
        super.entityInit();
    }

	public int getRightBarrelOffset() {
		return this.rightBarrelOffset;
	}

	public int getLeftBarrelOffset() {
		return this.leftBarrelOffset;
	}
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
		this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(15.0F);
    }

	private int prevShootTicks = Integer.MAX_VALUE;

	@Override
	public void shoot(boolean isRidden) {
		if( this.worldObj.isRemote ) {
			if( (this.prevShootTicks < this.getShootTicks() || this.getShootTicks() == 0) && this.getHealth() > 0
				&& (isRidden || this.getTargetHandler().hasTarget(this)) && this.getAmmo() > 0 )
			{
				if( !this.prevShooting ) {
					if( this.isRight ) {
						this.rightBarrelOffset = getMaxShootTicks();
					} else {
						this.leftBarrelOffset = getMaxShootTicks();
					}

					this.isRight = !this.isRight;
				}

				this.prevShooting = true;
			} else {
				this.prevShooting = false;
			}

            this.prevShootTicks = this.getShootTicks();
		}

		super.shoot(isRidden);
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

		if( this.worldObj.isRemote ) {
			if( this.rightBarrelOffset > 0 ) {
				this.rightBarrelOffset -= 1;
			}

			if( this.leftBarrelOffset > 0 ) {
				this.leftBarrelOffset -= 1;
			}
		}
	}

	@Override
	public String getShootSound() {
		return TurretMod.MOD_ID + ":shoot.pistol";
	}

	@Override
	public ResourceLocation getStandardTexture() {
		return Textures.TURRET_T2_REVOLVER.getResource();
	}

	@Override
	public ResourceLocation getGlowTexture() {
		return Textures.TURRET_T2_REVOLVER_GLOW.getResource();
	}

	@Override
	public IEntitySelector getTargetSelector() {
		return new TargetSelector();
	}

    public class TargetSelector implements IEntitySelector
	{
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return EntityTurretRevolver.this.canEntityBeSeen(entity) && EntityTurretRevolver.this.getRangeBB().intersectsWith(entity.boundingBox);
		}
	}
}
