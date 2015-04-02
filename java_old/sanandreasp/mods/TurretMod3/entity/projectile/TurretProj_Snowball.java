package sanandreasp.mods.TurretMod3.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class TurretProj_Snowball extends TurretProjectile {

	public int amplifier = 0;

	public TurretProj_Snowball(World par1World) {
		super(par1World);
		this.knockbackStrength = 0.0F;
	}

	public TurretProj_Snowball(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public boolean dieOnGround() {
		return true;
	}

	@Override
	public boolean dieOnImpact() {
		return true;
	}

	@Override
	public boolean isArrow() {
		return false;
	}

	@Override
	public void onEntityHit(EntityLivingBase living) {
		super.onEntityHit(living);
		living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, this.amplifier));
	}

	@Override
	public double getDamage() {
		return 0D;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeEntityToNBT(par1nbtTagCompound);
		par1nbtTagCompound.setInteger("amplifier", this.amplifier);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readEntityFromNBT(par1nbtTagCompound);
		this.amplifier = par1nbtTagCompound.getInteger("amplifier");
	}
}
