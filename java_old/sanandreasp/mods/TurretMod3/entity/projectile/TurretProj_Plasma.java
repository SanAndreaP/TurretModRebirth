package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretProj_Plasma extends TurretProjectile {

	public TurretProj_Plasma(World par1World) {
		super(par1World);
		this.knockbackStrength = 0.8F;
	}

	public TurretProj_Plasma(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public String getHitSound() {
		return "turretmod3:ricochet.splash";
	}

	@Override
	public float getGravityVal() {
		return 0.001F;
	}

	@Override
	public float getSpeedVal() {
		return 5F;
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
		return 25D;
	}

	@Override
	protected boolean shouldTargetOneType() {
		return true;
	}

	@Override
	public boolean dieOnGround() {
		return true;
	}

	@Override
	public float getBrightness(float par1) {
		return 1F;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		TM3ModRegistry.proxy.spawnParticle(3, this.posX, this.posY, this.posZ, 128, this.worldObj.provider.dimensionId, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		return 0x0000F0;
	}
}
