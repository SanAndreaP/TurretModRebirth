/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTurretCrossbow
        extends EntityTurret
{
    public EntityTurretCrossbow(World world) {
        super(world);

        this.targetProc = new MyTargetProc(this);
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Textures.TURRET_T1_CROSSBOW.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Textures.TURRET_T1_CROSSBOW_GLOW.getResource();
    }

    private class MyTargetProc
            extends TargetProcessor
    {
        public MyTargetProc(EntityTurret turret) {
            super(turret);
        }

        @Override
        public boolean isAmmoApplicable(ItemStack stack) {
            return stack != null && stack.getItem() == Items.arrow;
        }

        @Override
        public int getMaxAmmoCapacity() {
            return 256;
        }

        @Override
        public EntityTurretProjectile getProjectile() {
            EntityTurret turret = this.getTurret();
            return new EntityTurretProjectile(turret.worldObj, turret, turret.targetProc.getTarget());
        }

        @Override
        public int getMaxShootTicks() {
            return 20;
        }

        @Override
        public double getRange() {
            return 16;
        }
    }
}
