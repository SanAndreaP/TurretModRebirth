/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretMinigun
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_minigun");
    public static final UUID TII_UUID = UUID.fromString("97E1FB65-EE36-43BA-A900-583B4BD7973A");
    public static final TurretInfo TINFO = new TurretInfo() {
        @Override
        public String getName() {
            return "turret_ii_minigun";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretMinigun.TII_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretMinigun.class;
        }

        @Override
        public float getTurretHealth() {
            return 30.0F;
        }

        @Override
        public int getBaseAmmoCapacity() {
            return 512;
        }

        @Override
        public ResourceLocation getModel() {
            return ITEM_MODEL;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.TURRET_MK2_MG;
        }

        @Override
        public String getInfoRange() {
            return "20";
        }
    };

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-20.0D, -4.0D, -20.0D, 20.0D, 10.0D, 20.0D);

    public float maxBarrelLeft = 0.0F;
    public float barrelLeft = 0.0F;
    public float prevBarrelLeft = 0.0F;
    public float maxBarrelRight = 0.0F;
    public float barrelRight = 0.0F;
    public float prevBarrelRight = 0.0F;
    public boolean leftShot;

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretMinigun(World world) {
        super(world);
    }

    public EntityTurretMinigun(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).setBaseValue(512.0D);
        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    public void onUpdate() {
        this.prevBarrelLeft = this.barrelLeft;
        this.prevBarrelRight = this.barrelRight;

        super.onUpdate();


        if( this.wasShooting() ) {
            if( this.leftShot ) {
                this.maxBarrelRight += 90.0F;
                this.leftShot = false;
            } else {
                this.maxBarrelLeft += 90.0F;
                this.leftShot = true;
            }

            if( this.world.isRemote ) {
                TurretModRebirth.proxy.spawnParticle(EnumParticle.MINIGUN_SHOT, this.posX, this.posY + 1.5F, this.posZ, new Tuple(this.rotationYawHead, this.rotationPitch - 7.5F, this.isUpsideDown, this.leftShot));
            }
        }

        if( this.world.isRemote ) {
            if( this.barrelLeft < this.maxBarrelLeft ) {
                this.barrelLeft += 90.0F / this.targetProc.getMaxShootTicks() * 2.0F;
            } else {
                this.barrelLeft = this.maxBarrelLeft;
            }

            if( this.barrelRight < this.maxBarrelRight ) {
                this.barrelRight += 90.0F / this.targetProc.getMaxShootTicks() * 2.0F;
            } else {
                this.barrelRight = this.maxBarrelRight;
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return (this.hasCustomName() && this.getCustomNameTag().equalsIgnoreCase("silverchiren") ? Resources.TURRET_T2_MINIGUN_EE : Resources.TURRET_T2_MINIGUN).getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T2_MINIGUN_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB() {
        return RANGE_BB;
    }

    private class MyTargetProc
            extends TargetProcessor
    {
        public MyTargetProc() {
            super(EntityTurretMinigun.this);
        }

        @Override
        public SoundEvent getShootSound() {
            return Sounds.SHOOT_MINIGUN;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }
    }
}
