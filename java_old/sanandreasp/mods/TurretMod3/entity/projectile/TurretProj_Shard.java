package sanandreasp.mods.TurretMod3.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class TurretProj_Shard extends TurretProjectile {

	public TurretProj_Shard(World par1World) {
		super(par1World);
		this.knockbackStrength = 0.8F;
		this.dataWatcher.addObject(19, (int)0); // hit entities
	}

	public TurretProj_Shard(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	private int getHitEntities() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	private void setHitEntities(int cnt) {
		this.dataWatcher.updateObject(19, cnt);
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
		return 2.0F;
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
		return 15D;
	}

	@Override
	protected boolean shouldTargetOneType() {
		return false;
	}

	@Override
	public boolean dieOnImpact() {
		return this.getHitEntities() >= 10;
	}

	@Override
	public void onEntityHit(EntityLivingBase living) {
		super.onEntityHit(living);
		this.setHitEntities(this.getHitEntities()+1);
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
		TM3ModRegistry.proxy.spawnParticle(8, this.posX, this.posY, this.posZ, 128, this.worldObj.provider.dimensionId, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		return 0x0000F0;
	}
}
