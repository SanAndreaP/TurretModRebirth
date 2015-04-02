package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProj_Rocket;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;

public class EntityTurret_T4FLAK extends EntityTurret_Base {

	private int shoots = 0;

	public EntityTurret_T4FLAK(World par1World) {
		super(par1World);
		this.wdtRange = 50.5D;
		this.hgtRangeD = 0.0D;
		this.hgtRangeU = 50.5D;
        setTextures("t4FLAK");
	}

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(19, this.shoots);
    }

	private int getShoots() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	@Override
	public boolean isTargetValid(EntityLivingBase entity, double wdtRng, double hgtURng, double hgtDRng, boolean seeThrough) {
		return super.isTargetValid(entity, wdtRng, hgtURng, hgtDRng, seeThrough) && !entity.onGround;
	}

	@Override
	public TurretProjectile getProjectile() {
		TurretProj_Rocket proj = new TurretProj_Rocket(this.worldObj);
		proj.ammoType = this.getAmmoType();
		return proj;
	}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80.0D);
    }

	@Override
	public int getMaxAmmo() {
		return 256;
	}

	@Override
	public void shootProjectile(boolean isRidden) {
		super.shootProjectile(isRidden);
	}

	@Override
	public int getMaxShootTicks() {
		int shootTicks = 10;
		if (!this.worldObj.isRemote) {
			++this.shoots;
			this.dataWatcher.updateObject(19, this.shoots);
		}
		if (this.getShoots() >= 4) {
			shootTicks = 50;
			if (!this.worldObj.isRemote)
				this.shoots = 0;
		}
		return shootTicks;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.wdtRange = this.hgtRangeU = 50.5D + (double)(this.getAmmoType() % 3) * 25.0D;
	}

	@Override
	public String getShootSound() {
		return "turretmod3:shoot.flak";
	}

	@Override
	public void onKillEntity(EntityLivingBase par1EntityLiving) {
		super.onKillEntity(par1EntityLiving);
	}

	@Override
	public boolean hasFireImmunity() {
		return true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeEntityToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setInteger("shoots", this.shoots);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readEntityFromNBT(par1nbtTagCompound);
		this.shoots = par1nbtTagCompound.getInteger("shoots");
	}
}
