/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP, SilverChiren and CliffracerX
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.projectile;

import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityProjectileBullet
        extends EntityTurretProjectile
{
    public EntityProjectileBullet(World par1World) {
        super(par1World);
        this.setKnockbackStrength(0.9F);
    }

    public EntityProjectileBullet(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setKnockbackStrength(0.9F);
    }

    @Override
    public String getHitSound() {
        return TurretMod.MOD_ID + ":ricochet.bullet";
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
        return new ItemStack(Items.arrow, 1); //TODO: readd projectile item
//        return new ItemStack(TM3ModRegistry.ammoItems, 1, 3);
    }
}
