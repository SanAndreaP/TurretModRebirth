/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util.ammo;

import de.sanandrew.mods.turretmod.api.Turret;
import de.sanandrew.mods.turretmod.api.TurretAmmo;
import de.sanandrew.mods.turretmod.api.TurretProjectile;
import de.sanandrew.mods.turretmod.registry.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.entity.turret.techii.EntityTurretRevolver;
import de.sanandrew.mods.turretmod.item.ItemTurretAmmo;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class AmmoBullet
        implements TurretAmmo
{
    private ItemStack ammoItm;

    @Override
    public String getName() {
        return "t2BulletStd";
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public ItemStack getTypeItem() {
        return ammoItm;
    }

    @Override
    public ItemStack getAmmoItem() {
        return ammoItm;
    }

    @Override
    public boolean isApplicablToTurret(Turret turret) {
        return turret instanceof EntityTurretRevolver;
    }

    @Override
    public TurretProjectile<? extends EntityArrow> getProjectile(World world, Turret turret) {
        return new EntityProjectileBullet(world);
    }

    public TurretAmmo initializeItem() {
        this.ammoItm = ItemTurretAmmo.getItemFromType(this, 1);
        return this;
    }
}
