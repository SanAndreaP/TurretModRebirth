package de.sanandrew.mods.turretmod.api;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface TurretAmmo
{
    String getName();

    int getAmount();

    ItemStack getTypeItem();

    ItemStack getAmmoItem();

    Turret getApplicableTurret();

    TurretProjectile<? extends EntityArrow> getProjectile(World world);

    void onShooting(Turret turret);
}
