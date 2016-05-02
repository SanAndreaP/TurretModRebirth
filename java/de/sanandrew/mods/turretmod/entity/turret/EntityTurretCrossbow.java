/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.util.Textures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTurretCrossbow
        extends EntityTurret
{
    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretCrossbow(World world) {
        super(world);
    }

    public EntityTurretCrossbow(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
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
        public MyTargetProc() {
            super(EntityTurretCrossbow.this);
        }

        @Override
        public int getMaxAmmoCapacity() {
            return 256;
        }

        @Override
        public EntityTurretProjectile getProjectile() {
            return new EntityProjectileCrossbowBolt(EntityTurretCrossbow.this.worldObj, EntityTurretCrossbow.this, EntityTurretCrossbow.this.targetProc.getTarget());
        }

        @Override
        public int getMaxShootTicks() {
            return 20;
        }

        @Override
        public double getRange() {
            return 16;
        }

        @Override
        public String getShootSound() {
            return "random.bow";
        }

        @Override
        public String getLowAmmoSound() {
            return "random.click";
        }
    }
}
