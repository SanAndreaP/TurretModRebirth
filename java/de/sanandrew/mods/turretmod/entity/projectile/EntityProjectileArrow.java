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

	@Override
	public ItemStack getPickupItem() {
		return new ItemStack(Items.arrow, 1);
	}
}
