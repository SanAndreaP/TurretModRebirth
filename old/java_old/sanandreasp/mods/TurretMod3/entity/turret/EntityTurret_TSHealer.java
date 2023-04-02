package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.IHealable;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;

public class EntityTurret_TSHealer extends EntityTurret_Base {

	private int soundTicks = 0;

	public EntityTurret_TSHealer(World par1World) {
		super(par1World);
		this.ignoreFrustumCheck = true;
        setTextures("tsHealer");
	}

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(19, 0);
    }

	@Override
	public TurretProjectile getProjectile() {
		return null;
	}

	@Override
	public void shootProjectile(boolean isRidden) {
		this.currentTarget.heal(1);
		if (this.soundTicks == 0) {
	        this.worldObj.playSoundAtEntity(this, this.getShootSound(), 1.5F, 1.0F / (this.getRNG().nextFloat() * 0.2F + 0.8F));
		}
		this.soundTicks++;
		if (this.soundTicks > 3)
			this.soundTicks = 0;
	}

	@Override
	public String getShootSound() {
		return "turretmod3:shoot.healBeam";
	}

	@Override
	public int getMaxShootTicks() {
		return 5;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.worldObj.isRemote && this.currentTarget != null) {
			this.dataWatcher.updateObject(19, this.currentTarget.getEntityId());
		}

		if (this.currentTarget == null || this.getAmmo() <= 0) {
			this.soundTicks = 0;
		}
	}

	public int getTargetEID() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	@Override
	public boolean isTargetValid(EntityLivingBase entity) {
		float var1 = (float)wdtRange;
        boolean inList = !isEntityTargeted(entity) && entity instanceof IHealable;

        return !(entity.isDead || entity.getHealth() <= 0 || entity.getHealth() >= entity.getMaxHealth()
        		|| entity.getDistanceSqToEntity(this) > (double)(var1 * var1)
        		|| !this.canEntityBeSeen(entity)
        		|| this.posY - entity.posY > this.hgtRangeD
        		|| entity.posY - this.posY > this.hgtRangeU) && inList;
	}
}
