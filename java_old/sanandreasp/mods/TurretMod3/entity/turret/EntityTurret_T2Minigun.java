package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProj_Seed;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;
import sanandreasp.mods.turretmod3.registry.AchievementPageTM;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgPiercing;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class EntityTurret_T2Minigun extends EntityTurret_Base {

	public double barrelRot = 0D;
	private boolean canRot = false;
	private boolean isRight = false;
	private int killsWithin10sec = 0;

	public EntityTurret_T2Minigun(World par1World) {
		super(par1World);
		this.wdtRange = 24.5F;
        setTextures("t2Minigun");
	}

	@Override
	public TurretProjectile getProjectile() {
		TurretProj_Seed proj = new TurretProj_Seed(this.worldObj);
		proj.isPiercing = TurretUpgrades.hasUpgrade(TUpgPiercing.class, this.upgrades);
		return proj;
	}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
    }

	@Override
	public int getMaxAmmo() {
		return 256;
	}

	@Override
	public void shootProjectile(boolean isRidden) {
		super.shootProjectile(isRidden);
		double rotYawX = Math.sin((this.rotationYawHead / 180) * Math.PI);
		double rotYawZ = Math.cos((this.rotationYawHead / 180) * Math.PI);
		double partX = this.posX - rotYawX * (Math.cos(this.rotationPitch / (180F / (float)Math.PI))) * 0.5D;
		double partY = this.posY + this.getEyeHeight() - Math.sin(this.rotationPitch / (180F / (float)Math.PI)) * 0.5D;
		double partZ = this.posZ + rotYawZ * (Math.cos(this.rotationPitch / (180F / (float)Math.PI))) * 0.5D;

		TM3ModRegistry.proxy.spawnParticle(0, partX - (this.isRight ? 0.5D : -0.5D)*rotYawZ, partY+0.1D, partZ - (this.isRight ? 0.5D : -0.5D)*rotYawX, 64, this.worldObj.provider.dimensionId, this);
	}

	@Override
	public int getMaxShootTicks() {
		return 2;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.getShootTicks() == 1) {
			barrelRot += 45D;
			if (MathHelper.floor_double(barrelRot) == 180)
				barrelRot = 0F;

			isRight = MathHelper.floor_double(barrelRot) % 90 != 0;
		}
		if (this.ticksExisted % 200 == 0) {
			if (this.killsWithin10sec >= 10){
				EntityPlayer player = this.worldObj.getPlayerEntityByName(this.getPlayerName());
				if (player != null) {
					player.triggerAchievement(AchievementPageTM.piercing);
				}
			}
			this.killsWithin10sec = 0;
		}
	}

	@Override
	public String getShootSound() {
		return "turretmod3:shoot.minigun";
	}

	@Override
	public void onKillEntity(EntityLivingBase par1EntityLiving) {
		super.onKillEntity(par1EntityLiving);
		this.killsWithin10sec++;
	}
}
