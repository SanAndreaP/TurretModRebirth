package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class TurretProj_Laser extends TurretProjectile {

	public TurretProj_Laser(World par1World) {
		super(par1World);
		this.knockbackStrength = 0.2F;
	}

	public TurretProj_Laser(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public String getHitSound() {
		return "random.fizz";
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
	public void onEntityHit(EntityLivingBase living) {
		super.onEntityHit(living);
		living.setFire(5);
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
		return 6D;
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
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
        return 0x0000F0;
//        short var4 = 240;
//        int var5 = var3 >> 16 & 255;
//        return var4 | var5 << 16;
	}

//	@Override
//	public ItemStack getPickupItem() {
//		return new ItemStack(TM3ModRegistry.bullets, 1);
//	}
}
