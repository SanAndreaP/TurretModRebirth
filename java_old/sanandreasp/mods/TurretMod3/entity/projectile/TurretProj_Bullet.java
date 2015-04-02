package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretProj_Bullet extends TurretProjectile {

	public TurretProj_Bullet(World par1World) {
		super(par1World);
		this.setKnockbackStrength(0.9F);
	}

	public TurretProj_Bullet(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		this.setKnockbackStrength(0.9F);
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
		return 4D;
	}

	@Override
	protected boolean shouldTargetOneType() {
		return true;
	}

	@Override
	public ItemStack getPickupItem() {
		return new ItemStack(TM3ModRegistry.ammoItems, 1, 3);
	}
}
