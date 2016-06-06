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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectilePebble;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.darkhax.bookshelf.lib.javatuples.Triplet;
import net.darkhax.bookshelf.lib.javatuples.Unit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretShotgun
        extends EntityTurret
{
    public static final UUID TI_UUID = UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5");
    public static final TurretInfo TINFO = new TurretInfo()
    {
        @Override
        public String getName() {
            return "turret_i_shotgun";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretShotgun.TI_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretShotgun.class;
        }

        @Override
        public float getTurretHealth() {
            return 20.0F;
        }

        @Override
        public int getBaseAmmoCapacity() {
            return 256;
        }

        @Override
        public String getIcon() {
            return "turret_shotgun";
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.TURRET_MK1_SG;
        }

        @Override
        public String getInfoRange() {
            return "16";
        }
    };

    public float barrelPos = 1.0F;
    public float prevBarrelPos = 1.0F;
    public int prevAmmoVal;

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretShotgun(World world) {
        super(world);
    }

    public EntityTurretShotgun(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(20.0D);
    }

    @Override
    public void onUpdate() {
        this.prevBarrelPos = this.barrelPos;

        super.onUpdate();

        int currAmmoVal = this.targetProc.getAmmoCount();

        if( this.worldObj.isRemote ) {
            if( this.barrelPos < 1.0F ) {
                this.barrelPos += 0.06F * 20.0F / this.targetProc.getMaxShootTicks();
            } else {
                this.barrelPos = 1.0F;
            }

            if( this.prevAmmoVal > currAmmoVal ) {
                this.barrelPos = 0.0F;
                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, this.posX, this.posY + 1.5F, this.posZ, Triplet.with(this.rotationYawHead, this.rotationPitch, this.isUpsideDown));
            }
        }
        this.prevAmmoVal = currAmmoVal;
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T1_SHOTGUN.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T1_SHOTGUN_GLOW.getResource();
    }

    private class MyTargetProc
            extends TargetProcessor
    {
        public MyTargetProc() {
            super(EntityTurretShotgun.this);
        }

        @Override
        public EntityTurretProjectile getProjectile() {
            return new EntityProjectilePebble(EntityTurretShotgun.this.worldObj, EntityTurretShotgun.this, EntityTurretShotgun.this.targetProc.getTarget());
        }

        @Override
        public double getRange() {
            return 16;
        }

        @Override
        public String getShootSound() {
            return TurretModRebirth.ID + ":shoot.shotgun";
        }

        @Override
        public String getLowAmmoSound() {
            return "random.click";
        }

        public void shootProjectile() {
            if( this.hasAmmo() ) {
                for( int i = 0; i < 6; i++ ) {
                    EntityTurretProjectile projectile = this.getProjectile();
                    this.turret.worldObj.spawnEntityInWorld(projectile);
                    this.turret.worldObj.playSoundAtEntity(this.turret, this.getShootSound(), 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
                }
                this.ammoCount--;
            } else {
                this.turret.worldObj.playSoundAtEntity(this.turret, this.getLowAmmoSound(), 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
        }
    }
}
