/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import de.sanandrew.mods.turretmod.util.javatuples.Triplet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretShotgun
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_shotgun");
    public static final UUID TI_UUID = UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5");
    public static final TurretInfo TINFO = new TurretInfo() {
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
        public ResourceLocation getModel() {
            return ITEM_MODEL;
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

        if( this.worldObj.isRemote ) {
            if( this.barrelPos < 1.0F ) {
                this.barrelPos += 0.06F * 20.0F / this.targetProc.getMaxShootTicks();
            } else {
                this.barrelPos = 1.0F;
            }

            if( this.wasShooting() ) {
                this.barrelPos = 0.0F;
                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, this.posX, this.posY + 1.5F, this.posZ, Triplet.with(this.rotationYawHead, this.rotationPitch, this.isUpsideDown));
            }
        }
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
        public double getRange() {
            return 16;
        }

        @Override
        public SoundEvent getShootSound() {
            return Sounds.SHOOT_SHOTGUN;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }

        public void shootProjectile() {
            if( this.hasAmmo() ) {
                for( int i = 0; i < 6; i++ ) {
                    Entity projectile = (Entity) this.getProjectile();
                    assert projectile != null;
                    this.turret.worldObj.spawnEntityInWorld(projectile);
                    this.turret.worldObj.playSound(null, this.turret.posX, this.turret.posY, this.turret.posZ, this.getShootSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
                }
                this.turret.setShooting();
                this.decrAmmo();
            } else {
                this.turret.worldObj.playSound(null, this.turret.posX, this.turret.posY, this.turret.posZ, this.getLowAmmoSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F / (this.turret.getRNG().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
        }
    }
}
