package sanandreasp.mods.TurretMod3.entity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProj_Snowball;
import sanandreasp.mods.turretmod3.entity.projectile.TurretProjectile;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgSlowdownII;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TUpgStopMove;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class EntityTurret_TSSnowball extends EntityTurret_Base {

	public EntityTurret_TSSnowball(World par1World) {
		super(par1World);
        setTextures("tsSnowball");
	}

	@Override
	public TurretProjectile getProjectile() {
		TurretProj_Snowball proj = new TurretProj_Snowball(this.worldObj);
		proj.amplifier = this.getSlowdownAmplifier();
		return proj;
	}

	@Override
	public int getMaxShootTicks() {
		return 20;
	}

	private int getSlowdownAmplifier() {
		int amplifier = 0;
		if (TurretUpgrades.hasUpgrade(TUpgSlowdownII.class, this.upgrades))
			amplifier = 2;
		if (TurretUpgrades.hasUpgrade(TUpgStopMove.class, this.upgrades))
			amplifier = 6;

		return amplifier;
	}

	@Override
	public boolean isTargetValid(EntityLivingBase entity) {
		PotionEffect effect = entity.getActivePotionEffect(Potion.moveSlowdown);
		return super.isTargetValid(entity) && !(effect != null && effect.getDuration() > 20 && effect.getAmplifier() >= this.getSlowdownAmplifier());
	}
}
