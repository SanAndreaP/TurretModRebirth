package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_T2Minigun;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;

public class TurretProj_Seed extends TurretProjectile {

	public boolean isPiercing = false;

	public TurretProj_Seed(World par1World) {
		super(par1World);
		this.knockbackStrength = 0.5F;
	}

	public TurretProj_Seed(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		this.knockbackStrength = 0.5F;
	}

	@Override
	public String getHitSound() {
		return "turretmod3:ricochet.bullet";
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
		return 0.03F;
	}

	@Override
	public double getDamage() {
		return 1D;
	}

	@Override
	protected boolean shouldTargetOneType() {
		return false;
	}

	@Override
	public boolean flyThroughOnEntityHit() {
		return isPiercing;
	}

	@Override
	public ItemStack getPickupItem() {
		ItemStack is = TurretInfo.getTurretInfo(EntityTurret_T2Minigun.class).getAmmoTypeItemWithLowestScore(this.ammoType).copy();
		is.setItemDamage(Math.max(0, is.getItemDamage()));
		is.stackSize = 1;
		return is;
	}
}
