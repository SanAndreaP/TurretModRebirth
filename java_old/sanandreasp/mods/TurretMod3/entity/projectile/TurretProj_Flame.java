package sanandreasp.mods.TurretMod3.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TurretProj_Flame extends TurretProjectile {

	public TurretProj_Flame(World par1World) {
		super(par1World);
		this.dataWatcher.addObject(20, (byte)0);
		this.knockbackStrength = 0F;
	}

	public TurretProj_Flame(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public String getHitSound() {
		return this.rand.nextInt(10) == 0 ? "random.fizz" : "";
	}

	@Override
	public float getGravityVal() {
		return 0.001F;
	}

	@Override
	public float getSpeedVal() {
		return 0.5F;
	}

	@Override
	public boolean isArrow() {
		return false;
	}

	@Override
	public float getCurveCorrector() {
		return 0.07F;
	}

	public boolean isPurified() {
		return this.dataWatcher.getWatchableObjectByte(20) == (byte)1;
	}

	public void setPurified() {
		this.dataWatcher.updateObject(20, (byte)1);
	}

	@Override
	public double getDamage() {
		return this.isPurified() ? 4D : 3D;
	}

	@Override
	protected boolean shouldTargetOneType() {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		int blockX = MathHelper.floor_double(this.posX);
		int blockY = MathHelper.floor_double(this.posY)-2;
		int blockZ = MathHelper.floor_double(this.posZ);

		Block baseBlock = this.worldObj.getBlock(blockX, blockY, blockZ);
		Block aboveBlock = this.worldObj.getBlock(blockX, blockY + 1, blockZ);

		if (!this.worldObj.isRemote
				&& this.isPurified()
				&& baseBlock != null
				&& aboveBlock == null
				&& this.rand.nextInt(2500) == 0) {
			this.worldObj.setBlock(blockX, blockY+1, blockZ, Blocks.fire);
		}
	}

	@Override
	public boolean flyThroughOnEntityHit() {
		return this.isPurified();
	}

	@Override
	public void onEntityHit(EntityLivingBase living) {
		super.onEntityHit(living);
		living.setFire(this.isPurified() ? 20 : 5);
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
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeEntityToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setBoolean("purifying", this.isPurified());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readEntityFromNBT(par1nbtTagCompound);
		if (par1nbtTagCompound.getBoolean("purifying"))
			this.setPurified();
	}
}
