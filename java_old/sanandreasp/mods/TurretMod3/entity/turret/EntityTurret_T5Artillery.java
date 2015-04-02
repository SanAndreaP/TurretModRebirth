package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProj_Explosive;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgPrecision;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class EntityTurret_T5Artillery extends EntityTurret_Base {

	public EntityTurret_T5Artillery(World par1World) {
		super(par1World);
		this.wdtRange = 50.5F;
		this.hgtRangeD = 20.5F;
		this.hgtRangeU = 15.5F;
        setTextures("t5Artillery");
	}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D);
    }

	@Override
	public TurretProjectile getProjectile() {
		TurretProj_Explosive proj = new TurretProj_Explosive(this.worldObj);
		int i = this.getAmmoType();
		proj.isPrecise = TurretUpgrades.hasUpgrade(TUpgPrecision.class, this.upgrades);
		proj.explosionRadius = i==1||i==3||i==6||i==8 ? 4F : 2F;
		proj.isNapalm = i==2||i==3||i==7||i==8;
		proj.isGriefing = i>4;
		proj.isFragmentating = i==4||i==9;
		return proj;
	}

	@Override
	public void shootProjectile(boolean isRidden) {
		super.shootProjectile(isRidden);
		double rotYawX = Math.sin((this.rotationYawHead / 180F) * Math.PI);
		double rotYawZ = Math.cos((this.rotationYawHead / 180F) * Math.PI);
		double partX = this.posX - rotYawX * (Math.cos((this.rotationPitch - 90F) / (180F / (float)Math.PI))) * 0.5D;
		double partY = this.posY + this.getEyeHeight() - Math.sin((this.rotationPitch - 90F) / (180F / (float)Math.PI)) * 0.7D;
		double partZ = this.posZ + rotYawZ * (Math.cos((this.rotationPitch - 90F) / (180F / (float)Math.PI))) * 0.5D;

		TM3ModRegistry.proxy.spawnParticle(10, partX, partY, partZ, 64, this.worldObj.provider.dimensionId, this);
	}

	@Override
	public int getMaxShootTicks() {
		return 150;
	}

	@Override
	public boolean hasFireImmunity() {
		return true;
	}

	@Override
	public String getShootSound() {
		return "turretmod3:shoot.artillery";
	}

	@Override
	public float getShootSoundRng() {
		return 2F;
	}
}
