package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class EntityTurret_TSForcefield extends EntityTurret_Base {

	public EntityTurret_TSForcefield(World par1World) {
		super(par1World);
	    this.wdtRange = 8.5F;
	    this.hgtRangeU = 8.5F;
	    this.hgtRangeD = 1.5F;
		this.ignoreFrustumCheck = true;
		this.renderDistanceWeight = 128D;
        setTextures("tsForcefield");
	}

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(30, (short) 0); // Shield Points
        this.dataWatcher.updateObject(27, (byte)(this.dataWatcher.getWatchableObjectByte(27) | 4));
    }

	@Override
	protected String getLivingSound() {
		return this.isActive() ? "turretmod3:idle.turretexp" : null;
	}

	@Override
	public void addExperience(int par1Xp) {
	}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
    }

	@Override
	public int getMaxAmmo() {
		return 0;
	}

	@Override
	public int getExpCap() {
		return 0;
	}

	@Override
	protected void checkForEnemies() {
	}

	@Override
	public TurretProjectile getProjectile() {
		return null;
	}

	@Override
	public void shootProjectile(boolean isRidden) {
	}

	@Override
	public void tryToShoot(boolean isRidden) {
	}

	@Override
	public boolean canCollectXP() {
		return false;
	}

	@Override
	public int getMaxShootTicks() {
		return 60;
	}

	public boolean isShieldOnline() {
		return (this.dataWatcher.getWatchableObjectByte(27) & 4) == 4;
	}

	public void toggleShield(boolean flag) {
		byte currStates = this.dataWatcher.getWatchableObjectByte(27);
		this.dataWatcher.updateObject(27, (byte)(flag ? currStates | 4 : currStates & ~4));
	}

	public boolean hasHighRngI() {
		return TurretUpgrades.hasUpgrade(TUpgShieldRngI.class, this.upgrades);
	}

	public boolean hasHighRngII() {
		return TurretUpgrades.hasUpgrade(TUpgShieldRngII.class, this.upgrades);
	}

	public boolean canPushTargets() {
		return true;
	}

	public int getShieldPts() {
		return this.dataWatcher.getWatchableObjectShort(30);
	}

	public void decrShieldPts(int amount) {
		if (amount > 0)
			this.dataWatcher.updateObject(30, (short)Math.max((this.getShieldPts() - amount), 0));
	}

	public void incrShieldPts(int amount) {
		if (amount > 0)
			this.dataWatcher.updateObject(30, (short)Math.min((this.getShieldPts() + amount), this.getMaxShieldPts()));
	}

	public int getMaxShieldPts() {
		return TurretUpgrades.hasUpgrade(TUpgShieldPointsIncr.class, this.upgrades) ? 5000 : 500;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.hasHighRngI() && this.wdtRange < 16D) {
			this.wdtRange = this.hgtRangeU = 16.5D;
		} else if (this.hasHighRngII() && this.wdtRange < 32D) {
			this.wdtRange = this.hgtRangeU = 32.5D;
		}

		if (!this.worldObj.isRemote && this.isActive() ) {
			boolean isDamaging = false;

			if (this.isShieldOnline()) {
				if (this.ticksExisted % 10 == 0)
					TM3ModRegistry.proxy.spawnParticle(11, this.posX-0.5D, this.posY+1.0D, this.posZ-0.5D, 128, this.dimension, this);

				AxisAlignedBB aabb =  AxisAlignedBB.getBoundingBox(
						this.posX - this.wdtRange,
						this.posY - this.wdtRange,
						this.posZ - this.wdtRange,
						this.posX + this.wdtRange,
						this.posY + this.wdtRange,
						this.posZ + this.wdtRange
					);

				if (TurretUpgrades.hasUpgrade(TUpgShieldMobPush.class, this.upgrades)) {
					List<EntityLiving> entities = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, aabb);
					for (EntityLiving entity : entities) {
						if (this.isTargetValid(entity, this.wdtRange, this.hgtRangeU, this.hgtRangeD, true) && this.getDistanceToEntity(entity) <= this.wdtRange) {
							double motion = (2.0D - (this.getDistanceSqToEntity(entity) / Math.pow(this.wdtRange, 2.0D))) * 0.4D;
							double motX = (entity.posX - this.posX);
							double motZ = (entity.posZ - this.posZ);
							motX /= Math.max(Math.abs(motX), Math.abs(motZ));
							motZ /= Math.max(Math.abs(motX), Math.abs(motZ));
							motX *= motion;
							motZ *= motion;
							if (Double.isNaN(motX) || Double.isInfinite(motX)) motX = 0.0D;
							if (Double.isNaN(motZ) || Double.isInfinite(motZ)) motZ = 0.0D;

							entity.motionX = motX;
							entity.motionZ = motZ;
							entity.isAirBorne = true;

							this.decrShieldPts(4);
							isDamaging = true;
						}
					}
				}

				List<IProjectile> projectiles = this.worldObj.getEntitiesWithinAABB(IProjectile.class, aabb);
				for (IProjectile proj : projectiles) {
					if (proj instanceof EntityArrow) {
						EntityArrow arrow = (EntityArrow)proj;
						if (arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityLiving && this.isTargetValidINF((EntityLiving)arrow.shootingEntity) && this.getDistanceToEntity(arrow) <= this.wdtRange) {
							arrow.setDead();
							this.decrShieldPts(10);
							isDamaging = true;
						}
					} else if (proj instanceof EntityThrowable) {
						EntityThrowable throwed = (EntityThrowable)proj;
						if (throwed.getThrower() != null && this.isTargetValidINF(throwed.getThrower()) && this.getDistanceToEntity(throwed) <= this.wdtRange) {
							if (!this.onThrowableImpact(throwed))
								throwed.setDead();
							this.decrShieldPts(3);
							isDamaging = true;
						}
					}
				}

				List<EntityFireball> fireballs = this.worldObj.getEntitiesWithinAABB(EntityFireball.class, aabb);
				for (EntityFireball proj : fireballs) {
					if (proj.shootingEntity != null && this.isTargetValidINF(proj.shootingEntity) && this.getDistanceToEntity(proj) <= this.wdtRange) {
						if (!this.onFireballImpact(proj))
							proj.setDead();
						this.decrShieldPts(6);
						isDamaging = true;
					}
				}
			}

			if (!isDamaging)
				this.incrShieldPts(TurretUpgrades.hasUpgrade(TUpgShieldRepairIncr.class, this.upgrades) ? 10 : 2);

			if (this.getShieldPts() == 0 && this.isShieldOnline())
				this.toggleShield(false);
			if (this.getShieldPts() >= this.getMaxShieldPts()/2 && !this.isShieldOnline()) {
				this.toggleShield(true);
				this.playSound("turretmod3:misc.shieldActive", 1.0F, 1.0F);
				TM3ModRegistry.proxy.spawnParticle(12, this.posX-0.5D, this.posY+1.0D, this.posZ-0.5D, 128, this.dimension, this);
			}
		}
	}

	private boolean isTargetValidINF(EntityLivingBase entity) {
		return this.isTargetValid(entity, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true);
	}

	private MovingObjectPosition getMOPTileFromEntity(Entity e) {
		int bX = MathHelper.floor_double(e.posX);
		int bY = MathHelper.floor_double(e.posY);
		int bZ = MathHelper.floor_double(e.posZ);
		int side = 0;
		Vec3 vec = Vec3.createVectorHelper(e.posX, e.posY, e.posZ);

		return new MovingObjectPosition(bX, bY, bZ, side, vec);
	}

	private boolean onThrowableImpact(EntityThrowable throwable) {
		try {
			Method mtd = ReflectionHelper.findMethod(EntityThrowable.class, throwable, new String[] {"onImpact", "func_70184_a"}, MovingObjectPosition.class);
			mtd.invoke(throwable, getMOPTileFromEntity(throwable));
			return true;
		} catch(UnableToFindMethodException e) {
			FMLLog.warning("Method %s in Class %s not found! Most likely a naming error or wrong class!", "onImpact", throwable.getClass());
			return false;
		} catch (IllegalAccessException e) {
			FMLLog.warning("Access denied on Method %s in Class %s!", "onImpact", "EntityThrowable");
			return false;
		} catch (IllegalArgumentException e) {
			FMLLog.warning("Insufficient Arguments for Method %s in Class %s!", "onImpact", "EntityThrowable");
			return false;
		} catch (InvocationTargetException e) {
			FMLLog.warning("Invocation of Method %s in Class %s failed!", "onImpact", "EntityThrowable");
			return false;
		}
	}

	private boolean onFireballImpact(EntityFireball throwable) {
		try {
			Method mtd = ReflectionHelper.findMethod(EntityFireball.class, throwable, new String[] {"onImpact", "func_70184_a"}, MovingObjectPosition.class);
			mtd.invoke(throwable, getMOPTileFromEntity(throwable));
			return true;
		} catch(UnableToFindMethodException e) {
			FMLLog.warning("Method %s in Class %s not found! Most likely a naming error or wrong class!", "onImpact", throwable.getClass());
			return false;
		} catch (IllegalAccessException e) {
			FMLLog.warning("Access denied on Method %s in Class %s!", "onImpact", "EntityThrowable");
			return false;
		} catch (IllegalArgumentException e) {
			FMLLog.warning("Insufficient Arguments for Method %s in Class %s!", "onImpact", "EntityThrowable");
			return false;
		} catch (InvocationTargetException e) {
			FMLLog.warning("Invocation of Method %s in Class %s failed!", "onImpact", "EntityThrowable");
			return false;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeEntityToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setInteger("shieldPts", this.getShieldPts());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readEntityFromNBT(par1nbtTagCompound);
		if (par1nbtTagCompound.hasKey("shieldPts"))
			this.dataWatcher.updateObject(30, (short)par1nbtTagCompound.getInteger("shieldPts"));
	}
}
