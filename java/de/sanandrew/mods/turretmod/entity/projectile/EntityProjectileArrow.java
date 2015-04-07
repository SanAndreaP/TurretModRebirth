package de.sanandrew.mods.turretmod.entity.projectile;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityProjectileArrow
		extends EntityTurretProjectile
{
	public EntityProjectileArrow(World par1World) {
		super(par1World);
	}

	public EntityProjectileArrow(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	@Override
	public ItemStack getPickupItem() {
		return new ItemStack(Items.arrow, 1);
	}
}
