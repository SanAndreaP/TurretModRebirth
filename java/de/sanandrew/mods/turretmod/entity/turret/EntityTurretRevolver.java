/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
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

public class EntityTurretRevolver
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_revolver");
    public static final UUID TI_UUID = UUID.fromString("4449D836-F122-409A-8E6C-D7B7438FD08C");
    public static final TurretInfo TINFO = new TurretInfo()
    {
        @Override
        public String getName() {
            return "turret_i_shotgun";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretRevolver.TI_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretRevolver.class;
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

    public float barrelPosLeft = 1.0F;
    public float prevBarrelPosLeft = 1.0F;
    public float barrelPosRight = 1.0F;
    public float prevBarrelPosRight = 1.0F;
    public boolean leftShot;
    public int prevAmmoVal;

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretRevolver(World world) {
        super(world);
    }

    public EntityTurretRevolver(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(15.0D);
    }

    @Override
    public void onUpdate() {
        this.prevBarrelPosLeft = this.barrelPosLeft;
        this.prevBarrelPosRight = this.barrelPosRight;

        super.onUpdate();

        int currAmmoVal = this.targetProc.getAmmoCount();

        if( this.worldObj.isRemote ) {
            if( this.barrelPosLeft < 1.0F ) {
                this.barrelPosLeft += 0.06F * 20.0F / this.targetProc.getMaxShootTicks();
            } else {
                this.barrelPosLeft = 1.0F;
            }
            if( this.barrelPosRight < 1.0F ) {
                this.barrelPosRight += 0.06F * 20.0F / this.targetProc.getMaxShootTicks();
            } else {
                this.barrelPosRight = 1.0F;
            }

            if( this.prevAmmoVal > currAmmoVal ) {
                float partShift;
                if( this.leftShot ) {
                    this.barrelPosRight = 0.0F;
                    this.leftShot = false;
                    partShift = 10.0F;
                } else {
                    this.barrelPosLeft = 0.0F;
                    this.leftShot = true;
                    partShift = -10.0F;
                }

                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, this.posX, this.posY + 1.5F, this.posZ, Triplet.with(this.rotationYawHead + partShift, this.rotationPitch, this.isUpsideDown));
            }
        }
        this.prevAmmoVal = currAmmoVal;
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T2_REVOLVER.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T2_REVOLVER_GLOW.getResource();
    }

    private class MyTargetProc
            extends TargetProcessor
    {
        public MyTargetProc() {
            super(EntityTurretRevolver.this);
        }

        @Override
        public double getRange() {
            return 16;
        }

        @Override
        public SoundEvent getShootSound() {
            return Sounds.SHOOT_REVOLVER;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }
    }
}
